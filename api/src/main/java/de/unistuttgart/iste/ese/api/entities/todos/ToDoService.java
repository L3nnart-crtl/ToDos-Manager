package de.unistuttgart.iste.ese.api.entities.todos;

import de.unistuttgart.iste.ese.api.entities.assignees.Assignee;
import de.unistuttgart.iste.ese.api.entities.assignees.AssigneeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class ToDoService {
    
    AssigneeRepository assigneeRepository;

    @Autowired
    public ToDoService(AssigneeRepository assigneeRepository) {
        this.assigneeRepository = assigneeRepository;
    }
    
    
    public void todoRequestValid(ToDoRequest requestBody) {
        
        List<Long> assigneeIdList = requestBody.getAssigneeIdList();
        //Validation if the array has unique ids and if they exist.

        if (assigneeIdList != null && !this.idListHasUniqueIds(assigneeIdList)) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "AssigneeIdList does not have uniqueIds");


        } else if (!this.idsInIdListExist(assigneeIdList)) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "AssigneeIdList contains ids which do not exist");

            //Validation if the unix Time Stamp is Valid
        } else if (requestBody.getDueDate() != null && requestBody.getDueDate() >= 0 && requestBody.getDueDate() <= 2147483647L) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Invalid unix Timestamp for id %s");
        }
    }
    
    public boolean idListHasUniqueIds(List<Long> assigneeIdList) {
        
        if (assigneeIdList == null) {
            return true;
        }
        Set<Long> uniqueIds = new HashSet<>(assigneeIdList);
        if (uniqueIds.size() != assigneeIdList.size()) {
            return false;
        }
        return true;
    }

    public boolean idsInIdListExist(List<Long> assigneeIdList) {
        if (assigneeIdList == null) {
            return true;
        }
        for (Long id: assigneeIdList) {
            if (!assigneeRepository.existsById(id)) {
                return false;
            }
        }
        return true;
    }
    public List<Assignee> getAssigneesListByIds(List<Long> assigneeIdList) {
        
        List<Assignee> assigneesList = new ArrayList<>();
        if (assigneeIdList == null) {
            return assigneesList;
        }
        for (Long id : assigneeIdList) {
            
            if (assigneeRepository.findById(id).isPresent()) {
                assigneesList.add(assigneeRepository.findById(id).get());
            }
           
        }
        return assigneesList;
    }
    
    public String findCategory(final String todoName) {
        TodoModel todoModel = new TodoModel("src/main/resources/model.pmml");
        todoModel.loadModel();
        
        String category = todoModel.predictClass(todoName);
        todoModel.unloadModel();
        return category;
    }
}
