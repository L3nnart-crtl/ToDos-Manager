package de.unistuttgart.iste.ese.api.entities.todos;

import de.unistuttgart.iste.ese.api.entities.assignees.Assignee;

import java.util.List;

public class ToDoResponsePut {
    private long id;
    private String title;
    private String description;
    private boolean finished;
    private List<Assignee> assigneeList;
    private long createdDate;
    private long dueDate;
    private long finishedDate;
    private String category;

    public ToDoResponsePut(final long id, final String title, final String description, final boolean finished,
                           final List<Assignee> assigneeList, final long createdDate, final long dueDate, final long finishedDate,final String category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.finished = finished;
        this.assigneeList = assigneeList;
        this.createdDate = createdDate;
        this.dueDate = dueDate;
        this.finishedDate = finishedDate;
        this.category = category;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(final boolean finished) {
        this.finished = finished;
    }

    public List<Assignee> getAssigneeList() {
        return assigneeList;
    }

    public void setAssigneeList(final List<Assignee> assigneeList) {
        this.assigneeList = assigneeList;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(final long createdDate) {
        this.createdDate = createdDate;
    }

    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(final long dueDate) {
        this.dueDate = dueDate;
    }

    public long getFinishedDate() {
        return finishedDate;
    }

    public void setFinishedDate(final long finishedDate) {
        this.finishedDate = finishedDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(final String category) {
        this.category = category;
    }
}
