package dev.workforge.app.WorkForge.DTO;

import java.util.List;

public record PageResultDTO<T>(
        List<T> content,
        boolean hasNextPage,
        Long nextCursorId,
        Long prevCursorId
) {}
