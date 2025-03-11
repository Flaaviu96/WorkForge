package dev.workforge.app.WorkForge.Service.ServiceImpl;

import dev.workforge.app.WorkForge.Model.Permission;
import dev.workforge.app.WorkForge.Model.PermissionType;
import dev.workforge.app.WorkForge.Repository.UserPermissionProjection;
import dev.workforge.app.WorkForge.Repository.UserPermissionRepository;
import dev.workforge.app.WorkForge.Security.SecurityUser;
import dev.workforge.app.WorkForge.Service.UserPermissionService;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public void loadUserPermissions(UserDetails userDetails) {
        List<UserPermissionProjection> userPermission = userPermissionRepository.findPermissionsByUser(userDetails.getUsername());
        addPermissionsToUser(userDetails, userPermission);
    }

    private void addPermissionsToUser(UserDetails userDetails, List<UserPermissionProjection> userPermissionList) {
        for (UserPermissionProjection userPermission : userPermissionList) {
            ((SecurityUser) userDetails).addPermission(userPermission.getProjectId(), userPermission.getPermissions());
        }
    }

    public List<Long> extractProjectIdsFromSecurityContext() {
        Map<Long, Set<Permission>> list = ((SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPermissionMap();
        List<Long> projectIds = new ArrayList<>();
        for(Long key : list.keySet()) {
            if (list.get(key).contains(new Permission().setPermissionType(PermissionType.READ))) {

            }
        }
    }
}
