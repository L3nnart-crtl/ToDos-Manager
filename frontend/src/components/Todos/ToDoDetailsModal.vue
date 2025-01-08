<template>
  <div class="modal-overlay details-modal">
    <div class="modal">
      <!-- Close button (X) -->
      <button class="close-btn" @click="closeModal">X</button>

      <h2>Title: {{ todo.title }}</h2>
      <h2>Id: {{ todo.id }}</h2>
      <p>Description{{ todo.description }}</p>
      <p>Due Date: {{ new Date(todo.dueDate).toLocaleString() }}</p>
      <p>Status: {{ todo.finished ? "Completed" : "In Progress" }}</p>

      <!-- Assignees -->
      <div>
        <h3>Assignees</h3>
        <ul>
          <li v-for="assignee in todo.assigneeList" :key="assignee.id">
            {{ assignee.prename }} {{ assignee.name }} ({{ assignee.email }})
          </li>
        </ul>
      </div>

      <!-- Action buttons -->
      <button @click="deleteToDo(todo.id)">Delete</button>
      <button @click="editToDo">Edit</button>
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
