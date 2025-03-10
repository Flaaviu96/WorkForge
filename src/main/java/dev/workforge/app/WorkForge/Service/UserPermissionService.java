package dev.workforge.app.WorkForge.Service;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserPermissionService {
     void transferPermissions(UserDetails userDetails);
}
