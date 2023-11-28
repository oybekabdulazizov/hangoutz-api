package com.hangoutz.app.controller;

import com.hangoutz.app.model.Event;
import com.hangoutz.app.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
