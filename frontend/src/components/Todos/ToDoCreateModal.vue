<template>
  <div class="create-todo-form">
    <h2>Create new To-Do</h2>

    <div>
      <label for="title">Titel:</label>
      <input v-model="newTodo.title" id="title" type="text" required />

      <label for="description">Description:</label>
      <input v-model="newTodo.description" id="description" type="text" />

      <label for="dueDate">Due Date:</label>
      <input v-model="newTodo.dueDate" id="dueDate" type="date" />

      <label for="assignee">Assignees:</label>
      <select v-model="selectedAssignee" id="addAssignee" class="form-select">
        <option disabled value="">Choose an assignee</option>
        <option v-for="assignee in availableAssignees" :key="assignee.id" :value="assignee.id">
          {{ assignee.prename }} {{ assignee.name }}
        </option>
      </select>

      <button @click="addAssignee">Add</button>

      <div v-if="newTodo.assigneeList.length > 0">
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
  </div>
</template>

<script>
import axios from 'axios';
import {EventBus} from '@/components/event-bus.js';

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
      // Validierung des Titels
      if (!this.newTodo.title || this.newTodo.title.trim().length < 1) {
        alert('Der Titel muss mindestens ein Zeichen enthalten.');
        return;
      }

      // Validierung der Assignee-IDs
      const assigneeIds = this.newTodo.assigneeList.map((a) => a.id);
      const uniqueAssigneeIds = new Set(assigneeIds);
      if (assigneeIds.length !== uniqueAssigneeIds.size) {
        alert('Assignee-IDs müssen einzigartig sein.');
        return;
      }

      // Validierung des Fälligkeitsdatums (Unix Timestamp)
      if (this.newTodo.dueDate && isNaN(new Date(this.newTodo.dueDate).getTime())) {
        alert('Ungültiges Fälligkeitsdatum.');
        return;
      }

      try {
        const response = await axios.post('/api/v1/todos', {
          title: this.newTodo.title,
          description: this.newTodo.description,
          assigneeIdList: assigneeIds,
          dueDate: this.newTodo.dueDate ? new Date(this.newTodo.dueDate).getTime() : null,
        });

        // Sende das neue To-Do über den Event Bus
        EventBus.$emit('todoCreated', response.data);

        // Formular leeren
        this.newTodo = {
          title: "",
          description: "",
          assigneeList: [],
          dueDate: null,
        };

        this.selectedAssignee = ""; // Assignee-Auswahl zurücksetzen
      } catch (error) {
        console.error('Fehler beim Erstellen des To-Dos:', error);
      }
    },
  },
};
</script>
<style scoped>
/* Gesamter Container */
.create-todo-form {
  background-color: #1e1e1e; /* Dunkler Hintergrund für das Formular */
  color: #e0e0e0; /* Helle Textfarbe */
  padding: 20px;
  border-radius: 8px;
  max-width: 600px;
  margin: 20px auto;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
}

/* Titel */
h2 {
  font-size: 24px;
  color: #e0e0e0;
  margin-bottom: 20px;
}

/* Label */
label {
  font-size: 16px;
  color: #ccc; /* Helle Farbe für die Labels */
  margin-bottom: 5px;
  display: block;
}

/* Formularelemente */
input,
select {
  width: 100%;
  padding: 12px;
  font-size: 16px;
  background-color: #333; /* Dunkler Hintergrund für Eingabefelder */
  border: 1px solid #444; /* Dunkle Umrandung */
  border-radius: 4px;
  color: #e0e0e0; /* Helle Textfarbe */
  margin-bottom: 20px;
}

input:focus,
select:focus {
  border-color: #4CAF50; /* Grüne Umrandung bei Fokus */
  outline: none;
  background-color: #444; /* Etwas dunklerer Hintergrund bei Fokus */
}

/* Button zum Hinzufügen eines Assignees */
button {
  background-color: #4CAF50; /* Grüner Button */
  color: white;
  font-size: 16px;
  padding: 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  width: 100%;
}

button:hover {
  background-color: #45a049; /* Etwas dunkleres Grün bei Hover */
}

button:disabled {
  background-color: #666; /* Grauer Button, wenn deaktiviert */
  cursor: not-allowed;
}

/* Fehlernachricht */
.error-message {
  color: red;
  font-size: 14px;
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
}

ul li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  border-bottom: 1px solid #444; /* Dunkler Rand für Listenelemente */
}

ul li button {
  background-color: #f44336; /* Roter Button zum Entfernen */
  color: white;
  font-size: 14px;
  padding: 5px 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

ul li button:hover {
  background-color: #e53935; /* Etwas dunkleres Rot bei Hover */
}

/* Dropdown-Menü für Assignees */
select.form-select {
  background-color: #333;
  color: #e0e0e0;
  padding: 10px;
  font-size: 16px;
  border-radius: 4px;
  border: 1px solid #444;
}

select.form-select:focus {
  border-color: #4CAF50;
  background-color: #444;
  outline: none;
}

/* Anpassungen für das Layout der Eingabefelder */
div {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

/* Weitere optische Feinanpassungen */
.create-todo-form input,
.create-todo-form select {
  margin-bottom: 15px; /* Abstand zwischen den Eingabefeldern */
}

.create-todo-form button {
  margin-top: 10px; /* Abstand zum unteren Button */
}

</style>
