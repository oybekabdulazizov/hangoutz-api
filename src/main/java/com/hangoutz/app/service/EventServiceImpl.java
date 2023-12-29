package com.hangoutz.app.service;

import com.hangoutz.app.dao.CategoryDAO;
import com.hangoutz.app.dao.EventDAO;
import com.hangoutz.app.dao.UserDAO;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
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
                .map((event) -> eventMapper.toDto(event)).toList();
        return events;
    }

    @Override
    public EventDTO findById(String id) {
        Event existingCategory = checkByIdIfEventExists(id);
        return eventMapper.toDto(existingCategory);
    }

    @Override
    @Transactional
    public EventDTO create(String bearerToken, NewEventDTO newEventDTO) {
        User currentUser = getCurrentUser(bearerToken);
        Category category = checkByNameIfCategoryExists(newEventDTO.getCategory());
        Event newEvent = eventMapper.toModel(newEventDTO);

        newEvent.setCategory(category);
        newEvent.setHost(currentUser);
        newEvent.addAttendee(currentUser);

        return eventMapper.toDto(eventDAO.save(newEvent));
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
                    throw new BadRequestException(key + " is required");
                }
                if (key == "dateTime") {
                    ReflectionUtils.setField(field, event, LocalDateTime.parse(value.toString()));
                } else if (key == "category") {
                    Category category = checkByNameIfCategoryExists(value.toString().toLowerCase());
                    ReflectionUtils.setField(field, event, category);
                } else {
                    ReflectionUtils.setField(field, event, value);
                }
            }
        });
        return eventMapper.toDto(eventDAO.update(event));
    }

    @Override
    @Transactional
    public EventDTO attend(String bearerToken, String id) {
        User currentUser = getCurrentUser(bearerToken);
        Event event = checkByIdIfEventExists(id);

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

        return eventMapper.toDto(eventDAO.save(event));
    }

    @Override
    @Transactional
    public EventDTO cancel(String bearerToken, String id) {
        Event event = checkByIdIfEventExists(id);
        checkTokenValidity(jwtService.extractJwt(bearerToken), event.getHost());

        event.setCancelled(!event.isCancelled());
        return eventMapper.toDto(eventDAO.save(event));
    }


    private Category checkByNameIfCategoryExists(String name) {
        Category category = categoryDAO.findByName(name.toLowerCase());
        if (category == null) throw new NotFoundException(ExceptionMessage.CATEGORY_NOT_FOUND);

        return category;
    }

    private Event checkByIdIfEventExists(String id) {
        Event event = eventDAO.findById(id);
        if (event == null) throw new NotFoundException(ExceptionMessage.EVENT_NOT_FOUND);
        return event;
    }

    private User getCurrentUser(String bearerToken) {
        String jwt = jwtService.extractJwt(bearerToken);
        User user = checkByUsernameIfUserExists(jwtService.extractUsername(jwt));
        return user;
    }

    private User checkByUsernameIfUserExists(String username) {
        User user = userDAO.findByEmail(username);
        if (user == null) throw new NotFoundException(ExceptionMessage.USER_NOT_FOUND);
        return user;
    }

    private void checkTokenValidity(String jwt, User user) {
        if (!jwtService.isTokenValid(jwt, user))
            throw new AuthException(ExceptionMessage.PERMISSION_DENIED);
    }
}
