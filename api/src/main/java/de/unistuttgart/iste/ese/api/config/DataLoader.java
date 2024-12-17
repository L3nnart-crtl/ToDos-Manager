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

        // Create 30 assignees with emails ending with @uni-stuttgart.de
        Assignee assignee1 = new Assignee("John", "Doe", "john.doe@uni-stuttgart.de");
        Assignee assignee2 = new Assignee("Jane", "Doe", "jane.doe@uni-stuttgart.de");
        Assignee assignee3 = new Assignee("Max", "Mustermann", "max.mustermann@uni-stuttgart.de");
        Assignee assignee4 = new Assignee("Anna", "Schmidt", "anna.schmidt@uni-stuttgart.de");
        Assignee assignee5 = new Assignee("Peter", "Müller", "peter.mueller@uni-stuttgart.de");
        Assignee assignee6 = new Assignee("Sophia", "Bauer", "sophia.bauer@uni-stuttgart.de");
        Assignee assignee7 = new Assignee("Lukas", "Klein", "lukas.klein@uni-stuttgart.de");
        Assignee assignee8 = new Assignee("Emily", "Schneider", "emily.schneider@uni-stuttgart.de");
        Assignee assignee9 = new Assignee("Tim", "Fischer", "tim.fischer@uni-stuttgart.de");
        Assignee assignee10 = new Assignee("Laura", "Weber", "laura.weber@uni-stuttgart.de");
        Assignee assignee11 = new Assignee("Daniel", "Schulz", "daniel.schulz@uni-stuttgart.de");
        Assignee assignee12 = new Assignee("Julia", "Zimmermann", "julia.zimmermann@uni-stuttgart.de");
        Assignee assignee13 = new Assignee("Michael", "Becker", "michael.becker@uni-stuttgart.de");
        Assignee assignee14 = new Assignee("Lena", "Hoffmann", "lena.hoffmann@uni-stuttgart.de");
        Assignee assignee15 = new Assignee("Alexander", "Meyer", "alexander.meyer@uni-stuttgart.de");
        Assignee assignee16 = new Assignee("Sophie", "Schneider", "sophie.schneider@uni-stuttgart.de");
        Assignee assignee17 = new Assignee("Nico", "Koch", "nico.koch@uni-stuttgart.de");
        Assignee assignee18 = new Assignee("Maja", "Richter", "maja.richter@uni-stuttgart.de");
        Assignee assignee19 = new Assignee("Felix", "Wagner", "felix.wagner@uni-stuttgart.de");
        Assignee assignee20 = new Assignee("Carla", "Vogel", "carla.vogel@uni-stuttgart.de");
        Assignee assignee21 = new Assignee("Andreas", "Peters", "andreas.peters@uni-stuttgart.de");
        Assignee assignee22 = new Assignee("Emma", "Jäger", "emma.jaeger@uni-stuttgart.de");
        Assignee assignee23 = new Assignee("Jonas", "Kramer", "jonas.kramer@uni-stuttgart.de");
        Assignee assignee24 = new Assignee("Eva", "Lange", "eva.lange@uni-stuttgart.de");
        Assignee assignee25 = new Assignee("Tom", "Friedrich", "tom.friedrich@uni-stuttgart.de");
        Assignee assignee26 = new Assignee("Lisa", "Hermann", "lisa.hermann@uni-stuttgart.de");
        Assignee assignee27 = new Assignee("Stefan", "Hartmann", "stefan.hartmann@uni-stuttgart.de");
        Assignee assignee28 = new Assignee("Nina", "Schmitt", "nina.schmitt@uni-stuttgart.de");
        Assignee assignee29 = new Assignee("Oliver", "Neumann", "oliver.neumann@uni-stuttgart.de");
        Assignee assignee30 = new Assignee("Anna", "Wald", "anna.wald@uni-stuttgart.de");

        // Save assignees to the repository
        assignee1 = assigneeRepository.save(assignee1);
        assignee2 = assigneeRepository.save(assignee2);
        assignee3 = assigneeRepository.save(assignee3);
        assignee4 = assigneeRepository.save(assignee4);
        assignee5 = assigneeRepository.save(assignee5);
        assignee6 = assigneeRepository.save(assignee6);
        assignee7 = assigneeRepository.save(assignee7);
        assignee8 = assigneeRepository.save(assignee8);
        assignee9 = assigneeRepository.save(assignee9);
        assignee10 = assigneeRepository.save(assignee10);
        assignee11 = assigneeRepository.save(assignee11);
        assignee12 = assigneeRepository.save(assignee12);
        assignee13 = assigneeRepository.save(assignee13);
        assignee14 = assigneeRepository.save(assignee14);
        assignee15 = assigneeRepository.save(assignee15);
        assignee16 = assigneeRepository.save(assignee16);
        assignee17 = assigneeRepository.save(assignee17);
        assignee18 = assigneeRepository.save(assignee18);
        assignee19 = assigneeRepository.save(assignee19);
        assignee20 = assigneeRepository.save(assignee20);
        assignee21 = assigneeRepository.save(assignee21);
        assignee22 = assigneeRepository.save(assignee22);
        assignee23 = assigneeRepository.save(assignee23);
        assignee24 = assigneeRepository.save(assignee24);
        assignee25 = assigneeRepository.save(assignee25);
        assignee26 = assigneeRepository.save(assignee26);
        assignee27 = assigneeRepository.save(assignee27);
        assignee28 = assigneeRepository.save(assignee28);
        assignee29 = assigneeRepository.save(assignee29);
        assignee30 = assigneeRepository.save(assignee30);

        // Create 12 todos and assign assignees
        List<Assignee> assigneesForTodo1 = new ArrayList<>();
        assigneesForTodo1.add(assignee1);
        assigneesForTodo1.add(assignee2);

        List<Assignee> assigneesForTodo2 = new ArrayList<>();
        assigneesForTodo2.add(assignee3);

        List<Assignee> assigneesForTodo3 = new ArrayList<>();
        assigneesForTodo3.add(assignee4);
        assigneesForTodo3.add(assignee5);

        List<Assignee> assigneesForTodo4 = new ArrayList<>();
        assigneesForTodo4.add(assignee6);
        assigneesForTodo4.add(assignee7);

        List<Assignee> assigneesForTodo5 = new ArrayList<>();
        assigneesForTodo5.add(assignee8);

        List<Assignee> assigneesForTodo6 = new ArrayList<>();
        assigneesForTodo6.add(assignee9);
        assigneesForTodo6.add(assignee10);

        List<Assignee> assigneesForTodo7 = new ArrayList<>();
        assigneesForTodo7.add(assignee11);
        assigneesForTodo7.add(assignee12);

        List<Assignee> assigneesForTodo8 = new ArrayList<>();
        assigneesForTodo8.add(assignee13);

        List<Assignee> assigneesForTodo9 = new ArrayList<>();
        assigneesForTodo9.add(assignee14);

        List<Assignee> assigneesForTodo10 = new ArrayList<>();
        assigneesForTodo10.add(assignee15);
        assigneesForTodo10.add(assignee16);

        List<Assignee> assigneesForTodo11 = new ArrayList<>();
        assigneesForTodo11.add(assignee17);
        assigneesForTodo11.add(assignee18);

        List<Assignee> assigneesForTodo12 = new ArrayList<>();
        assigneesForTodo12.add(assignee19);
        assigneesForTodo12.add(assignee20);

        ToDo todo1 = new ToDo("Fix bugs", "Fix all reported bugs in the system", assigneesForTodo1, 1672531199000L);
        ToDo todo2 = new ToDo("Prepare report", "Prepare the quarterly report for the team", assigneesForTodo2, 1673027599000L);
        ToDo todo3 = new ToDo("Design new feature", "Design the new user profile feature", assigneesForTodo3, 1673617599000L);
        ToDo todo4 = new ToDo("Update documentation", "Update the user guide documentation", assigneesForTodo4, 1674207599000L);
        ToDo todo5 = new ToDo("Client meeting", "Meet with the client to discuss project progress", assigneesForTodo5, 1674897599000L);
        ToDo todo6 = new ToDo("Team building event", "Organize a team building event", assigneesForTodo6, 1675487599000L);
        ToDo todo7 = new ToDo("Code review", "Review the pull requests for the new feature", assigneesForTodo7, 1676077599000L);
        ToDo todo8 = new ToDo("Conduct training", "Conduct a training session for new team members", assigneesForTodo8, 1676667599000L);
        ToDo todo9 = new ToDo("Marketing strategy", "Develop a marketing strategy for the new product", assigneesForTodo9, 1677257599000L);
        ToDo todo10 = new ToDo("Product design", "Design the product UI/UX", assigneesForTodo10, 1677847599000L);
        ToDo todo11 = new ToDo("Budget planning", "Create a budget plan for the next quarter", assigneesForTodo11, 1678437599000L);
        ToDo todo12 = new ToDo("Website launch", "Launch the new company website", assigneesForTodo12, 1679027599000L);

        // Save todos to the repository
        toDoRepository.save(todo1);
        toDoRepository.save(todo2);
        toDoRepository.save(todo3);
        toDoRepository.save(todo4);
        toDoRepository.save(todo5);
        toDoRepository.save(todo6);
        toDoRepository.save(todo7);
        toDoRepository.save(todo8);
        toDoRepository.save(todo9);
        toDoRepository.save(todo10);
        toDoRepository.save(todo11);
        toDoRepository.save(todo12);
    }
}
