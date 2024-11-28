<template>
  <div class="modal-overlay edit-modal">
    <div class="modal">
      <button class="close-btn" @click="closeModal">X</button>

      <h2>Edit To-Do</h2>
      <div>
        <label for="title">Title:</label>
        <input v-model="todo.title" id="title" type="text" />

        <label for="description">Description:</label>
        <input v-model="todo.description" id="description" type="text" />

        <label for="dueDate">Due Date:</label>
        <input v-model="todo.dueDate" id="dueDate" type="date" />

        <label for="assignee">Assignees:</label>
        <!-- Dropdown für Assignees -->
        <select v-model="selectedAssignee" id="addAssignee" class="form-select">
          <option disabled value="">Wählen Sie einen Assignee aus</option>
          <option
              v-for="assignee in availableAssignees"
              :key="assignee.id"
              :value="assignee.id"
          >
            {{ assignee.prename }} {{ assignee.name }}
          </option>
        </select>
        <!-- Hinzufügen-Button -->
        <button @click="addAssignee">Hinzufügen</button>

        <!-- Liste der Assignees mit Entfernen-Button -->
        <div v-if="todo.assigneeList.length > 0">
          <h4>Assigned Assignees:</h4>
          <ul>
            <li v-for="assignee in todo.assigneeList" :key="assignee.id">
              {{ assignee.prename }} {{ assignee.name }}
              <button @click="removeAssignee(assignee.id)">Entfernen</button>
            </li>
          </ul>
        </div>
      </div>

      <button @click="updateToDo">Save Changes</button>
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
      availableAssignees: [], // Alle verfügbaren Assignees
      selectedAssignee: "", // Der aktuell ausgewählte Assignee
    };
  },
  created() {
    this.fetchAvailableAssignees(); // Lädt die verfügbaren Assignees
    // Setzt die initialen Assignees, wenn das To-Do welche hat
    this.todo.assigneeList = this.todo.assigneeList || [];
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
        if (assignee && !this.todo.assigneeList.includes(assignee)) {
          this.todo.assigneeList.push(assignee); // Füge Assignee zur Liste hinzu
        }
        this.selectedAssignee = ""; // Setze das ausgewählte Assignee zurück
      }
    },
    removeAssignee(assigneeId) {
      this.todo.assigneeList = this.todo.assigneeList.filter(
          (assignee) => assignee.id !== assigneeId
      ); // Entferne den Assignee aus der Liste
    },
    updateToDo() {
      // Speichern der To-Do mit der neuen Liste von Assignees
      const updatedToDo = {
        ...this.todo,
        assigneeIdList: this.todo.assigneeList.map((a) => a.id), // Assignee IDs für das Backend
      };
      this.$emit('update', updatedToDo); // Event zum Aktualisieren des To-Dos
    },
  },
};
</script>

