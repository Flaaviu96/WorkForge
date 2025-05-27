package dev.workforge.app.WorkForge.Mapper;

import dev.workforge.app.WorkForge.DTO.UserViewDTO;
import dev.workforge.app.WorkForge.Model.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "uuid", source = "uuid")
    @Mapping(target = "username", source = "username")
    UserViewDTO toDTO(AppUser appUser);


    List<UserViewDTO> toDTOList(List<AppUser> appUsers);
}
