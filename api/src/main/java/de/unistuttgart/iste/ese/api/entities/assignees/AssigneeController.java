package de.unistuttgart.iste.ese.api.entities.assignees;

import de.unistuttgart.iste.ese.api.entities.todos.ToDo;
import de.unistuttgart.iste.ese.api.entities.todos.ToDoRepository;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
@RequestMapping("/api/v1")
@RestController
public class AssigneeController {

    @Autowired
    private  AssigneeRepository assigneeRepository;
    @Autowired
    private  ToDoRepository toDoRepository;
    
    
    // get all assignees
    @GetMapping("/assignees")
    public List<Assignee> getAssignees() {
        List<Assignee> allAssignees = (List<Assignee>) assigneeRepository.findAll();
        return allAssignees;
    }

    // get a single assignee
    @GetMapping("/assignees/{id}")
    public Assignee getAssignee(@PathVariable("id") long id) {

        Optional<Assignee> searchedAssignee = assigneeRepository.findById(id);
        if (searchedAssignee.isPresent()) {
            return searchedAssignee.get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
            String.format("Assignee with ID %s not found!", id));
    }

    // create an assignee
    @PostMapping("/assignees")
    @ResponseStatus(HttpStatus.CREATED)
    public Assignee createAssignee(@Valid @RequestBody Assignee requestBody) {
        if (!requestBody.getEmail().endsWith("uni-stuttgart.de")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Email must end with uni-stuttgart.de");
        }
        Assignee assignee = new Assignee(requestBody.getPrename(), requestBody.getName(),
            requestBody.getEmail());
        return assigneeRepository.save(assignee);
    }

    // update an assignee
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

    // delete a assignee and delete in all todos
    @DeleteMapping("/assignees/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Assignee deleteAssignee(@PathVariable("id") long id) {

        Optional<Assignee> assigneeToDelete = assigneeRepository.findById(id);
        if (assigneeToDelete.isPresent()) {
            
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
