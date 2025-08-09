package dev.workforge.app.WorkForge.Service.UserSession;

import dev.workforge.app.WorkForge.Model.UserPermissionSec;

public interface PermissionTimestampStore {
    void save(String userId, UserPermissionSec securityUser);
    UserPermissionSec find(String userId);
    void updateTimeStamp(String userId);
}
