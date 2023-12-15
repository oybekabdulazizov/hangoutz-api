package com.hangoutz.app.service;

import com.hangoutz.app.dao.EventDAO;
import com.hangoutz.app.dto.NewEventDTO;
import com.hangoutz.app.exception.NotFoundException;
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
    private final UserService userService;
    private final CategoryService categoryService;

    @Override
    public List<Event> findAll() {
        return eventDAO.findAll();
    }

    @Override
    public Event findById(String id) {
        Event event = eventDAO.findById(id);
        if (event == null) {
            throw new NotFoundException("Event not found");
        }
        return event;
    }

    @Override
    @Transactional
    public Event save(String bearerToken, NewEventDTO newEventDTO) throws BadRequestException {
        User currentUser = getCurrentUser(bearerToken);
        checkTokenValidity(jwtService.extractJwt(bearerToken), currentUser);

        Category category = categoryService.findByName(newEventDTO.getCategory().toLowerCase());
        if (category == null) {
            throw new BadRequestException("Category not found. You may wanna use 'other'");
        }


        Event newEvent = Event.builder()
                              .title(newEventDTO.getTitle())
                              .city(newEventDTO.getCity())
                              .venue(newEventDTO.getVenue())
                              .category(category)
                              .dateTime(newEventDTO.getDateTime())
                              .host(currentUser)
                              .description(newEventDTO.getDescription())
                              .build();
        newEvent.addAttendee(currentUser);
        eventDAO.save(newEvent);
        System.out.println(category.getEvents());

        return newEvent;
    }

    @Override
    @Transactional
    public void delete(String bearerToken, String id) {
        Event event = findById(id);
        checkTokenValidity(jwtService.extractJwt(bearerToken), event.getHost());

        eventDAO.delete(event);
    }

    @Override
    @Transactional
    public Event update(String bearerToken, String id, Map<Object, Object> updatedFields) {
        Event event = findById(id);
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
                } else {
                    ReflectionUtils.setField(field, event, value);
                }
            }
        });
        return eventDAO.update(event);
    }

    @Override
    @Transactional
    public Event attend(String bearerToken, String id) throws BadRequestException {
        User currentUser = getCurrentUser(bearerToken);
        Event event = findById(id);

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

        eventDAO.save(event);
        return event;
    }

    @Override
    @Transactional
    public Event cancel(String bearerToken, String id) {
        Event event = findById(id);
        checkTokenValidity(jwtService.extractJwt(bearerToken), event.getHost());

        event.setCancelled(!event.isCancelled());
        eventDAO.save(event);
        return event;
    }

    private User getCurrentUser(String bearerToken) {
        String jwt = jwtService.extractJwt(bearerToken);
        String currentUserUsername = jwtService.extractUsername(jwt);
        return userService.findByEmailAndHandle(currentUserUsername);
    }

    private void checkTokenValidity(String jwt, User user) {
        if (!jwtService.isTokenValid(jwt, user)) {
            throw new BadCredentialsException("Invalid token. You are not authorized to perform this operation");
        }
    }
}
