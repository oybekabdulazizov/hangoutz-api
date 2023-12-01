package com.hangoutz.app.mappers;

import com.hangoutz.app.dto.EventDTO;
import com.hangoutz.app.model.Event;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public EventDTO modelToDto(Event event) {
        return new ModelMapper().map(event, EventDTO.class);
    }

    public Event dtoToModel(EventDTO eventDTO) {
        return new ModelMapper().map(eventDTO, Event.class);
    }
}
