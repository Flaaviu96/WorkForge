package dev.workforge.app.WorkForge;

import dev.workforge.app.WorkForge.Model.AppUser;
import dev.workforge.app.WorkForge.Repository.UserPermissionProjection;
import dev.workforge.app.WorkForge.Security.SecurityImpl.SecurityUserImpl;
import dev.workforge.app.WorkForge.Security.SecurityUser;
import dev.workforge.app.WorkForge.Service.SecurityUserService;
import dev.workforge.app.WorkForge.Service.ServiceImpl.SecurityUserServiceImpl;
import dev.workforge.app.WorkForge.Service.UserPermissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import dev.workforge.app.WorkForge.Model.Permission;
import java.util.*;
import dev.workforge.app.WorkForge.Model.PermissionType;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SecurityUserServiceTests {

    @InjectMocks
    private SecurityUserServiceImpl securityUserService;

    @Mock
    private UserPermissionService userPermissionService;

    @Mock
    UserPermissionProjection userPermissionProjection1;

    @Mock
    UserPermissionProjection userPermissionProjection2;

    @Mock
    private AppUser appUser;

    private SecurityUserImpl testUser;

    @BeforeEach
    public void setup() {
        when(appUser.getUsername()).thenReturn("testuser");
        testUser = new SecurityUserImpl(appUser);
    }

    @Test
    void refreshUserPermissionsForUserDetails_ShouldUpdatePermissions() {

        String username = "testUser";
        when(userPermissionProjection1.getProjectId()).thenReturn(1L);
        when(userPermissionProjection1.getPermissions()).thenReturn(List.of(createPermission(PermissionType.READ, 1L)));
        when(userPermissionProjection2.getProjectId()).thenReturn(2L);
        when(userPermissionProjection2.getPermissions()).thenReturn(List.of(createPermission(PermissionType.WRITE, 2L)));

        when(userPermissionService.getPermissionsForUser(anyString())).thenReturn(List.of(userPermissionProjection1, userPermissionProjection2));

        Map<Long, Set<Permission>> beforeUpdate = testUser.getPermissionMap();

        testUser.addPermissions(1L, Arrays.asList(createPermission(PermissionType.READ, 1L)));
        securityUserService.refreshUserPermissionsForUserDetails(testUser);

        Map<Long, Set<Permission>> afterUpdate = testUser.getPermissionMap();
        assertNotEquals(beforeUpdate, afterUpdate, "Permissions should be updated");
    }

    private Permission createPermission(PermissionType permissionType, long Id) {
        return Permission.builder()
                .id(Id)
                .permissionType(permissionType)
                .build();
    }
}
