package de.unistuttgart.iste.ese.api.entities.todos;

import de.unistuttgart.iste.ese.api.entities.assignees.Assignee;
import de.unistuttgart.iste.ese.api.entities.assignees.AssigneeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

/**
 * Service class responsible for handling ToDo-related operations, such as validating
 * requests, checking assignee validity, and categorizing tasks.
 */
@Service
public class ToDoService {

    private final AssigneeRepository assigneeRepository;

    /**
     * Constructor for ToDoService. Injects the AssigneeRepository to interact with assignee data.
     *
     * @param assigneeRepository the AssigneeRepository to be used by this service
     */
    @Autowired
    public ToDoService(AssigneeRepository assigneeRepository) {
        this.assigneeRepository = assigneeRepository;
    }

    /**
     * Validates the ToDo request by checking the uniqueness of assignee IDs, their existence,
     * and the validity of the due date.
     *
     * @param requestBody the ToDoRequest object to be validated
     * @throws ResponseStatusException if any validation fails, throwing a BAD_REQUEST status with a descriptive message
     */
    public void todoRequestValid(ToDoRequest requestBody) {

        List<Long> assigneeIdList = requestBody.getAssigneeIdList();

        // Validate if the array has unique ids and if they exist
        if (assigneeIdList != null && !this.idListHasUniqueIds(assigneeIdList)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "AssigneeIdList does not have uniqueIds");
        } else if (!this.idsInIdListExist(assigneeIdList)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "AssigneeIdList contains ids which do not exist");
        }

        // Validate if the unix Time Stamp is valid
        else if (requestBody.getDueDate() != null && requestBody.getDueDate() >= 0 && requestBody.getDueDate() <= 2147483647L) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid unix Timestamp for id %s");
        }
    }

    /**
     * Checks whether a list of assignee IDs contains only unique values.
     *
     * @param assigneeIdList the list of assignee IDs to check
     * @return true if all IDs are unique, false otherwise
     */
    public boolean idListHasUniqueIds(List<Long> assigneeIdList) {
        if (assigneeIdList == null) {
            return true;
        }
        Set<Long> uniqueIds = new HashSet<>(assigneeIdList);
        return uniqueIds.size() == assigneeIdList.size();
    }

    /**
     * Checks whether all assignee IDs in the given list exist in the repository.
     *
     * @param assigneeIdList the list of assignee IDs to check
     * @return true if all IDs exist in the repository, false otherwise
     */
    public boolean idsInIdListExist(List<Long> assigneeIdList) {
        if (assigneeIdList == null) {
            return true;
        }
        for (Long id : assigneeIdList) {
            if (!assigneeRepository.existsById(id)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Retrieves a list of Assignee objects based on a list of assignee IDs.
     *
     * @param assigneeIdList the list of assignee IDs
     * @return a list of Assignee objects corresponding to the provided IDs
     */
    public List<Assignee> getAssigneesListByIds(List<Long> assigneeIdList) {
        List<Assignee> assigneesList = new ArrayList<>();
        if (assigneeIdList == null) {
            return assigneesList;
        }
        for (Long id : assigneeIdList) {
            assigneeRepository.findById(id).ifPresent(assigneesList::add);
        }
        return assigneesList;
    }

    /**
     * Predicts the category of a ToDo item based on its name using a machine learning model.
     *
     * @param todoName the name of the ToDo item to classify
     * @return the predicted category of the ToDo item
     */
    public String findCategory(final String todoName) {
        TodoModel todoModel = new TodoModel("src/main/resources/model.pmml");
        todoModel.loadModel();

        String category = todoModel.predictClass(todoName);
        todoModel.unloadModel();
        return category;
    }
}