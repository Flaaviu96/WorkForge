package dev.workforge.app.WorkForge.Service.ServiceImpl;

import dev.workforge.app.WorkForge.DTO.PermissionDTO;
import dev.workforge.app.WorkForge.Model.Permission;
import dev.workforge.app.WorkForge.Model.PermissionType;
import dev.workforge.app.WorkForge.Repository.PermissionRepository;
import dev.workforge.app.WorkForge.Service.PermissionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionServiceImpl(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public List<Permission> getPermissionsByPermissionType(List<PermissionDTO> permissionTypes) {
        if (permissionTypes.isEmpty()) {
            return List.of();
        }
        List<PermissionType> permissionTypeList = new ArrayList<>(permissionTypes.stream()
                .map(this::mapPermissionDTO)
                .collect(Collectors.toSet()));
        return permissionRepository.findPermissionByPermissionType(permissionTypeList);
    }

    private PermissionType mapPermissionDTO(PermissionDTO permissionDTO) {
        return permissionDTO.permissionType();
    }
}
