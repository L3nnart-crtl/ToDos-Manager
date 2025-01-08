package de.unistuttgart.iste.ese.api.entities.todos;

import de.unistuttgart.iste.ese.api.entities.assignees.Assignee;
import de.unistuttgart.iste.ese.api.entities.assignees.AssigneeRepository;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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
    
    

    // empty default constructor is necessary for JPA
    public ToDo() {

    }


    public ToDo(final String title, final String description, final List<Assignee> assigneeList, final long unixTimeStamp,final String category) {
        
        this.title = title;
        this.description = description;
        this.dueDate = new Date(unixTimeStamp);
        this.assigneeList = new ArrayList<>(assigneeList);
        this.createdDate = dueDate;
        this.finishedDate = createdDate;
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

    public Long getDueDate() {
        return dueDate.getTime();
    }

    public void setDueDate(final Long dueDate) {
        this.dueDate = new Date(dueDate);
    }

    public Long getFinishedDate() {
        return finishedDate.getTime();
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
}
