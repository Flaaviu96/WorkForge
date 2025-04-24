package dev.workforge.app.WorkForge.Mapper;

import dev.workforge.app.WorkForge.DTO.StateDTO;
import dev.workforge.app.WorkForge.Model.State;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface StateMapper {
    StateMapper INSTANCE = Mappers.getMapper(StateMapper.class);

    StateDTO toDTO(State state);
}
