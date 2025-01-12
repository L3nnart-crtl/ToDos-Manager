<template>
  <h1 class="title">Todos Manager</h1>
  <div class="container">

    <div class="todoList-component">
      <!-- Search Todo component -->
      <SearchTodo />
      <!-- ToDo List component displaying todos -->
      <ToDoList :todos="todos" />
      <!-- CSV Downloader component to export todos -->
      <TodoCsvDownloader />
    </div>

    <div class="component">
      <!-- Modal for creating new To-Do -->
      <ToDoCreateModal @created="handleNewToDo" />
      <!-- Component for creating a new assignee -->
      <CreateAssignee />
    </div>

    <div class="assigneeList-component">
      <!-- List of Assignees -->
      <AssigneeList />
    </div>

  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue';

// Importing components
import AssigneeList from './Assignees/AssigneesList.vue';
import CreateAssignee from './Assignees/createAssignee.vue';
import ToDoList from "@/components/Todos/ToDoList.vue";
import ToDoCreateModal from "@/components/Todos/ToDoCreate.vue";
import SearchTodo from "@/components/Todos/SearchTodo.vue";
import TodoCsvDownloader from "@/components/Todos/TodoCsvDownloader.vue";

// Reactive reference for the todos list
const todos = ref([]); // To-Do list

// Method to handle the addition of a new To-Do
const handleNewToDo = (newTodo) => {
  todos.value.push(newTodo); // Adds the new todo directly to the list
};
</script>

<style>
/* Style for the title */
.title {
  text-align: center; /* Centers the title text */
  font-size: 32px; /* Font size */
  font-weight: bold; /* Makes the title bold */
  margin-top: 10px; /* Adds top margin */
  margin-bottom: 5px; /* Adds bottom margin */
}
</style>
