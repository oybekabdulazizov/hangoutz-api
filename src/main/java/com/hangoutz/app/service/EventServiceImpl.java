package com.hangoutz.app.service;

import com.hangoutz.app.dao.EventDAO;
import com.hangoutz.app.exception.NotFoundException;
import com.hangoutz.app.model.Event;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
    public Event save(Event newEvent) {
        eventDAO.save(newEvent);
        return newEvent;
    }

    @Override
    @Transactional
    public void delete(String id) {
        Event event = findById(id);
        eventDAO.delete(event);
    }

    @Override
    @Transactional
    public Event update(String id, Map<Object, Object> updatedFields) {
        Event event = findById(id);
        updatedFields.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(Event.class, (String) key);
            if (field != null && !key.equals("id")) {
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
