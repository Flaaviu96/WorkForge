package dev.workforge.app.WorkForge.service.user_permission.impl;

import dev.workforge.app.WorkForge.model.AppUser;
import dev.workforge.app.WorkForge.model.Permission;
import dev.workforge.app.WorkForge.model.Project;

import java.util.List;

public record PermissionData(List<Permission> permissionsList, List<AppUser> userList, Project project) {}
