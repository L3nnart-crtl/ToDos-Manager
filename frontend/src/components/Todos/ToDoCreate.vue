<template>

    <h1>Create To-Do</h1>
    <div class="form-group">
      <div class="form-row">
        <div class="form-column">
          <label for="title">Title:</label>
          <input v-model="newTodo.title" id="title" type="text" required />
        </div>
        <div class="form-column">
          <label for="description">Description:</label>
          <input v-model="newTodo.description" id="description" type="text" />
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
        <!-- Button Column: Do not let it take full width -->
        <div class="add-button-column">
          <button @click="addAssignee">Add</button>
        </div>
      </div>

      <div v-show="true" class="assignees-container">
        <h4>Assignees:</h4>
        <ul>
          <li v-for="assignee in newTodo.assigneeList" :key="assignee.id">
            {{ assignee.prename }} {{ assignee.name }}
            <button @click="removeAssignee(assignee.id)">Remove</button>
          </li>
        </ul>
      </div>
    </div>

    <button @click="submit">Create</button>
</template>

<script>
import axios from 'axios';
import { EventBus } from '@/components/event-bus.js';
import { watch } from 'vue';

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
    };
  },
  created() {
    this.fetchAvailableAssignees(); // Initiales Abrufen der Assignees
    this.watchAssignees(); // Starte den Watcher für Assignees
  },
  methods: {
    // Überwacht den EventBus und aktualisiert availableAssignees, wenn sich etwas ändert
    watchAssignees() {
      EventBus.$on('new-assignee', (newAssignee) => {
        if (newAssignee) {
          this.addAssigneeToList(newAssignee); // Add new assignee to available list
        }
      });
    },

    // Lädt die verfügbaren Assignees
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

    // Assignee zur Liste der verfügbaren Assignees hinzufügen
    addAssigneeToList(newAssignee) {
      if (!this.availableAssignees.some((a) => a.id === newAssignee.id)) {
        this.availableAssignees.push(newAssignee); // Ensure no duplicates
      }
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
      // Form submission code here...
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

</style>
