package de.unistuttgart.iste.ese.api.entities.todos;

import de.unistuttgart.iste.ese.api.ApiVersion1;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.apache.logging.log4j.CloseableThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/api/v1")
@ApiVersion1
public class ToDoController {

    @Autowired
    private ToDoRepository toDoRepository;
    @Autowired
    private ToDoService toDoService;

    // executed after start-up and dependency injection
    @PostConstruct
    public void init() {
        // check if DB is empty
        long numberOfToDos = toDoRepository.count();
        
    }

    // get all todos
    @GetMapping("/todos")
    public List<ToDoResponse> gettodos() {
        List<ToDoResponse> toDoResponses = new ArrayList<>();
        for (ToDo todo : toDoRepository.findAll()) {

            ToDoResponse response = new ToDoResponse(todo.getId(), todo.getTitle(), todo.getDescription(),
                todo.isFinished(), todo.getAssigneeList(),
                todo.getCreatedDate().getTime(), todo.getDueDate());
            toDoResponses.add(response);
        }
        return toDoResponses ;
    }

    // get a single todo
    @GetMapping("/todos/{id}")
    public ToDoResponsePut getToDo(@PathVariable("id") long id) {
        
        if (!toDoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ToDo not found");
        }
        ToDo todo = toDoRepository.findById(id);
        if (todo != null) {
            ToDoResponsePut response = new ToDoResponsePut(todo.getId(), todo.getTitle(), todo.getDescription(),
                todo.isFinished(), todo.getAssigneeList(),
                todo.getCreatedDate().getTime(), todo.getDueDate(),todo.getFinishedDate());
            return response;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("ToDo with ID %s not found!", id));
    }

    // create a todo
    @PostMapping("/todos")
    @ResponseStatus(HttpStatus.CREATED)
    public ToDoResponse createToDo(@Valid @RequestBody ToDoRequest requestBody) {
        
        if (requestBody.getTitle() == null || requestBody.getTitle().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        List<Long> assigneeIdList = requestBody.getAssigneeIdList();
        toDoService.todoRequestValid(requestBody);
        
        ToDo todo = new ToDo(requestBody.getTitle(),requestBody.getDescription(),toDoService.getAssigneesListByIds(assigneeIdList),requestBody.getDueDate());
        toDoRepository.save(todo);
        ToDoResponse response = new ToDoResponse(todo.getId(), todo.getTitle(), todo.getDescription(),
            todo.isFinished(), todo.getAssigneeList(),
            todo.getCreatedDate().getTime(), todo.getDueDate());
        return response;
    }

    // update a todo
    @PutMapping("/todos/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ToDoResponsePut updateToDo(@PathVariable("id") long id, @Valid @RequestBody ToDoRequest requestBody) {
        
        if (!toDoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ToDo not found");
        }
        toDoService.todoRequestValid(requestBody);
        ToDo toDoToUpdate = toDoRepository.findById(id);
        if (toDoToUpdate != null) {
            toDoToUpdate.setTitle(requestBody.getTitle());
            toDoToUpdate.setDescription(requestBody.getDescription());
            toDoToUpdate.setFinished(requestBody.isFinished());
            toDoToUpdate.setAssigneeList(toDoService.getAssigneesListByIds(requestBody.getAssigneeIdList()));
            toDoToUpdate.setFinishedDate(new Date(requestBody.getDueDate()));
            toDoRepository.save(toDoToUpdate);
            return new ToDoResponsePut(toDoToUpdate.getId(), toDoToUpdate.getTitle(), toDoToUpdate.getDescription(),
                toDoToUpdate.isFinished(), toDoToUpdate.getAssigneeList(),
                toDoToUpdate.getCreatedDate().getTime(), toDoToUpdate.getDueDate(),toDoToUpdate.getDueDate());
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("ToDo with ID %s not found!", id));
    }

    // delete a todo
    @DeleteMapping("/todos/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> deleteToDo(@PathVariable("id") long id) {
        
        if (!toDoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ToDo not found");
        }
        ToDo toDoToDelete = toDoRepository.findById(id);
      
        if (toDoToDelete != null) {
            toDoRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("ToDo with ID %s not found!", id));
    }
}
