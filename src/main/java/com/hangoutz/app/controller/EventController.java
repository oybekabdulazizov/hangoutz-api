package com.hangoutz.app.controller;

import com.hangoutz.app.model.Event;
import com.hangoutz.app.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @DeleteMapping("/events/{eventId}")
    public String delete(@PathVariable String eventId) {
        eventService.delete(eventId);
        return "Deleted the event with id " + eventId;
    }
}
