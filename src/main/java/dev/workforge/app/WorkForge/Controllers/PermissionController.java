package dev.workforge.app.WorkForge.Controllers;

import dev.workforge.app.WorkForge.DTO.ProjectPermissionsDTO;
import dev.workforge.app.WorkForge.Service.UserPermission.UserPermissionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/permissions")
public class PermissionController {

    private final UserPermissionService userPermissionService;

    public PermissionController(UserPermissionService userPermissionService) {
        this.userPermissionService = userPermissionService;
    }

    @PostMapping("/assign")
    public void assignPermissionToUser(@RequestBody ProjectPermissionsDTO projectPermissionsDTO) {
        userPermissionService.updateProjectPermissionsForUsers(projectPermissionsDTO);
    }
}
