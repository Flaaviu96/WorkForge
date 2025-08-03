package dev.workforge.app.WorkForge.DTO;

import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
public class TaskFilter {

    private String taskName;
    private String state;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date createdDateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date createdDateTo;

    private String assignedTo;

    private long cursorTaskId;

    private boolean nextPage;

    private boolean firstSearch;

    public boolean isFirstSearch() {
        return firstSearch;
    }

    public void setFirstSearch(boolean firstSearch) {
        this.firstSearch = firstSearch;
    }

    public void setNextPage(boolean nextPage) {
        this.nextPage = nextPage;
    }

    public void setCursorTaskId(long cursorTaskId) {
        this.cursorTaskId = cursorTaskId;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCreatedDateFrom(Date createdDateFrom) {
        this.createdDateFrom = createdDateFrom;
    }

    public void setCreatedDateTo(Date createdDateTo) {
        this.createdDateTo = createdDateTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }
}
