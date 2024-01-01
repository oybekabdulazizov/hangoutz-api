package com.hangoutz.app.service;

import com.hangoutz.app.dto.EventDTO;
import com.hangoutz.app.dto.NewEventDTO;
import com.hangoutz.app.exception.AuthException;
import com.hangoutz.app.exception.BadRequestException;
import com.hangoutz.app.exception.ExceptionMessage;
import com.hangoutz.app.exception.NotFoundException;
import com.hangoutz.app.mappers.EventMapper;
import com.hangoutz.app.model.Category;
import com.hangoutz.app.model.Event;
import com.hangoutz.app.model.User;
import com.hangoutz.app.repository.CategoryRepository;
import com.hangoutz.app.repository.EventRepository;
import com.hangoutz.app.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;

    @Override
    public List<EventDTO> findAll() {
        return eventRepository.findAll().stream().map(eventMapper::toDto).toList();
    }

    @Override
    public EventDTO findById(String id) {
        return eventMapper.toDto(getByIdIfEventExists(id));
    }

    @Override
    @Transactional
    public EventDTO create(String bearerToken, NewEventDTO newEventDTO) {
        User currentUser = getCurrentUser(bearerToken);
        Category category = getByNameIfCategoryExists(newEventDTO.getCategory());
        Event newEvent = eventMapper.toModel(newEventDTO);

        newEvent.setCategory(category);
        newEvent.setHost(currentUser);
        newEvent.addAttendee(currentUser);

        return eventMapper.toDto(eventRepository.save(newEvent));
    }

    @Override
    @Transactional
    public void delete(String bearerToken, String id) {
        Event event = getByIdIfEventExists(id);
        checkTokenValidity(jwtService.extractJwt(bearerToken), event.getHost());
        eventRepository.delete(event);
    }

    @Override
    @Transactional
    public EventDTO update(String bearerToken, String id, Map<Object, Object> updatedFields) {
        Event event = getByIdIfEventExists(id);
        checkTokenValidity(jwtService.extractJwt(bearerToken), event.getHost());

        updatedFields.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(Event.class, (String) key);
            if (field != null && !(key.equals("id") || key.equals("hostUserId"))) {
                field.setAccessible(true);
                if (value == null || value.toString().isBlank()) {
                    throw new BadRequestException(key + " is required");
                }
                if (key == "dateTime") {
                    ReflectionUtils.setField(field, event, LocalDateTime.parse(value.toString()));
                } else if (key == "category") {
                    Category category = getByNameIfCategoryExists(value.toString().toLowerCase());
                    ReflectionUtils.setField(field, event, category);
                } else {
                    ReflectionUtils.setField(field, event, value);
                }
            }
        });
        return eventMapper.toDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventDTO attend(String bearerToken, String id) {
        User currentUser = getCurrentUser(bearerToken);
        Event event = getByIdIfEventExists(id);

        // this works like a toggle for attendees:
        //  - if user is attending, it cancels the attendance
        //  - if user is not in the attendees list, they get added into it
        //
        //  PS. hosts' are not allowed to cancel attendance to an event
        if (event.getAttendees().contains(currentUser)) {
            if (currentUser.getId().equals(event.getHost().getId())) {
                throw new BadRequestException(ExceptionMessage.HOST_MUST_BE_PRESENT);
            }
            event.removeAttendee(currentUser);
        } else {
            event.addAttendee(currentUser);
        }

        return eventMapper.toDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventDTO cancel(String bearerToken, String id) {
        Event event = getByIdIfEventExists(id);
        checkTokenValidity(jwtService.extractJwt(bearerToken), event.getHost());

        event.setCancelled(!event.isCancelled());
        return eventMapper.toDto(eventRepository.save(event));
    }


    private Category getByNameIfCategoryExists(String name) {
        Optional<Category> category = categoryRepository.findByName(name.toLowerCase());
        if (category.isEmpty()) throw new NotFoundException(ExceptionMessage.CATEGORY_NOT_FOUND);
        return category.get();
    }

    private Event getByIdIfEventExists(String id) {
        Optional<Event> event = eventRepository.findById(id);
        if (event.isEmpty()) throw new NotFoundException(ExceptionMessage.EVENT_NOT_FOUND);
        return event.get();
    }

    private User getCurrentUser(String bearerToken) {
        String jwt = jwtService.extractJwt(bearerToken);
        return checkByUsernameIfUserExists(jwtService.extractUsername(jwt));
    }

    private User checkByUsernameIfUserExists(String username) {
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isEmpty()) throw new NotFoundException(ExceptionMessage.USER_NOT_FOUND);
        return user.get();
    }

    private void checkTokenValidity(String jwt, User user) {
        if (!jwtService.isTokenValid(jwt, user))
            throw new AuthException(ExceptionMessage.PERMISSION_DENIED);
    }
}
