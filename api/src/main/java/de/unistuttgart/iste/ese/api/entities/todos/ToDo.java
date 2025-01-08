package de.unistuttgart.iste.ese.api.entities.todos;

import de.unistuttgart.iste.ese.api.entities.assignees.Assignee;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.sql.CallableStatement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "todos")
public class ToDo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @NotNull
    @Size(min = 1)
    private String title;

    private String description;

    private boolean finished;

    @OneToMany
    private List<Assignee> assigneeList;

    private Date createdDate;

    private Date dueDate;

    private Date finishedDate;

    private String category;

    // Empty default constructor is necessary for JPA
    public ToDo() {

    }

    public ToDo(final String title, final String description, final List<Assignee> assigneeList, final long dueDate, final String category) {
        this.title = title;
        this.description = description;
        this.createdDate = new Date();
        this.dueDate = new Date(dueDate);
        this.finishedDate = null;  // Finished date is initially null
        this.assigneeList = new ArrayList<>(assigneeList);
        this.category = category;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public @NotNull @Size(min = 1) String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setDueDate(final Date dueDate) {
        this.dueDate = dueDate;
    }

    public void setTitle(final @NotNull String title) {
        this.title = title;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(final boolean finished) {
        this.finished = finished;
        if (finished) {
            this.finishedDate = new Date();  // Set the finished date to the current time when finished is true
        } else {
            this.finishedDate = null;  // Set finished date to null when finished is false
        }
    }

    public List<Assignee> getAssigneeList() {
        return assigneeList;
    }

    public void setAssigneeList(final List<Assignee> assigneeList) {
        this.assigneeList = assigneeList;
    }

    public void setCreatedDate(final Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(final Long dueDate) {
        this.dueDate = new Date(dueDate);
    }

    public Date getFinishedDate() {
        if (finishedDate == null) {
            return null;
        }
        return finishedDate;
    }

    public void setFinishedDate(final Date finishedDate) {
        this.finishedDate = finishedDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(final String category) {
        this.category = category;
    }

    public Long getFinishedDateMilliSeconds() {
        if (this.finishedDate == null) {
            return null;
        }
        return this.finishedDate.getTime();
    }
}
