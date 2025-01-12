<template>
  <div class="todos">
    <div class="search-form">
      <h1>Search Todo</h1>
      <div class="search-fields">
        <input v-model="todoId" placeholder="Enter Todo ID" />
        <button @click="searchTodo">Search</button>
      </div>
    </div>

    <div v-if="todo" class="todo-list-section">
      <div class="todo-item">
        <input type="checkbox" class="checkbox" v-model="todo.finished" @change="toggleFinished(todo)" />
        <span><strong>ID:</strong> {{ todo.id }}</span>
        <span><strong>Title:</strong> {{ todo.title }}</span>
        <span><strong>Due Date:</strong> {{ new Date(todo.dueDate).toLocaleString() }}</span>
        <button class="btn-details" @click="openDetails(todo)">Details</button>
      </div>

      <!-- Todo Details Modal -->
      <TodoDetailsModal
          v-if="isDetailsModalOpen"
          :todo="selectedToDo"
          @delete="deleteToDo"
          @close="closeDetailsModal"
          @edit="openEditModal" />

      <!-- Todo Edit Modal -->
      <ToDoEditModal
          v-if="isEditModalOpen"
          :todo="selectedToDo"
          @update="updateToDo"
          @close="closeEditModal" />
    </div>

    <div v-if="errorMessage" class="error-message">
      <p>{{ errorMessage }}</p>
    </div>

    <!-- Message Modal -->
    <MessageModal
        :isOpen="isMessageModalOpen"
        :message="messageContent"
        @close="closeMessageModal" />
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue';
import axios from 'axios';
import TodoDetailsModal from './ToDoDetailsModal.vue';
import ToDoEditModal from './ToDoEditModal.vue';
import MessageModal from '@/components/Modals/messageModal.vue'; // Import MessageModal
import { EventBus } from '@/components/event-bus.js'; // Import EventBus

export default defineComponent({
  name: 'SearchTodo',
  components: {
    TodoDetailsModal,
    ToDoEditModal,
    MessageModal, // Register MessageModal
  },
  setup() {
    const todoId = ref('');
    const todo = ref(null);
    const selectedToDo = ref(null);
    const isDetailsModalOpen = ref(false);
    const isEditModalOpen = ref(false);
    const errorMessage = ref(null);
    const isMessageModalOpen = ref(false);
    const messageContent = ref('');

    // Search for Todo by ID
    const searchTodo = async () => {
      errorMessage.value = null;
      todo.value = null;

      if (!todoId.value.trim()) {
        errorMessage.value = 'Please enter a Todo ID';
        return;
      }

      try {
        const response = await axios.get(`/api/v1/todos/${todoId.value.trim()}`);
        todo.value = response.data;
      } catch (error) {
        errorMessage.value = 'No Todo found with the given ID';
      }
    };

    // Open Todo details in a modal
    const openDetails = (todo) => {
      selectedToDo.value = todo;
      isDetailsModalOpen.value = true;
    };

    // Close Todo details modal
    const closeDetailsModal = () => {
      selectedToDo.value = null;
      isDetailsModalOpen.value = false;
    };

    // Open Todo edit modal
    const openEditModal = () => {
      isEditModalOpen.value = true;
    };

    // Close Todo edit modal
    const closeEditModal = () => {
      isEditModalOpen.value = false;
    };

    // Close message modal
    const closeMessageModal = () => {
      isMessageModalOpen.value = false;
      messageContent.value = '';
    };

    // Show message in message modal
    const showMessageModal = (message) => {
      messageContent.value = message;
      isMessageModalOpen.value = true;
    };

    // Update Todo
    const updateToDo = async (updatedTodo) => {
      try {
        const response = await axios.put(`/api/v1/todos/${updatedTodo.id}`, updatedTodo);

        if (response.data) {
          selectedToDo.value = response.data;
          closeEditModal();
          EventBus.$emit('todoUpdated', response.data);
          showMessageModal('Todo successfully updated!');
        } else {
          showMessageModal('No data returned after updating Todo.');
        }
      } catch (error) {
        if (
            error.response &&
            error.response.status === 500 &&
            error.response.data &&
            error.response.data.message.includes("Duplicate entry")
        ) {
          const duplicateIdMatch = error.response.data.message.match(/Duplicate entry '(\d+)'/);
          if (duplicateIdMatch && duplicateIdMatch[1]) {
            const duplicateId = duplicateIdMatch[1];

            // Find and remove the assignee causing the duplication
            const assigneeIndex = updatedTodo.assigneeList.findIndex(assignee => assignee.id == duplicateId);
            if (assigneeIndex !== -1) {
              updatedTodo.assigneeList.splice(assigneeIndex, 1); // Remove the problematic assignee
            }

            // Update the selected todo to reflect changes
            todo.value = { ...todo.value, assigneeList: updatedTodo.assigneeList };

            // Inform the user about the error
            showMessageModal("ERROR: The assignee with ID " + duplicateId + " is already assigned to another to-do.");
            return;
          }
        }

        let errorMessage = 'An unexpected error occurred.';
        if (error.response) {
          errorMessage = `Error ${error.response.status}: ${error.response.data?.message || 'Error updating Todo'}`;
        } else if (error.request) {
          errorMessage = 'No response received from the server.';
        } else {
          errorMessage = `Error: ${error.message}`;
        }

        showMessageModal(errorMessage);
      }
    };

    // Toggle Todo status (finished or not)
    const toggleFinished = async (todo) => {
      try {
        const response = await axios.put(`/api/v1/todos/${todo.id}`, {
          ...todo,
          finished: todo.finished,
        });

        if (response.data) {
          todo.value = response.data;
          EventBus.$emit('todoUpdated', response.data);
        }
        await searchTodo();
      } catch (error) {
        showMessageModal('Error updating Todo status.');
      }
    };

    // Delete Todo
    const deleteToDo = async (todoId) => {
      try {
        await axios.delete(`/api/v1/todos/${todoId}`);
        if (todo.value && todo.value.id === todoId) {
          todo.value = null;
        }
        closeDetailsModal();
        EventBus.$emit('todoDeleted', todoId);

      } catch (error) {
        console.log('Error deleting Todo.');
      }
    };

    return {
      todoId,
      todo,
      selectedToDo,
      errorMessage,
      searchTodo,
      openDetails,
      closeDetailsModal,
      openEditModal,
      closeEditModal,
      updateToDo,
      deleteToDo,
      toggleFinished,
      isDetailsModalOpen,
      isEditModalOpen,
      isMessageModalOpen,
      messageContent,
      closeMessageModal,
    };
  },
});
</script>

<style scoped>
/* Main container for todos */
.todos {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 20px;
  height: 175px; /* Fixed height */
  overflow-y: auto; /* Scrollable content if it overflows */
}

/* Search form styling */
.search-form {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-bottom: 20px;
}

/* Search fields layout */
.search-fields {
  display: flex;
  gap: 10px;
  margin-top: -30px;
  margin-bottom: -10px; /* Space between input fields and button */
}

/* Styling for search input */
.search-form input {
  background-color: #333;
  color: #e0e0e0;
  padding: 8px;
  font-size: 14px;
  border-radius: 4px;
  border: 1px solid #444;
  margin-bottom: 8px;
  margin-left: 5px;
  width: 200px; /* Fixed width for input */
  height: 36px; /* Set the height for the input */
}

/* Styling for search button */
.search-form button {
  background-color: #4CAF50;
  color: white;
  font-size: 16px;
  padding: 8px 18px; /* Consistent padding with input */
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  height: 36px; /* Button height equal to input */
  width: 220px; /* Slightly wider button */
}
.search-form button:hover {
  background-color: #45a049;
}

/* Styling for the todo list section */
.todo-list-section {
  width: 100%;
  max-width: 500px;
}

/* Individual todo item styling */
.todo-item {
  background-color: #333;
  padding: 10px;
  margin-bottom: 6px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  transition: background-color 0.3s ease;
}
.todo-item:hover {
  background-color: #444;
}

/* Styling for the details button */
.btn-details {
  background-color: #4CAF50;
  color: white;
  font-size: 12px;
  padding: 6px 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.btn-details:hover {
  background-color: #45a049;
}

/* Error message styling */
.error-message {
  color: red;
  font-size: 12px;
  margin-top: 12px;
}

/* Checkbox styling */
.checkbox {
  margin-right: 5px;
}
</style>
