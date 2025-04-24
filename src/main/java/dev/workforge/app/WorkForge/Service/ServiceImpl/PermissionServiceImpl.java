package dev.workforge.app.WorkForge.Service.ServiceImpl;

import dev.workforge.app.WorkForge.DTO.PermissionDTO;
import dev.workforge.app.WorkForge.Exceptions.PermissionNotFoundException;
import dev.workforge.app.WorkForge.Model.Permission;
import dev.workforge.app.WorkForge.Model.PermissionType;
import dev.workforge.app.WorkForge.Repository.PermissionRepository;
import dev.workforge.app.WorkForge.Service.PermissionService;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionServiceImpl(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public List<Permission> getPermissionsByPermissionType(List<PermissionDTO> permissionTypes) {
        if (permissionTypes.isEmpty()) {
            return Collections.emptyList();
        }
        List<PermissionType> permissionTypeList = permissionTypes.stream()
                .map(this::mapPermissionDTO)
                .distinct()
                .toList();
        List<Permission> permissions = permissionRepository.findPermissionByPermissionType(permissionTypeList);
        if (permissions.isEmpty()) {
            throw new PermissionNotFoundException("The permission is not found");
        }
        return permissions;
    }

    private PermissionType mapPermissionDTO(PermissionDTO permissionDTO) {
        return permissionDTO.permissionType();
    }
}
