package de.unistuttgart.iste.ese.api.config;

import de.unistuttgart.iste.ese.api.entities.assignees.Assignee;
import de.unistuttgart.iste.ese.api.entities.assignees.AssigneeRepository;
import de.unistuttgart.iste.ese.api.entities.todos.ToDo;
import de.unistuttgart.iste.ese.api.entities.todos.ToDoRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataLoader {

    @Autowired
    private AssigneeRepository assigneeRepository;

    @Autowired
    private ToDoRepository toDoRepository;

    @PostConstruct
    public void loadData() {
        // Delete existing data
        assigneeRepository.deleteAll();
        toDoRepository.deleteAll();

        // Create assignees
        Assignee assignee1 = new Assignee("John", "Doe", "john.doe@uni-stuttgart.de");
        Assignee assignee2 = new Assignee("Jane", "Doe", "jane.doe@uni-stuttgart.de");
        Assignee assignee3 = new Assignee("Max", "Mustermann", "max.mustermann@uni-stuttgart.de");

        // Save assignees to the repository
        assignee1 = assigneeRepository.save(assignee1);
        assignee2 = assigneeRepository.save(assignee2);
        assignee3 = assigneeRepository.save(assignee3);

        // Create todos and assign assignees
        List<Assignee> assigneesForTodo1 = new ArrayList<>();
        assigneesForTodo1.add(assignee1);
        assigneesForTodo1.add(assignee2);

        List<Assignee> assigneesForTodo2 = new ArrayList<>();
        assigneesForTodo2.add(assignee3);

        ToDo todo1 = new ToDo("Fix bugs", "Fix all reported bugs in the system", assigneesForTodo1, 1672531199000L); // Unix timestamp for a date
        ToDo todo2 = new ToDo("Prepare report", "Prepare the quarterly report for the team", assigneesForTodo2, 1673027599000L); // Unix timestamp for a date

        // Save todos to the repository
        toDoRepository.save(todo1);
        toDoRepository.save(todo2);
    }
}
