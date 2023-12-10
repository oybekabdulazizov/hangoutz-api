package com.hangoutz.app.controller;

import com.hangoutz.app.dto.EventDTO;
import com.hangoutz.app.mappers.EventMapper;
import com.hangoutz.app.model.Event;
import com.hangoutz.app.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class EventController {

    private final EventService eventService;
    private final EventMapper eventMapper;


    @GetMapping("/events")
    public ResponseEntity<Collection<EventDTO>> findAll() {
        List<Event> events = eventService.findAll();
        if (events.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }
        List<EventDTO> eventsDTO = new ArrayList<>();
        events.forEach((event) -> {
            eventsDTO.add(eventMapper.modelToDto(event));
        });
        return new ResponseEntity<>(eventsDTO, HttpStatus.OK);
    }


    @GetMapping("/events/{eventId}")
    public ResponseEntity<EventDTO> findById(@PathVariable String eventId) {
        Event event = eventService.findById(eventId);
        return new ResponseEntity<>(eventMapper.modelToDto(event), HttpStatus.OK);
    }


    @PostMapping("/events")
    public ResponseEntity<EventDTO> create(@Valid @RequestBody EventDTO newEventDTO) {
        Event newEvent = eventMapper.dtoToModel(newEventDTO);
        eventService.save(newEvent);
        return new ResponseEntity<>(eventMapper.modelToDto(newEvent), HttpStatus.CREATED);
    }


    @PutMapping("/events/{eventId}")
    public ResponseEntity<EventDTO> update(@PathVariable String eventId, @RequestBody Map<Object, Object> fields) {
        Event event = eventService.findById(eventId);
        EventDTO eventDTO = eventMapper.modelToDto(event);
        fields.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(EventDTO.class, (String) key);
            if (field != null && !key.equals("id")) {
                field.setAccessible(true);
                if (value == null || value.toString().isBlank()) {
                    throw new IllegalArgumentException(key + " is required");
                }
                if (key == "dateTime") {
                    DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    LocalDateTime ldt = LocalDateTime.parse(value.toString(), dateTimeFormat);
                    ReflectionUtils.setField(field, eventDTO, ldt);
                } else {
                    ReflectionUtils.setField(field, eventDTO, value);
                }
            }
        });
        eventService.update(eventMapper.dtoToModel(eventDTO));
        return new ResponseEntity<>(eventMapper.modelToDto(event), HttpStatus.OK);
    }


    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<String> delete(@PathVariable String eventId) {
        Event event = eventService.findById(eventId);
        eventService.delete(event);
        return new ResponseEntity<>("Deleted the event with id " + eventId, HttpStatus.OK);
    }
}
