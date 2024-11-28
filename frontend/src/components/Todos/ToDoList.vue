<template>
  <div class="todo-container">
    <!-- Filter und Sortierung -->
    <div class="filters">
      <label for="titleFilter">Filter by Title:</label>
      <input id="titleFilter" v-model="titleFilter" type="text" placeholder="Search by title" />

      <label for="sortBy">Sort by:</label>
      <select v-model="sortBy">
        <option value="title">Title</option>
        <option value="dueDate">Due Date</option>
      </select>

      <label for="sortOrder">Sort Order:</label>
      <select v-model="sortOrder">
        <option value="asc">Ascending</option>
        <option value="desc">Descending</option>
      </select>
    </div>

    <!-- Open To-Do List -->
    <div class="todo-list-section">
      <h3>Open To-Dos</h3>
      <ul class="todo-list">
        <li v-for="todo in filteredAndSortedTodos" :key="todo.id" class="todo-item">
          <label>
            <input type="checkbox" v-model="todo.finished" @change="toggleFinished(todo)" />
            {{ todo.title }}
          </label>
          <button @click="openDetails(todo)" class="btn-details">Details</button>
        </li>
      </ul>
    </div>

    <!-- Finished To-Do List -->
    <div class="todo-list-section">
      <h3>Finished To-Dos</h3>
      <ul class="todo-list">
        <li v-for="todo in filteredAndSortedFinishedTodos" :key="todo.id" class="todo-item">
          <label>
            <input type="checkbox" v-model="todo.finished" @change="toggleFinished(todo)" />
            {{ todo.title }}
          </label>
          <button @click="openDetails(todo)" class="btn-details">Details</button>
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
import { EventBus } from '@/components/event-bus.js';

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
      titleFilter: '',  // Filter für den Titel
      sortBy: 'title',  // Sortierkriterium
      sortOrder: 'asc',  // Sortierreihenfolge
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
      this.selectedToDo = null;
    },
    openEditModal() {
      this.isEditModalOpen = true;
    },
    closeEditModal() {
      this.isEditModalOpen = false;
    },
    updateToDo(updatedTodo) {
      axios.put(`/api/v1/todos/${updatedTodo.id}`, updatedTodo)
          .then(response => {
            const index = this.todos.findIndex(todo => todo.id === updatedTodo.id);
            if (index !== -1) {
              this.todos.splice(index, 1, response.data);
            }
            this.closeEditModal();
          });
    },
    toggleFinished(todo) {
      axios.put(`/api/v1/todos/${todo.id}`, todo)
          .then(response => {
            const index = this.todos.findIndex(t => t.id === todo.id);
            if (index !== -1) {
              this.todos.splice(index, 1, response.data);
            }
          });
    },
    deleteToDo(todoId) {
      axios.delete(`/api/v1/todos/${todoId}`)
          .then(() => {
            this.todos = this.todos.filter(todo => todo.id !== todoId);
            this.closeModal();
          });
    },
  },
  computed: {
    filteredAndSortedTodos() {
// Filter und Sortierung zusammen in einer berechneten Eigenschaft
      const filteredTodos = this.todos.filter(todo => {
        return todo.title.toLowerCase().includes(this.titleFilter.toLowerCase()) && !todo.finished;
      });

// Sortierung der gefilterten To-Dos
      filteredTodos.sort((a, b) => {
        let aValue, bValue;
        if (this.sortBy === 'title') {
          aValue = a.title;
          bValue = b.title;
        } else {
          aValue = a.dueDate;
          bValue = b.dueDate;
        }

// Vergleiche aValue und bValue basierend auf der Sortierreihenfolge
        if (this.sortOrder === 'asc') {
          if (aValue > bValue) {
            return 1;
          } else if (aValue < bValue) {
            return -1;
          } else {
            return 0;
          }
        } else { // Wenn 'desc'
          if (aValue < bValue) {
            return 1;
          } else if (aValue > bValue) {
            return -1;
          } else {
            return 0;
          }
        }
      });

      return filteredTodos;
    },

    filteredAndSortedFinishedTodos() {
// Filtert abgeschlossene To-Dos
      const filteredTodos = this.todos.filter(todo => {
        return todo.title.toLowerCase().includes(this.titleFilter.toLowerCase()) && todo.finished;
      });

// Sortierung der gefilterten To-Dos
      filteredTodos.sort((a, b) => {
        let aValue, bValue;
        if (this.sortBy === 'title') {
          aValue = a.title;
          bValue = b.title;
        } else {
          aValue = a.dueDate;
          bValue = b.dueDate;
        }

// Vergleiche aValue und bValue basierend auf der Sortierreihenfolge
        if (this.sortOrder === 'asc') {
          if (aValue > bValue) {
            return 1;
          } else if (aValue < bValue) {
            return -1;
          } else {
            return 0;
          }
        } else { // Wenn 'desc'
          if (aValue < bValue) {
            return 1;
          } else if (aValue > bValue) {
            return -1;
          } else {
            return 0;
          }
        }
      });

      return filteredTodos;
    }
  },
  created() {
    this.fetchTodos();
    EventBus.$on('todoCreated', (newTodo) => {
      this.todos.push(newTodo);
    });
  },
  beforeDestroy() {
    EventBus.$off('todoCreated');
  },
};
</script>

<style scoped>
/* Gesamter Container */
.todo-container {
  background-color: #1e1e1e; /* Dunkler Hintergrund für den Container */
  color: #e0e0e0; /* Helle Textfarbe */
  padding: 20px;
  border-radius: 8px;
  max-width: 800px;
  margin: 20px auto;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
}

/* Filter und Sortierung */
.filters {
  margin-bottom: 20px;
}

.filters label {
  color: #ccc; /* Helle Farbe für die Labels */
  font-size: 16px;
}

.filters input, .filters select {
  background-color: #333; /* Dunkler Hintergrund für Eingabefelder */
  color: #e0e0e0; /* Helle Textfarbe */
  padding: 10px;
  border: 1px solid #444;
  border-radius: 4px;
  width: 100%;
  margin-bottom: 10px;
}

.filters input:focus, .filters select:focus {
  border-color: #4CAF50; /* Grüne Umrandung bei Fokus */
  outline: none;
  background-color: #444;
}

/* Überschrift der ToDo-Listen */
h3 {
  font-size: 20px;
  color: #e0e0e0;
  margin-bottom: 10px;
}

/* To-Do Listen */
.todo-list-section {
  margin-bottom: 30px;
}

.todo-list {
  list-style: none;
  padding: 0;
}

.todo-item {
  background-color: #333; /* Dunkler Hintergrund für jedes ToDo-Item */
  padding: 12px;
  margin-bottom: 10px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  transition: background-color 0.3s ease;
}

.todo-item:hover {
  background-color: #444; /* Etwas hellerer Hintergrund bei Hover */
}

.todo-item input[type="checkbox"] {
  margin-right: 10px;
}

/* Button für Details */
.btn-details {
  background-color: #4CAF50; /* Grüner Button */
  color: white;
  font-size: 14px;
  padding: 8px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.btn-details:hover {
  background-color: #45a049; /* Etwas dunkleres Grün bei Hover */
}

/* Modale Fenster */
.ToDoDetailsModal, .ToDoEditModal {
  max-width: 600px;
  margin: 20px auto;
}

.ToDoDetailsModal .modal-content, .ToDoEditModal .modal-content {
  background-color: #1e1e1e; /* Dunkler Hintergrund für das Modal */
  color: #e0e0e0;
  padding: 20px;
  border-radius: 8px;
}

.ToDoDetailsModal button, .ToDoEditModal button {
  background-color: #4CAF50; /* Grüner Button im Modal */
  color: white;
  padding: 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.ToDoDetailsModal button:hover, .ToDoEditModal button:hover {
  background-color: #45a049; /* Etwas dunkleres Grün bei Hover */
}

/* Eingabefelder im Edit Modal */
.ToDoEditModal input[type="text"], .ToDoEditModal input[type="date"] {
  background-color: #333;
  color: #e0e0e0;
  padding: 10px;
  border-radius: 4px;
  border: 1px solid #444;
  width: 100%;
  margin-bottom: 15px;
}

.ToDoEditModal input[type="text"]:focus, .ToDoEditModal input[type="date"]:focus {
  border-color: #4CAF50;
  outline: none;
  background-color: #444;
}

/* Anpassung der Modalen Fenster für Buttons */
.ToDoDetailsModal button:first-child, .ToDoEditModal button:first-child {
  background-color: #e74c3c; /* Roter Button für Löschen */
}

.ToDoDetailsModal button:first-child:hover, .ToDoEditModal button:first-child:hover {
  background-color: #c0392b; /* Etwas dunkleres Rot bei Hover */
}

/* Fehlernachricht im Modal */
.error-message {
  color: red;
  font-size: 14px;
  margin-top: 15px;
}
</style>

