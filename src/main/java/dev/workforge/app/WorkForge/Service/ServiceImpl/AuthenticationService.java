package dev.workforge.app.WorkForge.Service.ServiceImpl;

import dev.workforge.app.WorkForge.DTO.UserDTO;
import dev.workforge.app.WorkForge.Security.SecurityUser;
import dev.workforge.app.WorkForge.Security.SecurityUserService;
import dev.workforge.app.WorkForge.Service.UserPermissionService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final SecurityUserService securityUserService;
    private final UserPermissionService userPermissionService;

    public AuthenticationService(AuthenticationManager authenticationManager, SecurityUserService securityUserService, UserPermissionService userPermissionService) {
        this.authenticationManager = authenticationManager;
        this.securityUserService = securityUserService;
        this.userPermissionService = userPermissionService;
    }

    public void login(UserDTO userDTO, String idSession) {
        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword());
            Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            userPermissionService.transferPermissions(securityUser);
            securityUserService.storeUserInRedis(idSession, securityUser);

        } catch (AuthenticationException e) {
            System.out.println("Authentication failed: " + e.getMessage());
        }
    }
}
