package de.unistuttgart.iste.ese.api.entities.assignees;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Entity class representing an Assignee in the system. 
 * This class is mapped to the "assignees" table in the database.
 *
 * It includes validation annotations to ensure that the prename, name, and email are not null 
 * and follow specific formats (e.g., email must be a valid email address).
 */
@Entity
@Table(name = "assignees")
public class Assignee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @NotNull
    @Size(min = 1)
    private String prename;
   
    @NotNull
    @Size(min = 1)
    private String name;

    @NotNull
    @Email(message = "Please enter a valid Email-Address")
    private String email;
    
    
    // empty default constructor is necessary for JPA


    public Assignee() {
    }

    public Assignee(final String prename, final String name, final String email) {
        this.prename = prename;
        this.name = name;
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public @NotNull @Size(min = 1, message = "Prename must have at least one character") String getPrename() {
        return prename;
    }

    public void setPrename(final @NotNull(message = "Prename cannot be null") @Size(min = 1, message = "Prename must have at least one character") String prename) {
        this.prename = prename;
    }

    public @NotNull(message = "Name cannot be null") @Size(min = 1, message = "Name must have at least one character") String getName() {
        return name;
    }

    public void setName(final @NotNull(message = "Name cannot be null") @Size(min = 1, message = "Name must have at least one character") String name) {
        this.name = name;
    }

    public @NotNull @Email(message = "Please enter a valid Email-Address") String getEmail() {
        return email;
    }

    public void setEmail(final @NotNull @Email(message = "Please enter a valid Email-Address")  String email) {
        this.email = email;
    }
}
