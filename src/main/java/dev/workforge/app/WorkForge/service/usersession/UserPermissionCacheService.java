package dev.workforge.app.WorkForge.service.usersession;

import dev.workforge.app.WorkForge.security.UserPrincipal;

public interface UserPermissionCacheService {
     UserPrincipal getCachedPermissions(String username);
}
