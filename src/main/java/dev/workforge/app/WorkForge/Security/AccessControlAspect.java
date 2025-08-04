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

    /**
     * Around advice that intercepts methods annotated with @PermissionCheck.
     * It extracts the projectId parameter (or defaults to "projectId"), verifies if the
     * current user has the required permission on that project, and either proceeds with
     * the method invocation or blocks it by returning null.
     *
     * If projectId is not found, it attempts to replace the first argument with a list
     * of available projects for the current user and proceeds.
     *
     * @param joinPoint the join point representing the intercepted method call
     * @return the method's original return value if permission check passes, or null otherwise
     * @throws Throwable if the intercepted method throws an exception
     */
    @Around("@annotation(dev.workforge.app.WorkForge.Security.PermissionCheck)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("Aspect Intercepting Method: {}", joinPoint.getSignature().getName());

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        PermissionCheck permissionCheck = signature.getMethod().getAnnotation(PermissionCheck.class);

        // Determine which parameter to use for projectId
        String parameter = permissionCheck.parameter().equals("projectId") ? permissionCheck.parameter() : "projectId";
        Object projectId = getArgumentValue(joinPoint, parameter);

        if (projectId != null) {
            String sessionId = getCurrentHttpRequest().getRequestedSessionId();
            boolean hasPermission = accessControlService.hasPermissions((long) projectId, permissionCheck.permissionType(), sessionId);

            if (hasPermission) {
                return joinPoint.proceed();
            } else {
                logger.warn("Access denied for projectId {} with permission {}.", projectId, permissionCheck.permissionType());
                return null; // or throw exception based on your security policy
            }
        }

        // If projectId not found, try to replace the first argument with available projects and proceed
        Object[] newArgs = replaceTheArgument(joinPoint);
        return joinPoint.proceed(newArgs);
    }

    /**
     * Retrieves the value of a method argument by its name.
     *
     * @param joinPoint the join point representing the intercepted method call
     * @param argumentName the name of the argument to retrieve
     * @return the argument value if found, or null otherwise
     */
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

    /**
     * Replaces the first argument of the intercepted method with the list of project IDs
     * that the current user has access to, if applicable.
     *
     * @param joinPoint the join point representing the intercepted method call
     * @return the modified arguments array
     */
    private Object[] replaceTheArgument(ProceedingJoinPoint joinPoint) {
        int[] availableProjects = accessControlService.getAvailableProjectsForCurrentUser();
        Object[] args = joinPoint.getArgs();
        Object[] newArgs = Arrays.copyOf(args, args.length);

        if (args.length > 0 && args[0] instanceof List<?>) {
            List<Long> availableProjectsList = Arrays.stream(availableProjects)
                    .mapToLong(i -> i)
                    .boxed()
                    .collect(Collectors.toList());
            newArgs[0] = availableProjectsList;
        }

        return newArgs;
    }

    /**
     * Retrieves the current HttpServletRequest from the RequestContextHolder.
     *
     * @return the current HttpServletRequest, or null if not available
     */
    private HttpServletRequest getCurrentHttpRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
}
