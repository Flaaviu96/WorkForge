package dev.workforge.app.WorkForge.Mapper;

import dev.workforge.app.WorkForge.DTO.StateTransitionDTO;
import dev.workforge.app.WorkForge.Model.StateTransition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = { StateMapper.class })
public interface StateTransitionMapper {

    StateTransitionMapper INSTANCE = Mappers.getMapper(StateTransitionMapper.class);

    @Mapping(target = "fromStateDTO", source = "fromState")
    @Mapping(target = "toStateDTO", source = "toState")
    StateTransitionDTO toDTO(StateTransition stateTransition);

    List<StateTransitionDTO> toDTOWithList(List<StateTransition> stateTransitionList);
}