package com.hangoutz.app.service;

import com.hangoutz.app.dao.EventDAO;
import com.hangoutz.app.exception.NotFoundException;
import com.hangoutz.app.model.Event;
import com.hangoutz.app.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
    public Event save(String bearerToken, Event newEvent) {
        String jwt = jwtService.extractJwt(bearerToken);
        if (jwtService.isTokenExpired(jwt)) {
            throw new BadCredentialsException("Provided token either expired or is invalid");
        }

        User host = userService.findByEmail(jwtService.extractUsername(jwt));

        host.hostEvent(newEvent);
        newEvent.setHost(host);
        newEvent.addAttendee(host);
        eventDAO.save(newEvent);

        return newEvent;
    }

    @Override
    @Transactional
    public void delete(String bearerToken, String id) {
        String jwt = jwtService.extractJwt(bearerToken);
        Event event = findById(id);
        if (!jwtService.isTokenValid(jwt, event.getHost())) {
            throw new BadCredentialsException("Invalid token");
        }

        eventDAO.delete(event);
    }

    @Override
    @Transactional
    public Event update(String bearerToken, String id, Map<Object, Object> updatedFields) {
        String jwt = jwtService.extractJwt(bearerToken);
        String username = jwtService.extractUsername(jwt);

        User user = userService.findByEmail(username);
        Event event = findById(id);

        if (!jwtService.isTokenValid(jwt, user) || !event.getHost().getId().equals(user.getId())) {
            throw new BadCredentialsException("Invalid token");
        }

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
}
