package dev.workforge.app.WorkForge.Security;

import dev.workforge.app.WorkForge.Service.AccessControlService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Component
public class AccessControlAspect {

    private final AccessControlService accessControlService;

    public AccessControlAspect(AccessControlService accessControlService) {
        this.accessControlService = accessControlService;
    }

    @Around("@annotation(permissionCheck)")
    public void checkPermission(PermissionCheck permissionCheck, ProceedingJoinPoint joinPoint) throws Throwable {
        Object projectId = getArgumentValue(joinPoint, permissionCheck.parameter());
        if (projectId != null) {
            String sessionId = getCurrentHttpRequest().getRequestedSessionId();
            accessControlService.hasPermissions((long) projectId, permissionCheck.permissionType(), sessionId);
            return;
        }
        joinPoint.proceed(replaceTheArgument(joinPoint));

    }

    private Object getArgumentValue(ProceedingJoinPoint joinPoint, String argumentName) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = methodSignature.getParameterNames();
        Object[] arguments = joinPoint.getArgs();

        for (int i = 0; i < parameterNames.length; i++) {
            if (parameterNames[i].equals(argumentName)) {
                return arguments[i];
            }
        }
        return null;
    }

    private Object[] replaceTheArgument(ProceedingJoinPoint joinPoint) {
        int[] availableProjects = accessControlService.getAvailableProjectsForCurrentUser();

        Object[] args = joinPoint.getArgs();

        Object[] newArgs = Arrays.copyOf(args, args.length);

        newArgs[0] = availableProjects;

        return newArgs;
    }

    private HttpServletRequest getCurrentHttpRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
}
