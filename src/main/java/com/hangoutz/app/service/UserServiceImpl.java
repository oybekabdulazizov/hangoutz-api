package com.hangoutz.app.service;

import com.hangoutz.app.dto.UpdateUserDTO;
import com.hangoutz.app.dto.UserDTO;
import com.hangoutz.app.exception.AuthException;
import com.hangoutz.app.exception.BadRequestException;
import com.hangoutz.app.exception.ExceptionMessage;
import com.hangoutz.app.exception.NotFoundException;
import com.hangoutz.app.mappers.UserMapper;
import com.hangoutz.app.model.Role;
import com.hangoutz.app.model.User;
import com.hangoutz.app.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.hangoutz.app.service.UtilService.checkEmailIsValid;
import static com.hangoutz.app.service.UtilService.getMapFromObject;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtService jwtService;


    @Override
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream().map(userMapper::toDto).toList();
    }

    @Override
    public UserDTO findById(String id) {
        return userMapper.toDto(getByIdIfUserExists(id));
    }

    @Override
    public UserDTO findByEmail(String email) {
        return userMapper.toDto(getByUsernameIfUserExists(email));
    }

    @Override
    @Transactional
    public void delete(String id) {
        userRepository.delete(getByIdIfUserExists(id));
    }

    @Override
    @Transactional
    public UserDTO update(String bearerToken, String id, UpdateUserDTO updatedUserDTO) {
        String currentUserUsername = jwtService.extractUsername(jwtService.extractJwt(bearerToken));
        User currentUser = getByUsernameIfUserExists(currentUserUsername);
        User userToBeUpdated = getByIdIfUserExists(id);

        // not allowed unless the requester is admin or the user account owner
        if (currentUser.getRole() != Role.ROLE_ADMIN && !currentUser.getId().equals(userToBeUpdated.getId())) {
            throw new AuthException(ExceptionMessage.PERMISSION_DENIED);
        }

        Map<String, Object> updatedFields = getMapFromObject(updatedUserDTO);
        updatedFields.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(User.class, key);
            assert field != null;
            field.setAccessible(true);
            if (key.equals("dateOfBirth")) {
                ReflectionUtils.setField(field, userToBeUpdated, LocalDate.parse(value.toString()));
            } else if (key.equals("email")) {
                checkEmailIsValid(value.toString());
                if (userRepository.findByEmail(value.toString()).isPresent()) {
                    throw new BadRequestException(ExceptionMessage.EMAIL_TAKEN);
                }
                ReflectionUtils.setField(field, userToBeUpdated, value);
            } else {
                ReflectionUtils.setField(field, userToBeUpdated, value);
            }
            userToBeUpdated.setLastModifiedAt(LocalDateTime.now());
        });
        return userMapper.toDto(userRepository.save(userToBeUpdated));
    }

    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return userRepository.findByEmail(username).orElse(null);
            }
        };
    }

    private User getByUsernameIfUserExists(String username) {
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isEmpty()) throw new NotFoundException(ExceptionMessage.USER_NOT_FOUND);
        return user.get();
    }

    private User getByIdIfUserExists(String id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) throw new NotFoundException(ExceptionMessage.USER_NOT_FOUND);
        return user.get();
    }
}
