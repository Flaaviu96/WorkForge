package dev.workforge.app.WorkForge.Security;

import dev.workforge.app.WorkForge.Service.AccessControlService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Aspect
@Component
public class AccessControlAspect {

    private static final Logger logger = LoggerFactory.getLogger(AccessControlAspect.class);

    private final AccessControlService accessControlService;

    public AccessControlAspect(AccessControlService accessControlService) {
        this.accessControlService = accessControlService;
    }

    @Around("@annotation(dev.workforge.app.WorkForge.Security.PermissionCheck)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info(" Aspect Intercepting Method: {}", joinPoint.getSignature().getName());

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        PermissionCheck permissionCheck = signature.getMethod().getAnnotation(PermissionCheck.class);
        String parameter = permissionCheck.parameter().equals("projectId") ? permissionCheck.parameter() : "projectId";
        Object projectId = getArgumentValue(joinPoint, parameter);
        if (projectId != null) {
            String sessionId = getCurrentHttpRequest().getRequestedSessionId();
            accessControlService.hasPermissions((long) projectId, permissionCheck.permissionType(), sessionId);
            return null;
        }
        Object result = joinPoint.proceed(replaceTheArgument(joinPoint));

        return result;

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

        if (args[0] instanceof List<?>) {
            List<Long> availableProjectsList = Arrays.stream(availableProjects)
                    .mapToLong(i -> i)
                    .boxed()
                    .collect(Collectors.toList());
            newArgs[0] = availableProjectsList;
        }

        return newArgs;
    }

    private HttpServletRequest getCurrentHttpRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
}