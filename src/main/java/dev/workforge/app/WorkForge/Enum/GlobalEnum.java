package dev.workforge.app.WorkForge.Enum;

import lombok.Getter;

@Getter
public enum GlobalEnum {
    DEFAULT_WORKFLOW(1),
    INVALID_ID(0);
    final long id;
    private GlobalEnum(long id) {
        this.id = id;
    }

}
