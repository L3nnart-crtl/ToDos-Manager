package de.unistuttgart.iste.ese.api.entities.todos;

import java.util.List;

public class ToDoRequest {
    private String title;
    private String description;
    private List<Long> assigneeIdList;  
    private Long dueDate;
    private boolean finished;

    // Getter und Setter
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Long> getAssigneeIdList() {
        return assigneeIdList;
    }

    public void setAssigneeIdList(List<Long> assigneeIdList) {
        this.assigneeIdList = assigneeIdList;
    }

    public Long getDueDate() {
        return dueDate;
    }

    public void setDueDate(Long dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(final boolean finished) {
        this.finished = finished;
    }
}