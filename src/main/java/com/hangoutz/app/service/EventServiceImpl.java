package com.hangoutz.app.service;

import com.hangoutz.app.dao.CategoryDAO;
import com.hangoutz.app.dao.EventDAO;
import com.hangoutz.app.dao.UserDAO;
import com.hangoutz.app.dto.EventDTO;
import com.hangoutz.app.dto.NewEventDTO;
import com.hangoutz.app.exception.NotFoundException;
import com.hangoutz.app.mappers.EventMapper;
import com.hangoutz.app.model.Category;
import com.hangoutz.app.model.Event;
import com.hangoutz.app.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class EventServiceImpl implements EventService {

    private final EventDAO eventDAO;
    private final JwtService jwtService;
    private final UserDAO userDAO;
    private final CategoryDAO categoryDAO;
    private final EventMapper eventMapper;

    @Override
    public List<EventDTO> findAll() {
        List<EventDTO> events = eventDAO
                .findAll().stream()
                .map((event) -> eventMapper.toDto(event, new EventDTO())).toList();
        return events;
    }

    @Override
    public EventDTO findById(String id) {
        Event existingCategory = checkByIdIfEventExists(id);
        return eventMapper.toDto(existingCategory, new EventDTO());
    }

    @Override
    @Transactional
    public EventDTO create(String bearerToken, NewEventDTO newEventDTO) throws BadRequestException {
        User currentUser = getCurrentUser(bearerToken);
        checkTokenValidity(jwtService.extractJwt(bearerToken), currentUser);

        Category category = checkByNameIfCategoryExists(newEventDTO.getCategory());
        checkByNameIfCategoryExists(category.getName());

        Event newEvent = eventMapper.newDtoToModel(newEventDTO);
        newEvent.setCategory(category);
        newEvent.setHost(currentUser);
        newEvent.addAttendee(currentUser);
        return eventMapper.toDto(eventDAO.save(newEvent), new EventDTO());
    }

    @Override
    @Transactional
    public void delete(String bearerToken, String id) {
        Event event = checkByIdIfEventExists(id);
        checkTokenValidity(jwtService.extractJwt(bearerToken), event.getHost());
        eventDAO.delete(event);
    }

    @Override
    @Transactional
    public EventDTO update(String bearerToken, String id, Map<Object, Object> updatedFields) {
        Event event = checkByIdIfEventExists(id);
        checkTokenValidity(jwtService.extractJwt(bearerToken), event.getHost());

        updatedFields.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(Event.class, (String) key);
            if (field != null && !(key.equals("id") || key.equals("hostUserId"))) {
                field.setAccessible(true);
                if (value == null || value.toString().isBlank()) {
                    throw new IllegalArgumentException(key + " is required");
                }
                if (key == "dateTime") {
                    DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    LocalDateTime ldt = LocalDateTime.parse(value.toString(), dateTimeFormat);
                    ReflectionUtils.setField(field, event, ldt);
                } else if (key == "category") {
                    Category category = categoryDAO.findByName(value.toString().toLowerCase());
                    try {
                        checkByNameIfCategoryExists(category.getName());
                    } catch (BadRequestException e) {
                        throw new RuntimeException(e);
                    }
                    ReflectionUtils.setField(field, event, category);
                } else {
                    ReflectionUtils.setField(field, event, value);
                }
            }
        });
        return eventMapper.toDto(eventDAO.update(event), new EventDTO());
    }

    @Override
    @Transactional
    public EventDTO attend(String bearerToken, String id) throws BadRequestException {
        User currentUser = getCurrentUser(bearerToken);
        Event event = checkByIdIfEventExists(id);

        // this works like a toggle:
        //  - if user is attending, it cancels the attendance
        //  - if user is not in the attendees list, they get added into it
        //  - hosts' are not allowed cancel attendance to an event
        if (event.getAttendees().contains(currentUser)) {
            if (currentUser.getId().equals(event.getHost().getId())) {
                throw new BadRequestException("You, as the host, must be present at the event. Cancelling can be an option");
            }
            event.removeAttendee(currentUser);
        } else {
            event.addAttendee(currentUser);
        }

        return eventMapper.toDto(eventDAO.save(event), new EventDTO());
    }

    @Override
    @Transactional
    public EventDTO cancel(String bearerToken, String id) {
        Event event = checkByIdIfEventExists(id);
        checkTokenValidity(jwtService.extractJwt(bearerToken), event.getHost());

        event.setCancelled(!event.isCancelled());
        return eventMapper.toDto(eventDAO.save(event), new EventDTO());
    }

    private Category checkByNameIfCategoryExists(String name) throws BadRequestException {
        Category category = categoryDAO.findByName(name.toLowerCase());
        if (category == null) {
            throw new BadRequestException("Category not found. You may wanna use 'other'");
        }
        return category;
    }

    private Event checkByIdIfEventExists(String id) {
        Event event = eventDAO.findById(id);
        if (event == null) {
            throw new NotFoundException("Event not found");
        }
        return event;
    }

    private User getCurrentUser(String bearerToken) {
        String jwt = jwtService.extractJwt(bearerToken);
        User user = checkByUsernameIfUserExists(jwtService.extractUsername(jwt));
        return user;
    }

    private User checkByUsernameIfUserExists(String username) {
        User user = userDAO.findByEmail(username);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        return user;
    }

    private void checkTokenValidity(String jwt, User user) {
        if (!jwtService.isTokenValid(jwt, user)) {
            throw new BadCredentialsException("Invalid token. You are not authorized to perform this operation");
        }
    }
}
