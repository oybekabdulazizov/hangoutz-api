package com.hangoutz.app.controller;

import com.hangoutz.app.dto.EventDTO;
import com.hangoutz.app.dto.NewEventDTO;
import com.hangoutz.app.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
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


    @GetMapping("/events")
    public ResponseEntity<List<EventDTO>> findAll() {
        List<EventDTO> events = eventService.findAll();
        return events.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(events);
    }


    @GetMapping("/events/{id}")
    public ResponseEntity<EventDTO> findById(@PathVariable String id) {
        return new ResponseEntity<>(eventService.findById(id), HttpStatus.OK);
    }


    @PostMapping("/events")
    public ResponseEntity<EventDTO> create(
            @RequestHeader(name = "Authorization") String bearerToken,
            @Valid @RequestBody NewEventDTO newEventDTO
    ) throws BadRequestException {
        return new ResponseEntity<>(eventService.create(bearerToken, newEventDTO), HttpStatus.CREATED);
    }


    @PostMapping("/events/{id}/attend")
    public ResponseEntity<EventDTO> attend(
            @RequestHeader(name = "Authorization") String bearerToken,
            @PathVariable String id
    ) throws BadRequestException {
        return new ResponseEntity<>(eventService.attend(bearerToken, id), HttpStatus.OK);
    }

    @PostMapping("/events/{id}/cancel")
    public ResponseEntity<EventDTO> cancel(
            @RequestHeader(name = "Authorization") String bearerToken,
            @PathVariable String id
    ) {
        return new ResponseEntity<>(eventService.cancel(bearerToken, id), HttpStatus.OK);
    }


    @PutMapping("/events/{id}")
    public ResponseEntity<EventDTO> update(
            @RequestHeader(name = "Authorization") String bearerToken,
            @PathVariable String id,
            @RequestBody Map<Object, Object> updatedFields
    ) throws BadRequestException {
        return new ResponseEntity<>(eventService.update(bearerToken, id, updatedFields), HttpStatus.OK);
    }


    @DeleteMapping("/events/{eventId}")
    @ResponseStatus(value = HttpStatus.OK)
    public void delete(@RequestHeader(name = "Authorization") String bearerToken, @PathVariable String eventId) {
        eventService.delete(bearerToken, eventId);
    }
}
