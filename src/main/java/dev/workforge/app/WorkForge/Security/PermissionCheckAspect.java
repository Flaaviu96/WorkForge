package dev.workforge.app.WorkForge.Security;

import dev.workforge.app.WorkForge.Model.Permission;
import dev.workforge.app.WorkForge.Model.PermissionType;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


import java.util.Map;
import java.util.Set;

@Aspect
@Component
public class PermissionCheckAspect {

    @Before("@annotation(permissionCheck) && args(projectId)")
    public void checkPermission(PermissionCheck permissionCheck, Long projectId) {
        Map<Long, Set<Permission>> permissions = retrievePermissionsFromSecurityContext();
        if (permissions.containsKey(projectId) && permissions.get(projectId).contains(new Permission().setPermissionType(PermissionType.READ))) {
            return;
        }
        throw new AccessDeniedException("User does not have permission to access the project"+ projectId);
    }

    private Map<Long, Set<Permission>> retrievePermissionsFromSecurityContext() {
        return ((SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPermissionMap();
    }
}
