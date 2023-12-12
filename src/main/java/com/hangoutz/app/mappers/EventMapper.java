package com.hangoutz.app.mappers;

import com.hangoutz.app.dto.DisplayEventDTO;
import com.hangoutz.app.dto.NewEventDTO;
import com.hangoutz.app.model.Event;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventMapper {

    public DisplayEventDTO toDto(Event event, DisplayEventDTO displayEventDTO) {
        new ModelMapper()
                .typeMap(Event.class, DisplayEventDTO.class)
                /*.addMappings(mapper -> {
                    mapper.map(src -> src.getHost().getId(), DisplayEventDTO::setHostUserId);
                })*/
                .map(event, displayEventDTO);
        return displayEventDTO;
    }

    public Event toModel(NewEventDTO newEventDTO) {
        return new ModelMapper().map(newEventDTO, Event.class);
    }
}
