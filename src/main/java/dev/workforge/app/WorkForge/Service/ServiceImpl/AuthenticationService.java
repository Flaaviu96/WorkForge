package dev.workforge.app.WorkForge.Service.ServiceImpl;

import dev.workforge.app.WorkForge.DTO.UserDTO;
import dev.workforge.app.WorkForge.Security.SecurityUser;
import dev.workforge.app.WorkForge.Security.UserSessionService;
import dev.workforge.app.WorkForge.Service.SecurityUserService;
import dev.workforge.app.WorkForge.Service.UserPermissionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserSessionService userSessionService;
    private final SecurityUserService securityUserService;

    public AuthenticationService(AuthenticationManager authenticationManager, UserSessionService userSessionService, SecurityUserService securityUserService) {
        this.authenticationManager = authenticationManager;
        this.userSessionService = userSessionService;
        this.securityUserService = securityUserService;
    }

    public void login(UserDTO userDTO, String sessionId) {
        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(userDTO.username(), userDTO.password());
            Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            securityUserService.loadUserPermissionsIntoUserDetails(securityUser);
            userSessionService.storeUserInRedis(sessionId, securityUser);
        } catch (AuthenticationException e) {
            System.out.println("Authentication failed: " + e.getMessage());
        }
    }

    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
            String sessionId = session.getId();
            userSessionService.removeUserFromRedis(sessionId);
            SecurityContextHolder.clearContext();
        }
    }
}
