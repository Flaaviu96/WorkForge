package dev.workforge.app.WorkForge.Security;

import dev.workforge.app.WorkForge.Security.SecurityUser.SecurityUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;

public class RedisSecurityContextRepository implements SecurityContextRepository {

    private final UserSessionService securityUserService;

    public RedisSecurityContextRepository(UserSessionService securityUserService) {
        this.securityUserService = securityUserService;
    }

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        String sessionId = requestResponseHolder.getRequest().getRequestedSessionId();

        if (sessionId == null) {
            return SecurityContextHolder.createEmptyContext();
        }

        SecurityUser securityUser = securityUserService.getUserFromRedis(sessionId);
        if (securityUser == null) {
            return SecurityContextHolder.createEmptyContext();
        }
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                securityUser, null, securityUser.getAuthorities()
        );
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        return securityContext;
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        if (context.getAuthentication() == null) {
            return;
        }

        Authentication authentication = context.getAuthentication();

        if (authentication.getPrincipal() instanceof SecurityUser securityUser) {
            HttpSession session = request.getSession(false);
            if (session == null) {
                // No session, do NOT create one just to save context.
                // This avoids creating session on every request.
                return;
            }
            String sessionId = session.getId();
            securityUserService.storeUserInRedis(sessionId, securityUser);
        }
    }



    @Override
    public boolean containsContext(HttpServletRequest request) {
        String sessionId = request.getRequestedSessionId();
        return sessionId != null && securityUserService.hasKey(sessionId);
    }
}
