package dev.workforge.app.WorkForge.Repository;

import dev.workforge.app.WorkForge.DTO.PageResultDTO;
import dev.workforge.app.WorkForge.DTO.TaskFilter;
import dev.workforge.app.WorkForge.DTO.TaskSummaryDTO;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskCriteriaRepository {
    PageResultDTO<TaskSummaryDTO> findTasksByFilter(TaskFilter taskFilter, long projectId);
}
