package dev.workforge.app.WorkForge.Controllers;

import dev.workforge.app.WorkForge.DTO.PermissionDTO;
import dev.workforge.app.WorkForge.DTO.ProjectPermissionsDTO;
import dev.workforge.app.WorkForge.Service.UserPermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/permissions")
public class PermissionController {

    private final UserPermissionService userPermissionService;

    public PermissionController(UserPermissionService userPermissionService) {
        this.userPermissionService = userPermissionService;
    }

    @PostMapping("/assign")
    public ResponseEntity<String> assignPermissionToUser(@RequestBody ProjectPermissionsDTO projectPermissionsDTO) {
        userPermissionService.assignProjectPermissionsForUsers(projectPermissionsDTO);
        return ResponseEntity.ok("Yes");
    }
}
