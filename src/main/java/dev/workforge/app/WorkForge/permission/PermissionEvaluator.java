package dev.workforge.app.WorkForge.permission;

import dev.workforge.app.WorkForge.model.PermissionType;
import dev.workforge.app.WorkForge.security.UserPrincipal;
import dev.workforge.app.WorkForge.service.other.AccessControlService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("permissionEvaluator")
public class PermissionEvaluator {

    private final AccessControlService accessControlService;

    public PermissionEvaluator(AccessControlService accessControlService) {
        this.accessControlService = accessControlService;
    }

    public boolean hasPermissionOnProject(long projectId, List<PermissionType> permissionType) {
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean hasPermission = false;
        if (user instanceof final UserPrincipal userPrincipal) {
           hasPermission = accessControlService.hasPermissions((long) projectId, permissionType, userPrincipal.getUsername());

        }
        return hasPermission;
    }
}
