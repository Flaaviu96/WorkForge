package dev.workforge.app.WorkForge.controllers;

import dev.workforge.app.WorkForge.dto.ProjectPermissionsDTO;
import dev.workforge.app.WorkForge.service.user_permission.UserPermissionService;
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
        userPermissionService.manageProjectPermissionsForUsers(projectPermissionsDTO);
    }
}
