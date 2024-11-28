<template>
  <div class="modal-overlay details-modal">
    <div class="modal">
      <!-- Close button (X) -->
      <button class="close-btn" @click="closeModal">X</button>

      <h2>{{ todo.title }}</h2>
      <p>{{ todo.description }}</p>
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
      this.$emit('delete', id); // Emit 'delete' event to parent
    },
    editToDo() {
      this.$emit('edit', this.todo); // Emit 'edit' event to parent
    },
  },
};
</script>
