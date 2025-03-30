package dev.workforge.app.WorkForge.Security;

import dev.workforge.app.WorkForge.Model.PermissionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PermissionCheck {
    PermissionType[] permissionType();
    String parameter() default "projectId";
}
