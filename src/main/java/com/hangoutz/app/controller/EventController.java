package com.hangoutz.app.controller;

import com.hangoutz.app.dto.EventDTO;
import com.hangoutz.app.mappers.EventMapper;
import com.hangoutz.app.model.Event;
import com.hangoutz.app.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class EventController {

    private final EventService eventService;
    private final EventMapper eventMapper;


    @GetMapping("/events")
    public ResponseEntity<List<EventDTO>> findAll() {
        List<EventDTO> events = eventService.findAll().stream()
                                            .map((event) -> eventMapper.modelToDto(event)).toList();
        HttpStatus httpStatus = events.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK;
        return new ResponseEntity<>(events, httpStatus);
    }


    @GetMapping("/events/{id}")
    public ResponseEntity<EventDTO> findById(@PathVariable String id) {
        return new ResponseEntity<>(eventMapper.modelToDto(eventService.findById(id)), HttpStatus.OK);
    }


    @PostMapping("/events")
    public ResponseEntity<EventDTO> create(
            @RequestHeader(name = "Authorization") String jwt,
            @Valid @RequestBody EventDTO newEventDTO
    ) {
        Event savedEvent = eventService.save(jwt, eventMapper.dtoToModel(newEventDTO));
        return new ResponseEntity<>(eventMapper.modelToDto(savedEvent), HttpStatus.CREATED);
    }


    @PutMapping("/events/{id}")
    public ResponseEntity<EventDTO> update(
            @PathVariable String id,
            @RequestBody Map<Object, Object> updatedFields
    ) {
        return new ResponseEntity<>(eventMapper.modelToDto(eventService.update(id, updatedFields)), HttpStatus.OK);
    }


    @DeleteMapping("/events/{eventId}")
    @ResponseStatus(value = HttpStatus.OK)
    public void delete(@PathVariable String eventId) {
        eventService.delete(eventId);
    }
}
