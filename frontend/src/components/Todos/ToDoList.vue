<template>
  <h1>Todos</h1>
  <!-- Filter and Sorting -->
  <div class="filters">
    <div>
      <input class="titleFilter" id="titleFilter" v-model="titleFilter" type="text" placeholder="Filter by title"/>
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
      <!-- Table-like Header -->
      <div class="todo-list-header">
        <div class="todo-list-header-item">
          <strong>Title</strong>
        </div>
        <div class="todo-list-header-item-dueDate">
          <strong>Due Date</strong>
        </div>
      </div>
      <ul class="todo-list">
        <li v-for="todo in filteredAndSortedTodos" :key="todo.id" class="todo-item">
          <input type="checkbox" v-model="todo.finished" @change="toggleFinished(todo)" />
          <div class="title-info">
              {{ todo.title }}
          </div>
          <div class="due-date-info">
            {{new Date(todo.dueDate).toLocaleString()}}
          </div>
          <button @click="openDetails(todo)" class="btn-details">Details</button>
        </li>
      </ul>
    </div>

    <!-- Finished To-Do List -->
    <div class="todo-list-section">
      <h3>Open To-Dos</h3>
      <!-- Table-like Header -->
      <div class="todo-list-header">
        <div class="todo-list-header-item">
          <strong>Title</strong>
        </div>
        <div class="todo-list-header-item-dueDate">
          <strong>Due Date</strong>
        </div>
      </div>
      <ul class="todo-list">
        <li v-for="todo in filteredAndSortedFinishedTodos" :key="todo.id" class="todo-item">
          <input type="checkbox" v-model="todo.finished" @change="toggleFinished(todo)" />
          <div class="title-info">
            <span class="todo-info">{{ todo.title }}</span>
          </div>
          <div class="due-date-info">
            <span class="todo-info">{{new Date(todo.dueDate).toLocaleString()}}</span>
          </div>
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
      @edit="openEditModal"
      ref="detailsModal"/>

  <!-- ToDoEditModal -->
  <ToDoEditModal
      v-if="isEditModalOpen"
      :todo="selectedToDo"
      @update="updateToDo"
      @close="closeEditModal" />

  <!-- MessageModal -->
  <MessageModal
      v-if="isMessageModalOpen"
      :isOpen="isMessageModalOpen"
      :message="message"
      @close="closeMessageModal" />
</template>


<script>
import axios from 'axios';
import ToDoDetailsModal from './ToDoDetailsModal.vue';
import ToDoEditModal from './ToDoEditModal.vue';
import MessageModal from '@/components/Modals/messageModal.vue';
import { EventBus } from '@/components/event-bus.js'; // Import EventBus

export default {
  components: {
    ToDoDetailsModal,
    ToDoEditModal,
    MessageModal,
  },
  data() {
    return {
      todos: [],
      selectedToDo: null,
      isEditModalOpen: false,
      titleFilter: '',
      sortBy: 'title',
      sortOrder: 'asc',
      isMessageModalOpen: false, // For MessageModal
      message: '', // Message to display in the modal
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
            this.closeModal();
            this.openDetails(updatedTodo);
            EventBus.$emit('todoUpdated', response.data); // Emit event for updated todo
          })
          .catch(error => {
            let errorMessage = "An unexpected error occurred.";
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
                this.selectedToDo = {...this.selectedToDo, assigneeList: updatedTodo.assigneeList};
                this.showMessageModal(`ERROR: The assignee with ID ${duplicateId} is already assigned to another to-do.`);
                return;
              }
            }
            if (error.response) {
              errorMessage = `Error ${error.response.status}: ${error.response.data?.message || 'Assignee already assigned'}`;
              console.error(errorMessage);
            }
            this.showMessageModal(errorMessage); // Use MessageModal instead of alert
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
    toggleFinished(todo) {
      const requestBody = {
        title: todo.title,
        description: todo.description,
        finished: todo.finished,
        assigneeIdList: todo.assigneeList.map(assignee => assignee.id),
        dueDate: new Date(todo.dueDate).getTime()
      };

      axios.put(`/api/v1/todos/${todo.id}`, requestBody)
          .then(response => {
            const index = this.todos.findIndex(t => t.id === todo.id);
            if (index !== -1) {
              this.todos.splice(index, 1, response.data);
            }
          })
          .catch(error => {
            console.error('Error updating the todo:', error);
          });
    },
    showMessageModal(message) {
      this.message = message;
      this.isMessageModalOpen = true;
    },
    closeMessageModal() {
      this.isMessageModalOpen = false;
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
        this.todos.splice(index, 1, updatedTodo);
      }
    });
    EventBus.$on('todoDeleted', (todoId) => {
      this.todos = this.todos.filter(todo => todo.id !== todoId);
    });
    EventBus.$on('todoCreated', (newTodo) => {
      this.todos.push(newTodo); // Add the new to-do to the list
    });
    EventBus.$on('todo-updated', (updatedTodo) => {
      // You can handle the updatedTodo event here if needed
    });
    // Listen for the assigneeDeleted, assigneeUpdated, and newAssignee events
    EventBus.$on('assigneeDeleted', this.fetchTodos);
    EventBus.$on('assigneeUpdated', this.fetchTodos);
    EventBus.$on('newAssignee', this.fetchTodos);
  },

  beforeDestroy() {
    EventBus.$off('todoUpdated');
    EventBus.$off('todoDeleted');
    EventBus.$off('assigneeDeleted', this.fetchTodos);
    EventBus.$off('assigneeUpdated', this.fetchTodos);
    EventBus.$off('newAssignee', this.fetchTodos);
  },
};
</script>

<style scoped>
/* General Container */
.todos {
  display: flex;
  flex-direction: row; /* Layout in one row */
  gap: 10px; /* Reduces the space between the to-do sections */
}

/* To-Do List Header */
.todo-list-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  background-color: #444;
  padding: 5px 10px;
  border-radius: 5px;
}

.todo-list-header-item {
  width: 45%; /* Adjust width to fit content */
  text-align: center;
  font-size: 14px;
  margin-right: 50px;
  color: #e0e0e0;
}

.todo-list-header-item strong {
  font-weight: bold;
}

/* Filter and Sorting */
.filters {
  margin-top: -30px; /* Reduces the bottom margin */
  gap: 10px; /* Reduces the space between filter elements */
  display: flex;
  flex-wrap: wrap;
}

.filters label {
  font-size: 14px; /* Smaller font size */
  color: #ccc;
  margin-bottom: 4px; /* Reduces the bottom margin */
}

.filters input,
.filters select {
  width: 100%;
  padding: 8px; /* Reduces padding */
  font-size: 14px; /* Smaller font size */
  background-color: #333;
  border: 1px solid #444;
  border-radius: 4px;
  color: #e0e0e0;
  margin-bottom: 8px; /* Reduces margin between input fields */
}

/* To-Do Lists */
.todo-list-section {
  margin-bottom: 20px; /* Reduces bottom margin */
  width: 400px;
  height: 450px; /* Maximum height for the to-do list section */
}

.todo-list {
  list-style: none;
  padding: 0;
  margin-bottom: 10px;
  max-height: 380px;
  /* Reduces space between list items */
}
/* Container for due-date information */
.due-date-info {
  width: 80px; /* Fixed width for the container */
  word-wrap: break-word; /* Allow line breaks if the text is too long */
  overflow-wrap: break-word; /* Ensure long words are also wrapped correctly */
  display: flex;
  flex-direction: row; /* Stack title and due date vertically */
  gap: 2px; /* Add a small gap between Title and Due Date */
  font-size: 14px; /* Optional: Adjust font size */
}

.title-info {
  width: 120px; /* Fixed width for the container */
  word-break: break-word; /* Allow breaking long words onto the next line */
  display: flex;
  flex-direction: row; /* Keep the layout horizontally */
  gap: 2px;
  font-size: 14px; /* Optional: Adjust font size */
}


.titleFilter {
  margin-top: 18px;
}
.todo-item {
  background-color: #333;
  padding: 8px; /* Adjust padding */
  margin-bottom: 6px; /* Reduce space between items */
  border-radius: 4px;
  display: flex;
  flex-direction: row;
  align-items: start;
  transition: background-color 0.3s ease;
}

.todo-item label {
  display: flex;
  flex-direction: row; /* Stack title and due date vertically */
  gap: 10px; /* Add a small gap between Title and Due Date */
  font-size: 14px; /* Optional: Adjust font size */
}
.todo-list-header-item-dueDate {
  width: 45%; /* Adjust width to fit content */
  text-align: center;
  font-size: 14px;
  color: #e0e0e0;
  margin-right: 90px;
}

.todo-item label div {
  line-height: 1.4; /* Optional: Adjust line height */
}

/* Ensure checkbox and button are correctly spaced */
.todo-item input[type="checkbox"] {
  margin-right: 8px; /* Space between checkbox and content */
}

.todo-item:hover {
  background-color: #444;
}


.todo-item input[type="checkbox"] {
  margin-right: 8px; /* Reduces space between checkbox and title */
}

/* Button for Details */
.btn-details {
  background-color: #4CAF50;
  color: white;
  font-size: 12px; /* Smaller font size */
  padding: 6px 10px; /* Reduces padding */
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.btn-details:hover {
  background-color: #45a049;
}

.ToDoDetailsModal .modal-content, .ToDoEditModal .modal-content {
  background-color: #1e1e1e;
  color: #e0e0e0;
  padding: 16px; /* Reduces padding */
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

/* Input Fields in Edit Modal */
.ToDoEditModal input[type="text"], .ToDoEditModal input[type="date"] {
  width: 100%;
  padding: 8px;
  background-color: #333;
  border: 1px solid #444;
  border-radius: 4px;
  color: #e0e0e0;
}
</style>