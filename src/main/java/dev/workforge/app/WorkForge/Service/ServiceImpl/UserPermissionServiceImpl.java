package dev.workforge.app.WorkForge.Service.ServiceImpl;

import dev.workforge.app.WorkForge.DTO.UserDTO;
import dev.workforge.app.WorkForge.Model.Permission;
import dev.workforge.app.WorkForge.Repository.UserPermissionProjection;
import dev.workforge.app.WorkForge.Repository.UserPermissionRepository;
import dev.workforge.app.WorkForge.Security.SecurityUser;
import dev.workforge.app.WorkForge.Service.UserPermissionService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserPermissionServiceImpl implements UserPermissionService {

    private final UserPermissionRepository userPermissionRepository;

    public UserPermissionServiceImpl(UserPermissionRepository userPermissionRepository) {
        this.userPermissionRepository = userPermissionRepository;
    }

    @Override
    public void transferPermissions(UserDetails userDetails) {
        List<UserPermissionProjection> userPermission = userPermissionRepository.findPermissionsByUser(userDetails.getUsername());
        transferPermission(userDetails, userPermission);
    }

    private void transferPermission(UserDetails userDetails, List<UserPermissionProjection> userPermissionList) {
        for (UserPermissionProjection userPermission : userPermissionList) {
            ((SecurityUser) userDetails).addPermission(userPermission.getProjectKey(), userPermission.getPermission());
        }
    }
}
