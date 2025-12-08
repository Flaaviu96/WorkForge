package dev.workforge.app.WorkForge.security;

import dev.workforge.app.WorkForge.service.usersession.UserPermissionCacheService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class PermissionFilter extends OncePerRequestFilter {

    private final UserPermissionCacheService userPermissionCacheService;

    public PermissionFilter(UserPermissionCacheService userPermissionCacheService) {
        this.userPermissionCacheService = userPermissionCacheService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            Jwt jwt = jwtAuthenticationToken.getToken();
            String username = jwt.getClaimAsString("preferred_username");
            if (username == null) {
                username = jwt.getSubject();
            }
            UserPrincipal userPrincipal = userPermissionCacheService.getCachedPermissions(username);
            if (userPrincipal == null) {

            }
            Object DUMMY = new Object();
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userPrincipal, DUMMY, null);
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            filterChain.doFilter(request, response);
        }
    }
}
