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

    public EventDTO toDto(Event event, EventDTO eventDTO) {
        new ModelMapper()
                .typeMap(Event.class, EventDTO.class)
                /*.addMappings(mapper -> {
                    mapper.map(src -> src.getHost().getId(), EventDTO::setHostUserId);
                })*/
                .map(event, eventDTO);
        return eventDTO;
    }

    public Event newDtoToModel(NewEventDTO newEventDTO) {
        return new ModelMapper().map(newEventDTO, Event.class);
    }
}
