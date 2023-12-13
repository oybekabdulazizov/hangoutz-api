package com.hangoutz.app.controller;

import com.hangoutz.app.dto.EventDTO;
import com.hangoutz.app.dto.NewEventDTO;
import com.hangoutz.app.mappers.EventMapper;
import com.hangoutz.app.model.Event;
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
    private final EventMapper eventMapper;


    @GetMapping("/events")
    public ResponseEntity<List<EventDTO>> findAll() {
        List<EventDTO> events = eventService
                .findAll().stream()
                .map((event) -> eventMapper.toDto(event, new EventDTO())).toList();
        HttpStatus httpStatus = events.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK;
        return new ResponseEntity<>(events, httpStatus);
    }


    @GetMapping("/events/{id}")
    public ResponseEntity<EventDTO> findById(@PathVariable String id) {
        return new ResponseEntity<>(eventMapper.toDto(eventService.findById(id), new EventDTO()), HttpStatus.OK);
    }


    @PostMapping("/events")
    public ResponseEntity<EventDTO> create(
            @RequestHeader(name = "Authorization") String bearerToken,
            @Valid @RequestBody NewEventDTO newEventDTO
    ) {
        Event savedEvent = eventService.save(bearerToken, eventMapper.newDtoToModel(newEventDTO));
        return new ResponseEntity<>(eventMapper.toDto(savedEvent, new EventDTO()), HttpStatus.CREATED);
    }


    @PostMapping("/events/{id}/attend")
    public ResponseEntity<EventDTO> attend(
            @RequestHeader(name = "Authorization") String bearerToken,
            @PathVariable String id
    ) throws BadRequestException {
        Event updatedEvent = eventService.attend(bearerToken, id);
        return new ResponseEntity<>(eventMapper.toDto(updatedEvent, new EventDTO()), HttpStatus.OK);
    }


    @PutMapping("/events/{id}")
    public ResponseEntity<EventDTO> update(
            @RequestHeader(name = "Authorization") String bearerToken,
            @PathVariable String id,
            @RequestBody Map<Object, Object> updatedFields
    ) {
        Event updatedEvent = eventService.update(bearerToken, id, updatedFields);
        return new ResponseEntity<>(eventMapper.toDto(updatedEvent, new EventDTO()), HttpStatus.OK);
    }


    @DeleteMapping("/events/{eventId}")
    @ResponseStatus(value = HttpStatus.OK)
    public void delete(@RequestHeader(name = "Authorization") String bearerToken, @PathVariable String eventId) {
        eventService.delete(bearerToken, eventId);
    }
}
