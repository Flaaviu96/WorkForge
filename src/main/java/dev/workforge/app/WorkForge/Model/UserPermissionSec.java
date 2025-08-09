package dev.workforge.app.WorkForge.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPermissionSec {
    private long buildPermissionAt;
    private long updatedPermission;

    public UserPermissionSec(long buildPermissionAt, long updatedPermission) {
        this.buildPermissionAt = buildPermissionAt;
        this.updatedPermission = updatedPermission;
    }

    public UserPermissionSec() {
        this.buildPermissionAt = System.currentTimeMillis();
    }
}
