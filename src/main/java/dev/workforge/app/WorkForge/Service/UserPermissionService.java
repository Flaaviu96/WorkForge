package dev.workforge.app.WorkForge.Service;

import dev.workforge.app.WorkForge.DTO.UserDTO;
import dev.workforge.app.WorkForge.Repository.UserPermissionProjection;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UserPermissionService {
    public void transferPermissions(UserDetails userDetails);
}
