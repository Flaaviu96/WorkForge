package dev.workforge.app.WorkForge.Service.ServiceImpl;

import dev.workforge.app.WorkForge.DTO.UserDTO;
import dev.workforge.app.WorkForge.Security.SecurityUser.SecurityUser;
import dev.workforge.app.WorkForge.Security.UserSessionService;
import dev.workforge.app.WorkForge.Service.Other.SecurityUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service implementation for authenticate the user
 * This class handles
 * - Verifying the user credentials
 * - Managing and authentication using Spring security
 * - Loading user permissions when the authentication is a success
 * - Storing and removing user sessions from Redis
 */

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

    /**
     * Authenticates the user based on provided credentials and initializes the session.
     * Verifies the username and password using Spring security.
     * Loads the user's permissions upon successfully authentication.
     * Stores the authenticated user into Redis session.
     *
     * @param userDTO The DTO containing the username and the password.
     */
    public void login(UserDTO userDTO, HttpServletRequest request) {
        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(userDTO.username(), userDTO.password());
            Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            securityUserService.loadUserPermissionsIntoUserDetails(securityUser);
            userSessionService.storeUserOnLogin(request.getSession().getId(), securityUser);
        } catch (AuthenticationException e) {
            throw new AuthenticationCredentialsNotFoundException("Bad credentials");
        }
    }

    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
            String sessionId = session.getId();
            userSessionService.removeUserFromRedis();
            SecurityContextHolder.clearContext();
        }
    }

    private void createCookie(String sessionId) {
        ResponseCookie.from("sessionId", sessionId)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .build();
    }
}
