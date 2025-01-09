<template>

    <h1>Search Todo</h1>
    <div class="search-form">
      <input v-model="todoId" placeholder="Enter Todo ID" />
      <button @click="searchTodo">Search</button>
    </div>
    <div v-if="todo">
      <h2>Todo Details</h2>
      <p><strong>ID:</strong> {{ todo.id }}</p>
      <p><strong>Title:</strong> {{ todo.title }}</p>
      <p><strong>Description:</strong> {{ todo.description }}</p>
      <p><strong>Category:</strong> {{ todo.category }}</p>
      <p><strong>Finished:</strong> {{ todo.finished }}</p>
      <p><strong>Created Date:</strong> {{ formatDate(todo.createdDate) }}</p>
      <p><strong>Due Date:</strong> {{ formatDate(todo.dueDate) }}</p>
      <p><strong>Finished Date:</strong> {{ todo.finishedDate ? formatDate(todo.finishedDate) : 'Not finished yet' }}</p>
      <h3>Assignee List</h3>
      <ul>
        <li v-for="assignee in todo.assigneeList" :key="assignee.id">
          <p><strong>ID:</strong> {{ assignee.id }}</p>
          <p><strong>First Name:</strong> {{ assignee.prename }}</p>
          <p><strong>Last Name:</strong> {{ assignee.name }}</p>
          <p><strong>Email:</strong> {{ assignee.email }}</p>
        </li>
      </ul>
    </div>
    <div v-if="errorMessage" class="error-message">
      <p>{{ errorMessage }}</p>
    </div>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue';
import axios from 'axios';

export default defineComponent({
  name: 'SearchTodo',
  setup() {
    const todoId = ref('');
    const todo = ref(null);
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

    const formatDate = (timestamp: number) => {
      const date = new Date(timestamp);
      return date.toLocaleString();
    };

    return {
      todoId,
      todo,
      errorMessage,
      searchTodo,
      formatDate,
    };
  },
});
</script>

<style scoped>
/* Gesamter Container */
.container {
  background-color: #1e1e1e;
  color: #e0e0e0;
  padding: 20px;
  display: grid;
  border-radius: 8px;
  max-width: 600px;
  margin: 20px auto;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
}

/* Titel */
h1 {
  font-size: 24px;
  margin-bottom: 20px;
}

/* Formular zur Suche */
.search-form {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-bottom: 20px;
}

input {
  background-color: #333;
  color: #e0e0e0;
  padding: 12px;
  font-size: 16px;
  border-radius: 4px;
  border: 1px solid #444;
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
  padding: 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

button:hover {
  background-color: #45a049;
}

.error-message {
  color: red;
  font-size: 14px;
  margin-top: 15px;
}

h2, h3 {
  font-size: 20px;
  margin-top: 20px;
  margin-bottom: 10px;
}

ul {
  list-style: none;
  padding: 0;
}

ul li {
  padding: 10px;
  border-bottom: 1px solid #444;
}
</style>
