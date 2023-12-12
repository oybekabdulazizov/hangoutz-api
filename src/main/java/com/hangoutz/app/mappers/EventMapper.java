package com.hangoutz.app.mappers;

import com.hangoutz.app.dto.EventDTO;
import com.hangoutz.app.model.Event;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventMapper {

    public EventDTO modelToDto(Event event) {
        EventDTO eventDTO = new EventDTO();
        new ModelMapper()
                .typeMap(Event.class, EventDTO.class)
                .addMappings(mapper -> {
                    mapper.map(src -> src.getHost().getId(), EventDTO::setHostUserId);
                }).map(event, eventDTO);
        return eventDTO;
    }

    public Event dtoToModel(EventDTO eventDTO) {
        return new ModelMapper().map(eventDTO, Event.class);
    }
}
