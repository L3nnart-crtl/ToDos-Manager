<template>
  <div class="modal-overlay details-modal">
    <div class="modal">
      <!-- Close button (X) -->
      <button class="close-btn" @click="closeModal">X</button>

      <div class="todo-details">
        <h2 class="todo-title">{{ todo.title }}</h2>

        <div class="todo-info">
          <p><strong>Id:</strong> {{ todo.id }}</p>
          <p><strong>Description:</strong> {{ todo.description }}</p>
          <p><strong>Category:</strong> {{ todo.category }}</p>
          <p><strong>Due Date:</strong> {{ new Date(todo.dueDate).toLocaleString() }}</p>
          <p><strong>Status:</strong> {{ todo.finished ? "Completed" : "In Progress" }}</p>
        </div>

        <!-- Assignees -->
        <div class="assignees">
          <h3>Assignees</h3>
          <ul>
            <li v-for="assignee in todo.assigneeList" :key="assignee.id">
              <span class="assignee-name">{{ assignee.prename }} {{ assignee.name }}</span>
              <span class="assignee-email">({{ assignee.email }})</span>
            </li>
          </ul>
        </div>
      </div>

      <!-- Action buttons -->
      <button class="delete" @click="deleteToDo(todo.id)">Delete</button>
      <button class="edit" @click="editToDo">Edit</button>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  props: {
    todo: {
      type: Object,
      required: true,
    },
  },
  methods: {
    closeModal() {
      this.$emit('close'); // Emit 'close' event to parent
    },
    deleteToDo(id) {
      const confirmed = window.confirm('Möchte Sie das Todo wirklich löschen?');
      if (confirmed) {
        try {
          this.$emit('delete', id); // Emit 'delete' event to parent
        } catch (error) {
          console.error('Fehler beim Löschen des Todos:', error);
        }
      } else {
        console.log('Delete operation was canceled.');
      }

    },
    editToDo() {
      this.$emit('edit', this.todo); // Emit 'edit' event to parent
    },
  },
};
</script>
<style>
.todo-details {
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.todo-title {
  font-size: 1.5em;
  margin-bottom: 15px;
}

.todo-info p {
  font-size: 1.1em;
  line-height: 1.5;
  margin: 5px 0;
}


.assignees {
  margin-top: 20px;
}

.assignees h3 {
  font-size: 1.2em;
  margin-bottom: 10px;
}

.assignees ul {
  list-style-type: none;
  padding: 0;
}

.assignees li {
  font-size: 1.1em;
  margin-bottom: 8px;
}

.assignee-name {
  font-weight: bold;
}

.assignee-email {
  font-style: italic;
}
button {
  margin-bottom: 5px; /* Abstand zwischen den Buttons */
}

.edit {
  background-color: #4CAF50; /* Grün für den Edit-Button */
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
}

.edit:hover {
  background-color: #45a049; /* Dunkleres Grün beim Hover */
}

.delete {
  background-color: #f44336; /* Rot für den Delete-Button */
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
}

.delete:hover {
  background-color: #e53935; /* Dunkleres Rot beim Hover */
}

button:focus {
  outline: none;
}


</style>
