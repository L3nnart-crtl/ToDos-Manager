<template>
    <h1>Todos</h1>
    <!-- Filter und Sortierung -->

    <div class="filters">
      <div>
        <label for="titleFilter">Filter by Title:</label>
        <input id="titleFilter" v-model="titleFilter" type="text" placeholder="Search by title" />
      </div>
      <div>
        <label for="sortBy">Sort by:</label>
        <select v-model="sortBy">
          <option value="title">Title</option>
          <option value="dueDate">Due Date</option>
        </select>
      </div>
      <div>
        <label for="sortOrder">Sort Order:</label>
        <select v-model="sortOrder">
          <option value="asc">Ascending</option>
          <option value="desc">Descending</option>
        </select>
      </div>
    </div>
  <div class="todos">
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
</template>

<script>
import axios from 'axios';
import ToDoDetailsModal from './ToDoDetailsModal.vue';
import ToDoEditModal from './ToDoEditModal.vue';
import { EventBus } from '@/components/event-bus.js'; // Import EventBus

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
      titleFilter: '',
      sortBy: 'title',
      sortOrder: 'asc',
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
            EventBus.$emit('todoUpdated', response.data); // Emit event for updated todo
          })
          .catch(error => {
            let errorMessage = "An unexpected error occurred.";
            if (error.response) {
              errorMessage = `Error ${error.response.status}: ${error.response.data?.message || 'Assignee already assigned'}`;
              console.error(errorMessage);
            }
            alert(errorMessage);
          });
    },
    deleteToDo(todoId) {
      axios.delete(`/api/v1/todos/${todoId}`)
          .then(() => {
            this.todos = this.todos.filter(todo => todo.id !== todoId);
            this.closeModal();
            EventBus.$emit('todoDeleted', todoId); // Emit event for deleted todo
          });
    },
  },
  computed: {
    filteredAndSortedTodos() {
      const filteredTodos = this.todos.filter(todo => todo.title.toLowerCase().includes(this.titleFilter.toLowerCase()) && !todo.finished);
      filteredTodos.sort((a, b) => {
        let aValue, bValue;
        if (this.sortBy === 'title') {
          aValue = a.title;
          bValue = b.title;
        } else {
          aValue = a.dueDate;
          bValue = b.dueDate;
        }
        return this.sortOrder === 'asc' ? aValue > bValue : aValue < bValue;
      });
      return filteredTodos;
    },
    filteredAndSortedFinishedTodos() {
      const filteredTodos = this.todos.filter(todo => todo.title.toLowerCase().includes(this.titleFilter.toLowerCase()) && todo.finished);
      filteredTodos.sort((a, b) => {
        let aValue, bValue;
        if (this.sortBy === 'title') {
          aValue = a.title;
          bValue = b.title;
        } else {
          aValue = a.dueDate;
          bValue = b.dueDate;
        }
        return this.sortOrder === 'asc' ? aValue > bValue : aValue < bValue;
      });
      return filteredTodos;
    }
  },
  created() {
    this.fetchTodos();
    EventBus.$on('todoUpdated', (updatedTodo) => {
      const index = this.todos.findIndex(todo => todo.id === updatedTodo.id);
      if (index !== -1) {
        this.todos.splice(index, 1, updatedTodo); // Update todo in the list
      }
    });
    EventBus.$on('todoDeleted', (todoId) => {
      this.todos = this.todos.filter(todo => todo.id !== todoId); // Remove deleted todo
    });
  },
  beforeDestroy() {
    EventBus.$off('todoUpdated');
    EventBus.$off('todoDeleted');
  },
};
</script>



<style scoped>
/* Gesamter Container */
.todos {
  display: flex;
  flex-direction: row; /* Layout in einer Spalte */
  gap: 10px; /* Reduziert den Abstand zwischen den To-Do-Abschnitten */
}

/* Filter und Sortierung */
.filters {
  margin-bottom: 10px; /* Verringert den Abstand nach unten */
  gap: 10px; /* Verringert den Abstand zwischen den Filter-Elementen */
  display: flex;
  flex-wrap: wrap;
}

.filters label {
  font-size: 14px; /* Kleinere Schriftgröße */
  color: #ccc;
  margin-bottom: 4px; /* Verringert den Abstand nach unten */
}

.filters input,
.filters select {
  width: 100%;
  padding: 8px; /* Verringert das Padding */
  font-size: 14px; /* Kleinere Schriftgröße */
  background-color: #333;
  border: 1px solid #444;
  border-radius: 4px;
  color: #e0e0e0;
  margin-bottom: 8px; /* Verringert den Abstand zwischen den Eingabefeldern */
}

/* To-Do Listen */
.todo-list-section {
  margin-bottom: 20px; /* Verringert den Abstand nach unten */
  width: 400px;
}

.todo-list {
  list-style: none;
  padding: 0;
  margin-bottom: 10px; /* Verringert den Abstand zwischen den Listen */
}

.todo-item {
  background-color: #333;
  padding: 8px; /* Verringert das Padding */
  margin-bottom: 6px; /* Verringert den Abstand zwischen den ToDo-Items */
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  transition: background-color 0.3s ease;
}

.todo-item:hover {
  background-color: #444;
}

.todo-item input[type="checkbox"] {
  margin-right: 8px; /* Verringert den Abstand zwischen Checkbox und Titel */
}

/* Button für Details */
.btn-details {
  background-color: #4CAF50;
  color: white;
  font-size: 12px; /* Kleinere Schriftgröße */
  padding: 6px 10px; /* Verringert das Padding */
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.btn-details:hover {
  background-color: #45a049;
}

/* Modale Fenster */
.ToDoDetailsModal, .ToDoEditModal {
  max-width: 500px; /* Verringert die maximale Breite des Modals */
  margin: 20px auto;
}

.ToDoDetailsModal .modal-content, .ToDoEditModal .modal-content {
  background-color: #1e1e1e;
  color: #e0e0e0;
  padding: 16px; /* Verringert das Padding */
  border-radius: 8px;
}

.ToDoDetailsModal button, .ToDoEditModal button {
  background-color: #4CAF50;
  color: white;
  padding: 8px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.ToDoDetailsModal button:hover, .ToDoEditModal button:hover {
  background-color: #45a049;
}

/* Eingabefelder im Edit Modal */
.ToDoEditModal input[type="text"], .ToDoEditModal input[type="date"] {
  background-color: #333;
  color: #e0e0e0;
  padding: 8px; /* Verringert das Padding */
  border-radius: 4px;
  border: 1px solid #444;
  width: 100%;
  margin-bottom: 12px; /* Verringert den Abstand zwischen den Eingabefeldern */
}

.ToDoEditModal input[type="text"]:focus, .ToDoEditModal input[type="date"]:focus {
  border-color: #4CAF50;
  outline: none;
  background-color: #444;
}

/* Fehlernachricht im Modal */
.error-message {
  color: red;
  font-size: 12px; /* Kleinere Schriftgröße */
  margin-top: 12px; /* Verringert den Abstand */
}

</style>

