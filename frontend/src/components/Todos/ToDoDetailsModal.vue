<template>
  <div class="modal-overlay details-modal">
    <div class="modal">
      <!-- Close button (X) -->
      <button class="close-btn" @click="closeModal">X</button>

      <div class="todo-details">
        <h2 class="todo-title">{{ todo.title }}</h2>

        <div class="todo-info">
          <p><strong>ID:</strong> {{ todo.id }}</p>
          <p><strong>Description:</strong> {{ todo.description }}</p>
          <p><strong>Category:</strong> {{ todo.category }}</p>
          <p><strong>Due Date:</strong> {{ formatDate(todo.dueDate) }}</p>
          <p><strong>Finished Date:</strong>
            {{ todo.finishedDate ? formatDate(todo.finishedDate) : "In Progress" }}
          </p>
        </div>

        <!-- Assignees -->
        <div class="assignees">
          <h3>Assignees</h3>
          <ul>
            <li v-for="assignee in todo.assigneeList" :key="assignee.id" class="assignee-item">
              <span class="assignee-id">{{ assignee.id }}</span>
              <span class="assignee-name">{{ assignee.prename }} {{ assignee.name }}</span>
              <span class="assignee-email">{{ assignee.email }}</span>
            </li>
          </ul>
        </div>
      </div>

      <!-- Action Buttons -->
      <div class="action-buttons">
        <button class="delete" @click="openConfirmModal">Delete</button>
        <button class="edit" @click="editToDo">Edit</button>
      </div>
    </div>
  </div>

  <!-- Confirm Modal -->
  <ConfirmModal
      v-if="isConfirmModalOpen"
      :isOpen="isConfirmModalOpen"
      :message="confirmMessage"
      @confirm="deleteToDo"
      @cancel="closeConfirmModal"
      @close="closeConfirmModal"
  />
</template>

<script>
import ConfirmModal from "@/components/Modals/confirmModal.vue";
import { EventBus } from "@/components/event-bus.js";

export default {
  props: {
    todo: {
      type: Object,
      required: true,
    },
  },
  components: {
    ConfirmModal,
  },
  data() {
    return {
      isConfirmModalOpen: false,
      confirmMessage: "Are you sure you want to delete this To-Do?", // Confirm message in English
    };
  },
  created() {
    EventBus.$on("todo-updated", this.updateTodoDetails);
  },
  beforeDestroy() {
    EventBus.$off("todo-updated", this.updateTodoDetails);
  },
  methods: {
    // Format the date to a human-readable string
    formatDate(date) {
      return new Date(date).toLocaleString();
    },
    // Update the todo details when the todo is updated
    updateTodoDetails(updatedTodo) {
      this.todo = updatedTodo;
    },
    // Close the modal
    closeModal() {
      this.$emit("close");
    },
    // Open the confirmation modal
    openConfirmModal() {
      this.isConfirmModalOpen = true;
    },
    // Close the confirmation modal
    closeConfirmModal() {
      this.isConfirmModalOpen = false;
    },
    // Delete the todo and close the confirmation modal
    deleteToDo() {
      this.$emit("delete", this.todo.id);
      this.closeConfirmModal();
    },
    // Edit the todo
    editToDo() {
      this.$emit("edit", this.todo);
    },
  },
};
</script>

<style scoped>
/* Modal Overlay */
.modal-overlay {
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

/* Modal */
.modal {
  background: #333;
  padding: 20px;
  border-radius: 10px;
  width: 480px;
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.2);
  color: #f0f0f0;
  position: relative;
}

.todo-title {
  font-size: 1.8em;
  text-align: center;
  margin-bottom: 20px;
  margin-right: 40px;
  color: #ccc;
  word-break: break-word; /* Allow breaking long words onto the next line */
  display: flex;
  flex-direction: row; /* Keep the layout horizontally */
  gap: 2px; /* Optional: Adjust font size */
}

.todo-info p {
  margin: 8px 0;
  font-size: 1.1em;
  line-height: 1.4;
}

/* Assignees */
.assignees {
  margin-top: 0px;
  max-height: 200px; /* Set a max height for the container */
  overflow-y: auto; /* Enable vertical scrolling when the content exceeds max height */
  padding-top: 0px; /* Adds space between header and the first list item */
}

.assignees h3 {
  font-size: 1.2em;
  margin-bottom: 12px;
  position: sticky; /* Make the header stick to the top */
  top: 0; /* Position it at the top of the container */
  background-color: #333; /* Same background as modal for seamless look */
  z-index: 1; /* Ensure it stays on top of the list items */
  padding: 5px 0; /* Add some padding for spacing */
}

.assignees ul {
  list-style: none;
  padding: 0;
}

.assignee-item {
  margin-bottom: 10px;
  font-size: 1em;
}

.assignee-id {
  color: #888;
  margin-right: 8px;
}

.assignee-name {
  font-weight: bold;
  margin-right: 8px;
}

.assignee-email {
  font-style: italic;
  color: #b0b0b0;
}

/* Action Buttons */
.action-buttons {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 24px;
}

button {
  padding: 10px 18px;
  border: none;
  border-radius: 5px;
  font-size: 1em;
  cursor: pointer;
}

.edit {
  background-color: #4caf50;
  color: white;
}

.edit:hover {
  background-color: #43a047;
}

.delete {
  background-color: #f44336;
  color: white;
}

.delete:hover {
  background-color: #e53935;
}


/* Close Button */
.close-btn {
  position: absolute;
  top: 12px;
  right: 12px;
  font-size: 1.5em;
  color: #f44336;
  background: #333;
  border: none;
  cursor: pointer;
  z-index: 10;
}
.close-btn:hover {
  color: #e53935;
}
</style>
