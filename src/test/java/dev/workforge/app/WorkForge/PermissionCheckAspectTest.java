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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)  // Enables Mockito for unit testing
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
    private SecurityContext securityContext;

    @Mock
    private ServletRequestAttributes servletRequestAttributes;

    @Mock
    private Authentication authentication;

    private SecurityUser testUser;

    @BeforeEach
    public void setup() {
        // Mock the Security Context
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Mock a test user
        testUser = mock(SecurityUser.class);
        when(authentication.getPrincipal()).thenReturn(testUser);

        when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);


    }

    @Test
    public void testPermissionGranted() {
        String sessionId = "testSessionId";
        when(httpServletRequest.getRequestedSessionId()).thenReturn(sessionId);

        long lastPermissionsUpdate = System.currentTimeMillis();
        Permission permission = new Permission();
        permission.setPermissionType(PermissionType.READ);
        Map<Long, Set<Permission>> permissions = new HashMap<>();
        permissions.put(1L, new HashSet<>(Collections.singleton(permission)));
        when(testUser.getPermissionMap()).thenReturn(permissions);

        when(userSessionService.getPermissionFromRedis(anyString())).thenReturn(lastPermissionsUpdate);
        when(testUser.getLastPermissionsUpdate()).thenReturn(lastPermissionsUpdate);

        PermissionCheck mockPermissionCheck = mock(PermissionCheck.class);
        when(mockPermissionCheck.permissionType()).thenReturn(PermissionType.READ);

        permissionCheckAspect.checkPermission(mockPermissionCheck, 1L);

        verify(userSessionService, times(1)).getPermissionFromRedis(anyString());
        verify(securityUserService, times(0)).refreshUserPermissionsForUserDetails(testUser);
    }

    @Test
    public void testPermissionNotGranted() {
        String sessionId = "testSessionId";
        when(httpServletRequest.getRequestedSessionId()).thenReturn(sessionId);

        Permission permission = new Permission();
        permission.setPermissionType(PermissionType.READ);
        Map<Long, Set<Permission>> permissions = new HashMap<>();
        when(testUser.getPermissionMap()).thenReturn(permissions);

        when(userSessionService.getPermissionFromRedis(anyString())).thenReturn(System.currentTimeMillis());
        when(testUser.getLastPermissionsUpdate()).thenReturn(System.currentTimeMillis());
    }
}