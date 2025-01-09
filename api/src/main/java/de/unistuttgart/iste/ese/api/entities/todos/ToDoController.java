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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * REST controller for managing ToDo entities.
 */
@RestController
@RequestMapping("/api/v1")
@ApiVersion1
@CrossOrigin(origins = "http://localhost")
public class ToDoController {

    @Autowired
    private ToDoRepository toDoRepository;

    @Autowired
    private ToDoService toDoService;

    /**
     * Initializes the controller after startup and dependency injection.
     */
    @PostConstruct
    public void init() {
        long numberOfToDos = toDoRepository.count();
    }

    /**
     * Retrieves all ToDo entities.
     *
     * @return a list of ToDo responses.
     */
    @GetMapping("/todos")
    public List<ToDoResponse> getTodos() {
        List<ToDoResponse> toDoResponses = new ArrayList<>();
        for (ToDo todo : toDoRepository.findAll()) {
            ToDoResponse response = new ToDoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.isFinished(),
                todo.getAssigneeList(),
                todo.getCreatedDate().getTime(),
                todo.getDueDate().getTime(),
                todo.getFinishedDateMilliSeconds(),
                todo.getCategory()
            );
            toDoResponses.add(response);
        }
        return toDoResponses;
    }

    /**
     * Retrieves a single ToDo entity by its ID.
     *
     * @param id the ID of the ToDo.
     * @return the ToDo response.
     */
    @GetMapping("/todos/{id}")
    public ToDoResponse getToDo(@PathVariable("id") long id) {
        if (!toDoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ToDo not found");
        }
        ToDo todo = toDoRepository.findById(id);
        if (todo != null) {
            return new ToDoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.isFinished(),
                todo.getAssigneeList(),
                todo.getCreatedDate().getTime(),
                todo.getDueDate().getTime(),
                todo.getFinishedDateMilliSeconds(),
                todo.getCategory()
            );
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("ToDo with ID %s not found!", id));
    }

    /**
     * Creates a new ToDo entity.
     *
     * @param requestBody the request body containing ToDo data.
     * @return the created ToDo response.
     */
    @PostMapping("/todos")
    @ResponseStatus(HttpStatus.CREATED)
    public ToDoResponsePost createToDo(@Valid @RequestBody ToDoRequest requestBody) {
        if (requestBody.getTitle() == null || requestBody.getTitle().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        List<Long> assigneeIdList = requestBody.getAssigneeIdList();
        toDoService.todoRequestValid(requestBody);

        ToDo todo = new ToDo(
            requestBody.getTitle(),
            requestBody.getDescription(),
            toDoService.getAssigneesListByIds(assigneeIdList),
            requestBody.getDueDate(),
            toDoService.findCategory(requestBody.getTitle())
        );
        toDoRepository.save(todo);
        return new ToDoResponsePost(
            todo.getId(),
            todo.getTitle(),
            todo.getDescription(),
            todo.isFinished(),
            todo.getAssigneeList(),
            todo.getCreatedDate().getTime(),
            todo.getDueDate().getTime(),
            toDoService.findCategory(requestBody.getTitle())
        );
    }

    /**
     * Updates an existing ToDo entity.
     *
     * @param id the ID of the ToDo to update.
     * @param requestBody the request body containing updated ToDo data.
     * @return the updated ToDo response.
     */
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
            toDoToUpdate.setCategory(toDoService.findCategory(toDoToUpdate.getTitle()));
            toDoRepository.save(toDoToUpdate);
            return new ToDoResponse(
                toDoToUpdate.getId(),
                toDoToUpdate.getTitle(),
                toDoToUpdate.getDescription(),
                toDoToUpdate.isFinished(),
                toDoToUpdate.getAssigneeList(),
                toDoToUpdate.getCreatedDate().getTime(),
                toDoToUpdate.getDueDate().getTime(),
                toDoToUpdate.getFinishedDateMilliSeconds(),
                toDoToUpdate.getCategory()
            );
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("ToDo with ID %s not found!", id));
    }

    /**
     * Deletes a ToDo entity by its ID.
     *
     * @param id the ID of the ToDo to delete.
     * @return a response indicating the deletion status.
     */
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
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("ToDo with ID %s not found!", id));
    }

    /**
     * Downloads all ToDo entities as a CSV file.
     *
     * @return the CSV file as a byte array.
     * @throws IOException if an error occurs during file generation.
     */
    @GetMapping(value = "/csv-downloads/todos", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadToDosCSV() throws IOException {
        List<ToDo> todos = (List<ToDo>) toDoRepository.findAll();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(byteArrayOutputStream, StandardCharsets.UTF_8);
        
        writer.write("id,title,description,finished,assignees,createdDate,dueDate,finishedDate,category\n");
        
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
                formatDate(todo.getFinishedDate()),
                escapeCsv(todo.getCategory())
            ));
        }
        writer.flush();
        
        byte[] csvContent = byteArrayOutputStream.toByteArray();
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=\"todos.csv\"")
            .header("Content-Type", "text/csv")
            .body(csvContent);

    }

    /**
     * Converts a list of Assignee objects into a list of their names in "prename name" format.
     *
     * @param assignees the list of Assignee objects
     * @return a list of names in "prename name" format
     */
    private List<String> getAssigneeNames(List<Assignee> assignees) {
        return assignees.stream()
            .map(assignee -> assignee.getPrename() + " " + assignee.getName())
            .toList();
    }

    /**
     * Escapes a value to be safely used in a CSV file.
     * If the value contains commas, quotes, or newlines, it will be surrounded by quotes.
     * Double quotes within the value are escaped by doubling them.
     *
     * @param value the value to escape
     * @return the escaped value, or an empty string if the input value is null
     */
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        value = value.replace("\"", "\"\""); // Escape double quotes
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            value = "\"" + value + "\""; // Surround with quotes if necessary
        }
        return value;
    }

    /**
     * Formats a Date object into a string representation in the "YYYY-MM-DD" format.
     *
     * @param date the Date object to format
     * @return the formatted date string, or an empty string if the input date is null
     */
    private String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return new java.text.SimpleDateFormat("yyyy-MM-dd").format(date);
    }
}
