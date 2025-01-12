<template>
  <h1>Create To-Do</h1>
  <div class="form-group">
    <div class="form-row">
      <div class="form-column">
        <!-- Input field for the title of the to-do -->
        <input v-model="newTodo.title" id="title" type="text" placeholder="Title" required />
      </div>
      <div class="form-column">
        <!-- Button to open the description modal -->
        <button class="add-description" @click="openDescriptionModal">Add Description</button>
      </div>
    </div>

    <div class="form-row">
      <div class="form-column">
        <!-- Input field for the due date -->
        <label for="dueDate">Due Date:</label>
        <input v-model="newTodo.dueDate" id="dueDate" type="date" />
      </div>
    </div>

    <div class="form-row">
      <div class="form-column">
        <!-- Dropdown to select an assignee -->
        <select v-model="selectedAssignee" id="addAssignee" class="form-select">
          <option disabled value="">Choose an assignee</option>
          <option v-for="assignee in availableAssignees" :key="assignee.id" :value="assignee.id">
            {{ assignee.id }} {{ assignee.prename }} {{ assignee.name }}
          </option>
        </select>
      </div>
      <div class="add-button-column">
        <!-- Button to add the selected assignee -->
        <button @click="addAssignee">Add</button>
      </div>
    </div>

    <!-- Display the list of assigned users -->
    <div v-show="true" class="assignees-container">
      <h4>Assignees:</h4>
      <ul>
        <li v-for="assignee in newTodo.assigneeList" :key="assignee.id">
          {{ assignee.id }} {{ assignee.prename }} {{ assignee.name }}
          <button @click="removeAssignee(assignee.id)">Remove</button>
        </li>
      </ul>
    </div>
  </div>

  <!-- Button to submit the new to-do -->
  <button @click="submit">Create</button>

  <!-- Modal for adding description -->
  <div v-if="isDescriptionModalOpen" class="modal-overlay">
    <div class="modal-content">
      <h2>Add Description</h2>
      <!-- Text area to input the description of the to-do -->
      <textarea v-model="newTodo.description" rows="10" placeholder="Enter detailed description"></textarea>
      <div class="modal-actions">
        <!-- Buttons to save or cancel the description -->
        <button class="save" @click="closeDescriptionModal">Save</button>
        <button class="cancel" @click="closeDescriptionModal">Cancel</button>
      </div>
    </div>
  </div>

  <!-- Message Modal -->
  <MessageModal :isOpen="isModalOpen" :message="modalMessage" @close="closeModal" />
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue';
import { EventBus } from '@/components/event-bus';
import axios from 'axios';
import MessageModal from '@/components/Modals/messageModal.vue'; // Import the message modal component

export default defineComponent({
  name: 'TodoCreate',
  components: {
    MessageModal,
  },
  setup() {
    const newTodo = ref({
      title: '',
      description: '',
      dueDate: '',
      assigneeList: [] as any[],
    });
    const selectedAssignee = ref('');
    const availableAssignees = ref<any[]>([]);
    const isDescriptionModalOpen = ref(false);
    const isModalOpen = ref(false);
    const modalMessage = ref('');

    const getAvailableAssignees = async () => {
      try {
        const response = await axios.get('/api/v1/assignees');
        availableAssignees.value = response.data;
      } catch (error) {
        console.error('Error fetching available assignees:', error);
      }
    };

    const addAssignee = () => {
      if (selectedAssignee.value) {
        const assignee = availableAssignees.value.find(
            (a: any) => a.id === selectedAssignee.value
        );
        if (assignee && !newTodo.value.assigneeList.includes(assignee)) {
          newTodo.value.assigneeList.push(assignee);
          selectedAssignee.value = ''; // Reset selection
          EventBus.$emit('assigneeAdded', assignee); // Notify AssigneeList about the new assignee
        }
      }
    };

    const removeAssignee = (assigneeId: number) => {
      newTodo.value.assigneeList = newTodo.value.assigneeList.filter(
          (assignee) => assignee.id !== assigneeId
      );
      EventBus.$emit('assigneeRemoved', assigneeId); // Notify AssigneeList about removal
    };

    const submit = async () => {
      // Title validation
      if (!newTodo.value.title || newTodo.value.title.trim().length < 1) {
        modalMessage.value = 'ERROR: The title must contain at least one character.';
        isModalOpen.value = true;
        return;
      }

      // Assignee ID validation
      const assigneeIds = newTodo.value.assigneeList.map((a) => a.id);
      const uniqueAssigneeIds = new Set(assigneeIds);
      if (assigneeIds.length !== uniqueAssigneeIds.size) {
        modalMessage.value = 'ERROR: Assignee IDs must be unique.';
        isModalOpen.value = true;
        return;
      }

      // Due date validation (Unix Timestamp)
      if (newTodo.value.dueDate && isNaN(new Date(newTodo.value.dueDate).getTime())) {
        modalMessage.value = 'ERROR: Invalid due date.';
        isModalOpen.value = true;
        return;
      }

      try {
        const response = await axios.post('/api/v1/todos', {
          title: newTodo.value.title,
          description: newTodo.value.description,
          assigneeIdList: assigneeIds,
          dueDate: newTodo.value.dueDate ? new Date(newTodo.value.dueDate).getTime() : null,
        });

        // Emit the new to-do via Event Bus
        EventBus.$emit('todoCreated', response.data);
        modalMessage.value = "Todo created!";
        isModalOpen.value = true;

        // Clear form
        newTodo.value = {
          title: "",
          description: "",
          assigneeList: [],
          dueDate: null,
        };

        selectedAssignee.value = ""; // Reset assignee selection
      } catch (error) {
        console.error('Error while creating the to-do:', error);

        // Check for specific error with Duplicate Key
        if (
            error.response &&
            error.response.status === 500 &&
            error.response.data &&
            error.response.data.message.includes("Duplicate entry")
        ) {
          const duplicateIdMatch = error.response.data.message.match(/Duplicate entry '(\d+)'/);
          if (duplicateIdMatch && duplicateIdMatch[1]) {
            const duplicateId = duplicateIdMatch[1];
            modalMessage.value = "ERROR: The assignee with ID " + duplicateId + " is already assigned to another to-do.";
            isModalOpen.value = true;
            return;
          }
        }

        // General error message
        modalMessage.value = 'ERROR: An error occurred while creating the to-do. Please try again.';
        isModalOpen.value = true;
      }
    };

    const openDescriptionModal = () => {
      isDescriptionModalOpen.value = true;
    };

    const closeDescriptionModal = () => {
      isDescriptionModalOpen.value = false;
    };

    const closeModal = () => {
      isModalOpen.value = false;
    };

    // Listen for changes in assignees
    EventBus.$on('assigneeDeleted', (id: number) => {
      availableAssignees.value = availableAssignees.value.filter(
          (assignee) => assignee.id !== id
      );
    });
    EventBus.$on('assigneeUpdated', (updatedAssignee: any) => {
      // Find the assignee in the list of available assignees and update it
      const index = availableAssignees.value.findIndex((a: any) => a.id === updatedAssignee.id);
      if (index !== -1) {
        availableAssignees.value[index] = updatedAssignee;
      }
    });
    EventBus.$on('new-assignee', (newAssignee: any) => {
      availableAssignees.value.push(newAssignee);
    });
    getAvailableAssignees();

    return {
      newTodo,
      selectedAssignee,
      availableAssignees,
      isDescriptionModalOpen,
      isModalOpen,
      modalMessage,
      addAssignee,
      removeAssignee,
      submit,
      openDescriptionModal,
      closeDescriptionModal,
      closeModal,
    };
  },
});
</script>

<style scoped>
/* Container for the entire form */

.assignees-container {
  height: 200px;
}
/* Container for form elements displayed in a column */
.form-group {
  display: flex;
  flex-direction: column;
  gap: 2px; /* Spacing between the form rows */
}

/* Row with form elements displayed side by side */
.form-row {
  display: flex;
  gap: 20px; /* Spacing between the columns */
  align-items: center; /* Vertical alignment */
}

/* Individual columns side by side */
.form-column {
  flex: 1;
}

/* Button column */
.add-button-column {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  margin-top: -10px; /* Align the button vertically */
}

.add-description {
  margin-top: -10px;
}
/* Form fields */
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

/* Button for adding an assignee */
/* Button for creating a to-do */
button {
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

button:hover {
  background-color: #45a049;
}

/* List of assignees */
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

ul li button {
  background-color: #f44336;
  color: white;
  font-size: 14px;
  padding: 5px 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  width: auto;
}

ul li button:hover {
  background-color: #e53935;
}

/* Dropdown for assignees */
select.form-select {
  background-color: #333;
  color: #e0e0e0;
  padding: 10px;
  font-size: 16px;
  border-radius: 4px;
  border: 1px solid #444;
  height: 40px;
}

select.form-select:focus {
  border-color: #4CAF50;
  background-color: #444;
  outline: none;
}
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

.modal-content {
  background-color: #333;
  padding: 20px;
  border-radius: 8px;
  width: 500px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

textarea {
  width: 100%;
  padding: 10px;
  font-size: 16px;
  border-radius: 4px;
  border: 1px solid #ccc;
}

.modal-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
}

.save {
  background-color: #4CAF50;
  color: white;
  padding: 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.cancel {
  background-color: #f44336;
  color: white;
  padding: 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.cancel:hover {
  background-color: #e53935;
}
</style>
