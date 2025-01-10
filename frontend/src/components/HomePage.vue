<template>
  <h1 class="title">Todos Manager</h1>
  <div class="container">

      <div class="todoList-component">
        <ToDoList :todos="todos" />
        <TodoCsvDownloader />
        <SearchTodo />
      </div>
      <div class="component">
        <ToDoCreateModal @created="handleNewToDo" />
        <CreateAssignee />
      </div>
      <div>
        <div class="component-search">
          <SearchAssignee />
        </div>
        <div class="assigneeList-component">
          <AssigneeList />
        </div>
      </div>


  </div>


</template>

<script lang="ts" setup>
import { ref } from 'vue';
import AssigneeList from './Assignees/AssigneesList.vue';
import CreateAssignee from './Assignees/createAssignee.vue';
import SearchAssignee from './Assignees/SearchAssignee.vue';
import ToDoList from "@/components/Todos/ToDoList.vue";
import ToDoCreateModal from "@/components/Todos/ToDoCreate.vue";
import SearchTodo from "@/components/Todos/SearchTodo.vue";
import TodoCsvDownloader from "@/components/Todos/TodoCsvDownloader.vue";

const todos = ref([]); // To-Do Liste

// Methode zum Hinzufügen eines neuen To-Dos
const handleNewToDo = (newTodo) => {
  todos.value.push(newTodo); // Füge das neue To-Do direkt der Liste hinzu
};
</script>

<style>
/* Style for the title */
.title {
  text-align: center; /* Center the text horizontally */
  font-size: 32px; /* Adjust the font size as needed */
  font-weight: bold; /* Make the text bold */
  margin-top: 10px; /* Add space from the top */
  margin-bottom: 5px; /* Add space below the title */
}
</style>