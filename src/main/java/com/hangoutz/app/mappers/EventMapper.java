package com.hangoutz.app.mappers;

import com.hangoutz.app.dto.EventDTO;
import com.hangoutz.app.dto.NewEventDTO;
import com.hangoutz.app.model.Event;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventMapper {

    public EventDTO toDto(Event event) {
        return new ModelMapper().map(event, EventDTO.class);
    }

    public Event toModel(NewEventDTO newEventDTO) {
        return new ModelMapper().map(newEventDTO, Event.class);
    }
}
