package de.unistuttgart.iste.ese.api.entities.assignees;

import de.unistuttgart.iste.ese.api.entities.todos.ToDo;
import de.unistuttgart.iste.ese.api.entities.todos.ToDoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RequestMapping("/api/v1")
@RestController
@CrossOrigin(origins = "http://localhost")
public class AssigneeController {

    @Autowired
    private AssigneeRepository assigneeRepository;

    @Autowired
    private ToDoRepository toDoRepository;

    /**
     * Retrieves all assignees.
     *
     * @return a list of all assignees.
     */
    @GetMapping("/assignees")
    public List<Assignee> getAssignees() {
        List<Assignee> allAssignees = (List<Assignee>) assigneeRepository.findAll();
        return allAssignees;
    }

    /**
     * Retrieves a specific assignee by ID.
     *
     * @param id the ID of the assignee to be retrieved.
     * @return the assignee with the given ID.
     * @throws ResponseStatusException if the assignee with the given ID is not found.
     */
    @GetMapping("/assignees/{id}")
    public Assignee getAssignee(@PathVariable("id") long id) {

        Optional<Assignee> searchedAssignee = assigneeRepository.findById(id);
        if (searchedAssignee.isPresent()) {
            return searchedAssignee.get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
            String.format("Assignee with ID %s not found!", id));
    }

    /**
     * Creates a new assignee.
     *
     * @param requestBody the assignee to be created.
     * @return the created assignee.
     * @throws ResponseStatusException if any validation error occurs (e.g., empty name, invalid email).
     */
    @PostMapping("/assignees")
    @ResponseStatus(HttpStatus.CREATED)
    public Assignee createAssignee(@Valid @RequestBody Assignee requestBody) {
        if (requestBody.getName().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Name must not be blank!");
        }
        if (requestBody.getPrename().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "PreName must not be blank!");
        }

        if (!requestBody.getEmail().endsWith("uni-stuttgart.de")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Email must end with uni-stuttgart.de");
        }
        Assignee assignee = new Assignee(requestBody.getPrename(), requestBody.getName(),
            requestBody.getEmail());
        return assigneeRepository.save(assignee);
    }

    /**
     * Updates an existing assignee by ID.
     *
     * @param id the ID of the assignee to be updated.
     * @param requestBody the new assignee data.
     * @return the updated assignee.
     * @throws ResponseStatusException if the assignee with the given ID is not found or if the email is invalid.
     */
    @PutMapping("/assignees/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Assignee updateAssignee(@PathVariable("id") long id, @Valid @RequestBody Assignee requestBody) {

        if (!requestBody.getEmail().endsWith("uni-stuttgart.de")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Email must end with uni-stuttgart.de");
        }
        requestBody.setId(id);
        Optional<Assignee> assigneeToUpdate = assigneeRepository.findById(id);
        if (assigneeToUpdate.isPresent()) {
            return assigneeRepository.save(requestBody);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
            String.format("Assignee with ID %s not found!", id));
    }

    /**
     * Deletes an assignee by ID and removes the assignee from all associated to-do items.
     *
     * @param id the ID of the assignee to be deleted.
     * @return the deleted assignee.
     * @throws ResponseStatusException if the assignee with the given ID is not found.
     */
    @DeleteMapping("/assignees/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Assignee deleteAssignee(@PathVariable("id") long id) {

        Optional<Assignee> assigneeToDelete = assigneeRepository.findById(id);
        if (assigneeToDelete.isPresent()) {

            // Remove assignee from all associated to-dos
            for (ToDo todo : toDoRepository.findAll()) {
                todo.getAssigneeList().remove(assigneeToDelete.get());
                toDoRepository.save(todo);
            }
            assigneeRepository.deleteById(id);
            return assigneeToDelete.get();
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
            String.format("Assignee with ID %s not found!", id));
    }
}
