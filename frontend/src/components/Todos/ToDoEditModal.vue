<template>
  <div class="modal-overlay edit-modal">
    <div class="modal">
      <button class="close-btn" @click="closeModal">X</button>

      <h2>Edit To-Do</h2>
      <div class="form-group">
        <label for="title">Title:</label>
        <input v-model="editedTodo.title" id="title" type="text" />

        <label for="description">Description:</label>
        <input v-model="editedTodo.description" id="description" type="text" />

        <label for="dueDate">Due Date:</label>
        <input v-model="editedTodo.dueDate" id="dueDate" type="date" />

        <label for="assignee">Assignees:</label>
        <select v-model="selectedAssignee" id="addAssignee" class="form-select">
          <option disabled value="">Choose an assignee</option>
          <option
              v-for="assignee in availableAssignees"
              :key="assignee.id"
              :value="assignee.id"
          >
            {{ assignee.id }} {{ assignee.prename }} {{ assignee.name }}
          </option>
        </select>
        <button @click="addAssignee" class="add-assignee-button">Add</button>

        <div v-if="editedTodo.assigneeList.length > 0">
          <h4>Assigned Assignees:</h4>
          <ul>
            <li v-for="assignee in editedTodo.assigneeList" :key="assignee.id">
              {{ assignee.id }} {{ assignee.prename }} {{ assignee.name }}
              <button @click="removeAssignee(assignee.id)" class="remove-assignee-button">
                Remove
              </button>
            </li>
          </ul>
        </div>
      </div>

      <button @click="updateToDo" class="save-changes-button">Save Changes</button>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  props: {
    todo: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      availableAssignees: [],
      selectedAssignee: '',
      // Creating a local copy of the todo data
      editedTodo: {...this.todo, assigneeList: [...this.todo.assigneeList]},
    };
  },
  created() {
    this.fetchAvailableAssignees();
  },
  methods: {
    fetchAvailableAssignees() {
      axios
          .get('/api/v1/assignees')
          .then((response) => {
            this.availableAssignees = response.data;
          })
          .catch((error) => {
            console.error('Error fetching assignees:', error);
          });
    },
    closeModal() {
      this.$emit('close');
    },
    addAssignee() {
      if (this.selectedAssignee) {
        const assignee = this.availableAssignees.find(
            (a) => a.id === this.selectedAssignee
        );
        if (assignee && !this.editedTodo.assigneeList.includes(assignee)) {
          this.editedTodo.assigneeList.push(assignee);
        }
        this.selectedAssignee = '';
      }
    },
    removeAssignee(assigneeId) {
      this.editedTodo.assigneeList = this.editedTodo.assigneeList.filter(
          (assignee) => assignee.id !== assigneeId
      );
    },
    updateToDo() {
      const updatedToDo = {
        ...this.editedTodo,
        dueDate: new Date(this.editedTodo.dueDate).getTime(),
        assigneeIdList: this.editedTodo.assigneeList.map((a) => a.id),
      };

      // Emit 'update' event to parent component
      this.$emit('update', updatedToDo);

      // Emit event to update details modal
      this.$root.$emit('todo-updated', updatedToDo); // This will notify the details modal
    }
  },
};
</script>

<style scoped>
/* Container for the entire form */
.form-group {
  display: flex;
  flex-direction: column;
  gap: 10px; /* Space between form rows */
}



/* Label and input fields */
input,
select {
  width: 100%;
  padding: 12px;
  font-size: 16px;
  background-color: #333;
  border: 1px solid #444;
  border-radius: 4px;
  color: #e0e0e0;
  margin-bottom: 10px;
  height: 40px;
}

input:focus,
select:focus {
  border-color: #4CAF50;
  outline: none;
  background-color: #444;
}

/* Add Assignee Button */
.add-assignee-button {
  background-color: #4CAF50;
  color: white;
  font-size: 16px;
  padding: 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  width: 100%;
  height: 40px;
}

.add-assignee-button:hover {
  background-color: #45a049;
}

/* Remove Assignee Button */
.remove-assignee-button {
  background-color: #f44336;
  color: white;
  font-size: 14px;
  padding: 5px 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.remove-assignee-button:hover {
  background-color: #e53935;
}

/* Save Changes Button */
.save-changes-button {
  background-color: #4CAF50;
  color: white;
  font-size: 16px;
  padding: 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  width: 100%;
  height: 40px;
}

.save-changes-button:hover {
  background-color: #45a049;
}

/* Modal and Overlay Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal {
  background-color: #333;
  padding: 20px;
  border-radius: 8px;
  width: 500px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  color: #e0e0e0;
}

h2 {
  font-size: 1.5em;
  margin-bottom: 15px;
  text-align: center;
}

h4 {
  font-size: 18px;
  color: #e0e0e0;
  margin-top: 20px;
  margin-bottom: 10px;
}

ul {
  list-style: none;
  padding: 0;
  max-height: 125px;
  overflow-y: auto;
}

ul li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 5px;
  border-bottom: 1px solid #444;
}

button {
  margin-bottom: 5px; /* Space between buttons */
}

.close-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  background-color: transparent;
  color: #f44336;
  font-size: 20px;
  border: none;
  cursor: pointer;
}
</style>
