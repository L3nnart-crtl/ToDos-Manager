<template>
  <h1>Create To-Do</h1>
  <div class="form-group">
    <div class="form-row">
      <div class="form-column">
        <input v-model="newTodo.title" id="title" type="text" placeholder="Title" required />
      </div>
      <div class="form-column">
        <button class="add-description" @click="openDescriptionModal">Add Description</button>
      </div>
    </div>

    <div class="form-row">
      <div class="form-column">
        <label for="dueDate">Due Date:</label>
        <input v-model="newTodo.dueDate" id="dueDate" type="date" />
      </div>
    </div>

    <div class="form-row">
      <div class="form-column">
        <select v-model="selectedAssignee" id="addAssignee" class="form-select">
          <option disabled value="">Choose an assignee</option>
          <option v-for="assignee in availableAssignees" :key="assignee.id" :value="assignee.id">
            {{ assignee.id }} {{ assignee.prename }} {{ assignee.name }}
          </option>
        </select>
      </div>
      <div class="add-button-column">
        <button @click="addAssignee">Add</button>
      </div>
    </div>

    <div v-show="true" class="assignees-container">
      <h4>Assignees:</h4>
      <ul>
        <li v-for="assignee in newTodo.assigneeList" :key="assignee.id">
          {{assignee.id}} {{ assignee.prename }} {{ assignee.name }}
          <button @click="removeAssignee(assignee.id)">Remove</button>
        </li>
      </ul>
    </div>
  </div>

  <button @click="submit">Create</button>

  <!-- Modal für Beschreibung -->
  <div v-if="isDescriptionModalOpen" class="modal-overlay">
    <div class="modal-content">
      <h2>Add Description</h2>
      <textarea v-model="newTodo.description" rows="10" placeholder="Enter detailed description"></textarea>
      <div class="modal-actions">
        <button class="save" @click="closeDescriptionModal">Save</button>
        <button class="cancel" @click="closeDescriptionModal">Cancel</button>
      </div>
    </div>
  </div>

  <!-- Fehler-Modal -->
  <div v-if="isModalOpen" class="modal-overlay">
    <div class="modal-content">
      <p>{{ modalMessage }}</p>
      <div class="modal-actions">
        <button @click="isModalOpen = false">Close</button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { EventBus } from '@/components/event-bus.js';

export default {
  data() {
    return {
      newTodo: {
        title: "",
        description: "",
        assigneeList: [],
        dueDate: null,
      },
      availableAssignees: [],
      selectedAssignee: "",
      isDescriptionModalOpen: false,
      isModalOpen: false, // Fehler-Modal-Status
      modalMessage: "", // Fehlermeldung
    };
  },
  created() {
    this.fetchAvailableAssignees(); // Lädt die verfügbaren Assignees
  },
  methods: {
    fetchAvailableAssignees() {
      axios
          .get('/api/v1/assignees')
          .then((response) => {
            this.availableAssignees = response.data;
          })
          .catch((error) => {
            console.error('Fehler beim Abrufen der Assignees:', error);
          });
    },
    addAssignee() {
      if (this.selectedAssignee) {
        const assignee = this.availableAssignees.find(
            (a) => a.id === this.selectedAssignee
        );
        if (assignee && !this.newTodo.assigneeList.includes(assignee)) {
          this.newTodo.assigneeList.push(assignee); // Assignee zur Liste hinzufügen
        }
        this.selectedAssignee = ""; // Auswahl zurücksetzen
      }
    },
    removeAssignee(assigneeId) {
      this.newTodo.assigneeList = this.newTodo.assigneeList.filter(
          (assignee) => assignee.id !== assigneeId
      ); // Entferne den Assignee aus der Liste
    },
    async submit() {
      // Title validation
      if (!this.newTodo.title || this.newTodo.title.trim().length < 1) {
        this.modalMessage = 'ERROR: The title must contain at least one character.';
        this.isModalOpen = true;
        return;
      }

      // Assignee ID validation
      const assigneeIds = this.newTodo.assigneeList.map((a) => a.id);
      const uniqueAssigneeIds = new Set(assigneeIds);
      if (assigneeIds.length !== uniqueAssigneeIds.size) {
        this.modalMessage = 'ERROR: Assignee IDs must be unique.';
        this.isModalOpen = true;
        return;
      }

      // Due date validation (Unix Timestamp)
      if (this.newTodo.dueDate && isNaN(new Date(this.newTodo.dueDate).getTime())) {
        this.modalMessage = 'ERROR: Invalid due date.';
        this.isModalOpen = true;
        return;
      }

      try {
        const response = await axios.post('/api/v1/todos', {
          title: this.newTodo.title,
          description: this.newTodo.description,
          assigneeIdList: assigneeIds,
          dueDate: this.newTodo.dueDate ? new Date(this.newTodo.dueDate).getTime() : null,
        });

        // Emit the new to-do via Event Bus
        EventBus.$emit('todoCreated', response.data);
        this.modalMessage = "Todo created!";
        this.isModalOpen = true;

        // Clear form
        this.newTodo = {
          title: "",
          description: "",
          assigneeList: [],
          dueDate: null,
        };

        this.selectedAssignee = ""; // Reset assignee selection
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
            this.modalMessage = "ERROR: The assignee with ID " + duplicateId + " is already assigned to another to-do.";
            this.isModalOpen = true;
            return;
          }
        }

        // General error message
        this.modalMessage = 'ERROR: An error occurred while creating the to-do. Please try again.';
        this.isModalOpen = true;
      }
    }
    ,
    openDescriptionModal() {
      this.isDescriptionModalOpen = true;
    },
    closeDescriptionModal() {
      this.isDescriptionModalOpen = false;
    },
  },
};
</script>


<style scoped>
/* Container für das gesamte Formular */

.assignees-container {
  height: 200px;
}
/* Container für die Formularelemente nebeneinander */
.form-group {
  display: flex;
  flex-direction: column;
  gap: 2px; /* Abstand zwischen den verschiedenen Form-Reihen */
}

/* Reihe mit nebeneinander liegenden Feldern */
.form-row {
  display: flex;
  gap: 20px; /* Abstand zwischen den Spalten */
  align-items: center; /* Vertikale Ausrichtung auf gleicher Höhe */
}

/* Einzelne Spalten nebeneinander */
.form-column {
  flex: 1;
}

/* Button-Spalte */
.add-button-column {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  margin-top: -5px; /* Align the button vertically to the center */
}

.add-description {
  margin-top: -5px;
}
/* Formularfelder */
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
  height: 40px; /* Gleiche Höhe für alle Formularelemente */
}

input:focus,
select:focus {
  border-color: #4CAF50;
  outline: none;
  background-color: #444;
}

/* Button zum Hinzufügen eines Assignees */
/* Button zum Erstellen eines To-Do */
button {
  background-color: #4CAF50;
  color: white;
  font-size: 16px;
  padding: 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  width: 100%; /* Makes the button take up the full width */
  height: 40px; /* Keeps the button height the same as input fields */
}

button:hover {
  background-color: #45a049;
}

/* Liste der Assignees */
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

/* Dropdown-Menü für Assignees */
select.form-select {
  background-color: #333;
  color: #e0e0e0;
  padding: 10px;
  font-size: 16px;
  border-radius: 4px;
  border: 1px solid #444;
  height: 40px; /* Gleiche Höhe wie das Eingabefeld */
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
