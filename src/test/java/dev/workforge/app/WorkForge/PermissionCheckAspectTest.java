package dev.workforge.app.WorkForge;

import dev.workforge.app.WorkForge.Model.Permission;
import dev.workforge.app.WorkForge.Model.PermissionType;
import dev.workforge.app.WorkForge.Security.PermissionCheck;
import dev.workforge.app.WorkForge.Security.PermissionCheckAspect;
import dev.workforge.app.WorkForge.Security.SecurityUser;
import dev.workforge.app.WorkForge.Security.UserSessionService;
import dev.workforge.app.WorkForge.Service.SecurityUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PermissionCheckAspectTest {

    @Mock
    private UserSessionService userSessionService;

    @Mock
    private SecurityUserService securityUserService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private PermissionCheckAspect permissionCheckAspect;

    @Mock
    private PermissionCheck permissionCheck;

    @Mock
    private ServletRequestAttributes servletRequestAttributes;

    @Mock
    private Authentication authentication;

    private SecurityUser testUser;

    private static final String sessionId = "testSessionId";

    @BeforeEach
    public void setup() {
        long lastPermissionsUpdate = System.currentTimeMillis();
        when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);
        when(httpServletRequest.getRequestedSessionId()).thenReturn(sessionId);

        testUser = mock(SecurityUser.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        SecurityContextHolder.setContext(securityContext);

        when(userSessionService.getPermissionFromRedis(anyString())).thenReturn(lastPermissionsUpdate);
        when(testUser.getLastPermissionsUpdate()).thenReturn(lastPermissionsUpdate);
    }

    private void preparePermissions() {
        Map<Long, Set<Permission>> permissions = new HashMap<>();
    }

    private Permission createPermission(PermissionType permissionType) {
        return new Permission().setPermissionType(permissionType);
    }

    private void mockUserPermissions(Map<Long, Set<Permission>> permissions) {
        when(testUser.getPermissionMap()).thenReturn(permissions);
    }

    @Test
    public void testPermissionGranted() {
        Map<Long, Set<Permission>> permissions = new HashMap<>();
        permissions.put(1L, new HashSet<>(Collections.singleton(createPermission(PermissionType.READ))));
        mockUserPermissions(permissions);

        // Mock annotation behavior
        when(permissionCheck.permissionType()).thenReturn(PermissionType.READ);

        permissionCheckAspect.checkPermission(permissionCheck, 1L);

        verify(userSessionService, times(1)).getPermissionFromRedis(anyString());
        verify(securityUserService, times(0)).refreshUserPermissionsForUserDetails(testUser);
    }

    @Test
    public void testPermissionNotGranted() {
        mockUserPermissions(new HashMap<>());

        assertThrows(AccessDeniedException.class, () ->
                permissionCheckAspect.checkPermission(permissionCheck, 1L)
        );
    }
}