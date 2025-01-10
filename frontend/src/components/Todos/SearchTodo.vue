<template>
  <div class="todos">
    <div class="search-form">
      <h1>Search Todo</h1>
      <input v-model="todoId" placeholder="Enter Todo ID" />
      <button @click="searchTodo">Search</button>
    </div>

    <div v-if="todo" class="todo-list-section">
      <h2>Todo Details</h2>
      <div class="todo-item">
        <span><strong>Title:</strong> {{ todo.title }}</span>
        <button class="btn-details" @click="openDetails(todo)">Details</button>
      </div>

      <!-- Todo Details Modal -->
      <TodoDetailsModal
          v-if="isDetailsModalOpen"
          :todo="selectedToDo"
          @delete="deleteToDo"
          @close="closeModal"
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
  </div>
</template>
<script lang="ts">
import { defineComponent, ref } from 'vue';
import axios from 'axios';
import TodoDetailsModal from './ToDoDetailsModal.vue';
import ToDoEditModal from './ToDoEditModal.vue';
import { EventBus } from '@/components/event-bus.js'; // Import EventBus

export default defineComponent({
  name: 'SearchTodo',
  components: {
    TodoDetailsModal,
    ToDoEditModal,
  },
  setup() {
    const todoId = ref('');
    const todo = ref(null);
    const selectedToDo = ref(null);
    const isDetailsModalOpen = ref(false);
    const isEditModalOpen = ref(false);
    const errorMessage = ref(null);

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

    const openDetails = (todo) => {
      selectedToDo.value = todo;
      isDetailsModalOpen.value = true;
    };

    const closeModal = () => {
      selectedToDo.value = null;
      isDetailsModalOpen.value = false;
    };

    const openEditModal = () => {
      isEditModalOpen.value = true;
    };

    const closeEditModal = () => {
      isEditModalOpen.value = false;
    };

    const updateToDo = async (updatedTodo) => {
      try {
        const response = await axios.put(`/api/v1/todos/${updatedTodo.id}`, updatedTodo);

        // Erfolgreiches Update
        if (response.data) {
          selectedToDo.value = response.data; // Aktualisiertes Todo setzen
          closeEditModal(); // Modal schließen
          EventBus.$emit('todoUpdated', response.data); // Event senden, wenn Todo erfolgreich aktualisiert wurde
        } else {
          alert('No data returned after updating Todo');
        }
      } catch (error) {
        console.error('Error during updateToDo:', error); // Loggen Sie den Fehler, um Details zu sehen
        let errorMessage = 'An unexpected error occurred.';
        if (error.response) {
          // Fehlerdetails vom Backend (z. B. 400, 500)
          errorMessage = `Error ${error.response.status}: ${error.response.data?.message || 'Error updating Todo'}`;
        } else if (error.request) {
          errorMessage = 'No response received from the server.';
        } else {
          errorMessage = `Error: ${error.message}`;
        }
        alert(errorMessage);
      }
    };



    const deleteToDo = async (todoId) => {
      try {
        await axios.delete(`/api/v1/todos/${todoId}`);
        if (todo.value && todo.value.id === todoId) {
          todo.value = null;
        }
        closeModal();
        EventBus.$emit('todoDeleted', todoId); // Emit event when todo is deleted
      } catch (error) {
        console.error('Error deleting Todo:', error);
      }
    };

    return {
      todoId,
      todo,
      selectedToDo,
      errorMessage,
      searchTodo,
      openDetails,
      closeModal,
      openEditModal,
      closeEditModal,
      updateToDo,
      deleteToDo,
      isDetailsModalOpen,
      isEditModalOpen,
    };
  },
});
</script>



<style scoped>
.todos {
  display: flex;
  flex-direction: row;
  gap: 10px;
}

.search-form {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-bottom: 20px;
}

input {
  background-color: #333;
  color: #e0e0e0;
  padding: 8px;
  font-size: 14px;
  border-radius: 4px;
  border: 1px solid #444;
  margin-bottom: 8px;
}

input:focus {
  border-color: #4CAF50;
  outline: none;
  background-color: #444;
}

button {
  background-color: #4CAF50;
  color: white;
  font-size: 16px;
  padding: 6px 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

button:hover {
  background-color: #45a049;
}

.todo-list-section {
  margin-bottom: 20px;
  width: 400px;
}

.todo-item {
  background-color: #333;
  padding: 8px;
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

.error-message {
  color: red;
  font-size: 12px;
  margin-top: 12px;
}
</style>
