<template>
  <div>
    <!-- Open To-Do List -->
    <div>
      <h3>Open To-Dos</h3>
      <ul>
        <li v-for="todo in filteredTodos" :key="todo.id">
          <label>
            <input type="checkbox" v-model="todo.finished" @change="toggleFinished(todo)" />
            {{ todo.title }}
          </label>
          <button @click="openDetails(todo)">Details</button>
        </li>
      </ul>
    </div>

    <!-- Finished To-Do List -->
    <div>
      <h3>Finished To-Dos</h3>
      <ul>
        <li v-for="todo in finishedTodos" :key="todo.id">
          <label>
            <input type="checkbox" v-model="todo.finished" @change="toggleFinished(todo)" />
            {{ todo.title }}
          </label>
          <button @click="openDetails(todo)">Details</button>
        </li>
      </ul>
    </div>

    <!-- ToDoDetailsModal -->
    <ToDoDetailsModal
        v-if="selectedToDo && !isEditModalOpen"
        :todo="selectedToDo"
        @delete="deleteToDo"
        @close="closeModal"
        @edit="openEditModal" />

    <!-- ToDoEditModal -->
    <ToDoEditModal
        v-if="isEditModalOpen"
        :todo="selectedToDo"
        @update="updateToDo"
        @close="closeEditModal" />
  </div>
</template>

<script>
import axios from 'axios';
import ToDoDetailsModal from './ToDoDetailsModal.vue';
import ToDoEditModal from './ToDoEditModal.vue';

export default {
  components: {
    ToDoDetailsModal,
    ToDoEditModal,
  },
  data() {
    return {
      todos: [],
      selectedToDo: null,
      isEditModalOpen: false,
    };
  },
  methods: {
    fetchTodos() {
      axios.get('/api/v1/todos')
          .then(response => {
            this.todos = response.data;
          })
          .catch(error => {
            console.error("Error fetching todos:", error);
          });
    },
    openDetails(todo) {
      this.selectedToDo = todo;
    },
    closeModal() {
      this.selectedToDo = null; // Close the modal
    },
    openEditModal(todo) {
      this.selectedToDo = {...todo}; // Create a copy to edit
      this.isEditModalOpen = true; // Show the edit modal
    },
    closeEditModal() {
      this.isEditModalOpen = false; // Close the edit modal
    },
    updateToDo(updatedToDo) {
      axios.put(`/api/v1/todos/${updatedToDo.id}`, updatedToDo)
          .then(() => {
            this.fetchTodos(); // Refresh the list after update
            this.isEditModalOpen = false; // Close the edit modal
          })
          .catch(error => {
            console.error("Error updating todo:", error);
          });
    },
    deleteToDo(id) {
      axios.delete(`/api/v1/todos/${id}`)
          .then(() => {
            this.fetchTodos(); // Refresh the list after deletion
            this.selectedToDo = null; // Close the modal by setting selectedToDo to null
          })
          .catch(error => {
            console.error("Error deleting todo:", error);
          });
    },
    toggleFinished(todo) {
      const updatedToDo = {...todo, finished: todo.finished};
      this.updateToDo(updatedToDo);
    }
  },
  computed: {
    filteredTodos() {
      return this.todos.filter(todo => !todo.finished); // Only open todos
    },
    finishedTodos() {
      return this.todos.filter(todo => todo.finished); // Only finished todos
    }
  },
  created() {
    this.fetchTodos(); // Fetch todos when the app is created
  },
};
</script>

