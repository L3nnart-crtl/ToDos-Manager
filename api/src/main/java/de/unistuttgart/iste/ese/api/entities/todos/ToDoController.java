package de.unistuttgart.iste.ese.api.entities.todos;

import de.unistuttgart.iste.ese.api.ApiVersion1;
import de.unistuttgart.iste.ese.api.entities.assignees.Assignee;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;

@RestController
@RequestMapping("/api/v1")
@ApiVersion1
@CrossOrigin(origins = "http://localhost")
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
                todo.getCreatedDate().getTime(), todo.getDueDate().getTime(),todo.getFinishedDateMilliSeconds(),todo.getCategory());
            toDoResponses.add(response);
        }
        return toDoResponses ;
    }

    // get a single todo
    @GetMapping("/todos/{id}")
    public ToDoResponse getToDo(@PathVariable("id") long id) {
        
        if (!toDoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ToDo not found");
        }
        ToDo todo = toDoRepository.findById(id);
        if (todo != null) {
            ToDoResponse response = new ToDoResponse(todo.getId(), todo.getTitle(), todo.getDescription(),
                todo.isFinished(), todo.getAssigneeList(),
                todo.getCreatedDate().getTime(), todo.getDueDate().getTime(),todo.getFinishedDateMilliSeconds(),todo.getCategory());
            return response;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("ToDo with ID %s not found!", id));
    }

    // create a todo
    @PostMapping("/todos")
    @ResponseStatus(HttpStatus.CREATED)
    public ToDoResponsePost createToDo(@Valid @RequestBody ToDoRequest requestBody) {
        
        if (requestBody.getTitle() == null || requestBody.getTitle().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        List<Long> assigneeIdList = requestBody.getAssigneeIdList();
        toDoService.todoRequestValid(requestBody);
        
        ToDo todo = new ToDo(requestBody.getTitle(),requestBody.getDescription(),toDoService.getAssigneesListByIds(assigneeIdList),requestBody.getDueDate(),toDoService.findCategory(requestBody.getTitle()));
        toDoRepository.save(todo);
        ToDoResponsePost response = new ToDoResponsePost(todo.getId(), todo.getTitle(), todo.getDescription(),
            todo.isFinished(), todo.getAssigneeList(),
            todo.getCreatedDate().getTime(), todo.getDueDate().getTime(),toDoService.findCategory(requestBody.getTitle()));
        return response;
    }

    // update a todo
    @PutMapping("/todos/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ToDoResponse updateToDo(@PathVariable("id") long id, @Valid @RequestBody ToDoRequest requestBody) {
        
        if (!toDoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ToDo not found");
        }
        toDoService.todoRequestValid(requestBody);
        ToDo toDoToUpdate = toDoRepository.findById(id);
        if (toDoToUpdate != null) {
            toDoToUpdate.setTitle(requestBody.getTitle());
            toDoToUpdate.setDescription(requestBody.getDescription());
            toDoToUpdate.setFinished(requestBody.isFinished());
            toDoToUpdate.setDueDate(requestBody.getDueDate());
            toDoToUpdate.setAssigneeList(toDoService.getAssigneesListByIds(requestBody.getAssigneeIdList()));
            
            if (requestBody.isFinished() && toDoToUpdate.getFinishedDate() == null) {
                toDoToUpdate.setFinishedDate(new Date(requestBody.getDueDate()));
            } else if (!requestBody.isFinished()) {
                toDoToUpdate.setFinishedDate(null);
            }
            //Update Category
            toDoToUpdate.setCategory(toDoService.findCategory(toDoToUpdate.getTitle()));
            toDoRepository.save(toDoToUpdate);
            return new ToDoResponse(toDoToUpdate.getId(), toDoToUpdate.getTitle(), toDoToUpdate.getDescription(),
                toDoToUpdate.isFinished(), toDoToUpdate.getAssigneeList(),
                toDoToUpdate.getCreatedDate().getTime(), toDoToUpdate.getDueDate().getTime(),toDoToUpdate.getFinishedDateMilliSeconds(),toDoToUpdate.getCategory());
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


    @GetMapping(value = "/csv-downloads/todos", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadToDosCSV() throws IOException {
        List<ToDo> todos = (List<ToDo>) toDoRepository.findAll();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(byteArrayOutputStream, StandardCharsets.UTF_8);

        // Write CSV headers
        writer.write("id,title,description,finished,assignees,createdDate,dueDate,finishedDate,category\n");

        // Write each ToDo in the required format
        for (ToDo todo : todos) {
            String assignees = String.join("+", getAssigneeNames(todo.getAssigneeList()));
            writer.write(String.format("%d,%s,%s,%b,%s,%s,%s,%s,%s\n",
                todo.getId(),
                escapeCsv(todo.getTitle()),
                escapeCsv(todo.getDescription()),
                todo.isFinished(),
                assignees,
                formatDate(todo.getCreatedDate()),
                formatDate(todo.getDueDate()),
                formatDate(todo.getFinishedDate()), // Hier wird finishedDate korrekt hinzugefügt
                escapeCsv(todo.getCategory())
            ));
        }
        writer.flush();

        // Return CSV file as response
        byte[] csvContent = byteArrayOutputStream.toByteArray();
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=\"todos.csv\"")
            .body(csvContent);
    }

    private List<String> getAssigneeNames(List<Assignee> assignees) {
        // Convert the assignees into "prename name" format
        return assignees.stream()
            .map(assignee -> assignee.getPrename() + " " + assignee.getName())
            .toList();
    }

    private String escapeCsv(String value) {
        // Escape special characters in CSV
        if (value == null) {
            return "";
        }
        return value.replace("\"", "\"\"");
    }

    private String formatDate(Date date) {
        // Format date to YYYY-MM-DD
        if (date == null) {
            return "";
        }
        return new java.text.SimpleDateFormat("yyyy-MM-dd").format(date);
    }
}
