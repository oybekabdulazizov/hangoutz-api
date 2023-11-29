package com.hangoutz.app.controller;

import com.hangoutz.app.model.Event;
import com.hangoutz.app.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/events")
    public List<Event> findAll() {
        return eventService.findAll();
    }

    @GetMapping("/events/{eventId}")
    public Event findById(@PathVariable String eventId) {
        return eventService.findById(eventId);
    }

    @PostMapping("/events")
    public Event create(@RequestBody Event newEvent) {
        eventService.save(newEvent);
        return newEvent;
    }

    @PutMapping("/events/{eventId}")
    public Event update(@PathVariable String eventId, @RequestBody Map<Object, Object> fields) {
        Event event = eventService.findById(eventId);
        fields.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(Event.class, (String) key);
            if (field != null) {
                field.setAccessible(true);
                ReflectionUtils.setField(field, event, value);
            }
        });
        eventService.update(event);
        return event;
    }

    @DeleteMapping("/events/{eventId}")
    public String delete(@PathVariable String eventId) {
        eventService.delete(eventId);
        return "Deleted the event with id " + eventId;
    }
}
