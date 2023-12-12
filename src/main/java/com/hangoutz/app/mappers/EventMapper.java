package com.hangoutz.app.mappers;

import com.hangoutz.app.dto.DisplayEventDTO;
import com.hangoutz.app.dto.NewEventDTO;
import com.hangoutz.app.model.Event;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventMapper {

    public DisplayEventDTO toDisplayDTO(Event event) {
        TypeMap<Event, DisplayEventDTO> typeMap = new ModelMapper()
                .createTypeMap(Event.class, DisplayEventDTO.class)
                .addMappings(mapper -> {
                    mapper.map(src -> src.getHost().getId(), DisplayEventDTO::setHostUserId);
                });
        return new ModelMapper().map(event, DisplayEventDTO.class);
    }

    public Event toModel(NewEventDTO newEventDTO) {
        return new ModelMapper().map(newEventDTO, Event.class);
    }
}
