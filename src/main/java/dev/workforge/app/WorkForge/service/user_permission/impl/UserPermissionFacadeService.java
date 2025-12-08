package dev.workforge.app.WorkForge.service.user_permission.impl;

import dev.workforge.app.WorkForge.dto.ProjectPermissionsDTO;
import dev.workforge.app.WorkForge.model.UserPermission;
import org.springframework.stereotype.Service;

@Service
public class UserPermissionFacadeService {

    private final UserPermissionAssignmentService assignmentService;
    private final UserPermissionRemovalService removalService;

    public UserPermissionFacadeService(
            UserPermissionAssignmentService assignmentService,
            UserPermissionRemovalService removalService
    ) {
        this.assignmentService = assignmentService;
        this.removalService = removalService;
    }

    public void managePermissions(ProjectPermissionsDTO dto) {
        assignmentService.process(dto);
    }

    public void removePermissions(ProjectPermissionsDTO dto) {
        removalService.process(dto);
    }

    public void saveUserPermission(UserPermission userPermission) {
        assignmentService.save(userPermission);
    }
}
