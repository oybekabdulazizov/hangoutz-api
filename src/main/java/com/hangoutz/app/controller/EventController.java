package com.hangoutz.app.controller;

import com.hangoutz.app.dto.DisplayEventDTO;
import com.hangoutz.app.dto.NewEventDTO;
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
    public ResponseEntity<List<DisplayEventDTO>> findAll() {
        List<DisplayEventDTO> events = eventService
                .findAll().stream()
                .map((event) -> eventMapper.toDisplayDTO(event)).toList();
        HttpStatus httpStatus = events.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK;
        return new ResponseEntity<>(events, httpStatus);
    }


    @GetMapping("/events/{id}")
    public ResponseEntity<DisplayEventDTO> findById(@PathVariable String id) {
        return new ResponseEntity<>(eventMapper.toDisplayDTO(eventService.findById(id)), HttpStatus.OK);
    }


    @PostMapping("/events")
    public ResponseEntity<DisplayEventDTO> create(
            @RequestHeader(name = "Authorization") String jwt,
            @Valid @RequestBody NewEventDTO newEventDTO
    ) {
        Event savedEvent = eventService.save(jwt, eventMapper.toModel(newEventDTO));
        return new ResponseEntity<>(eventMapper.toDisplayDTO(savedEvent), HttpStatus.CREATED);
    }


    @PutMapping("/events/{id}")
    public ResponseEntity<DisplayEventDTO> update(
            @PathVariable String id,
            @RequestBody Map<Object, Object> updatedFields
    ) {
        return new ResponseEntity<>(eventMapper.toDisplayDTO(eventService.update(id, updatedFields)), HttpStatus.OK);
    }


    @DeleteMapping("/events/{eventId}")
    @ResponseStatus(value = HttpStatus.OK)
    public void delete(@PathVariable String eventId) {
        eventService.delete(eventId);
    }
}
