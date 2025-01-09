<template>
  <div class="container">

      <div class="component-search-createTodo">
        <SearchAssignee />
        <SearchTodo />
      </div>

      <div class="todoList-component">
        <ToDoList :todos="todos" />
        <TodoCsvDownloader />
      </div>

      <div class="assigneeList-component">
        <CreateAssignee />
        <AssigneeList />
      </div>
      <div class="component">
        <ToDoCreateModal @created="handleNewToDo" />
      </div>
    </div>


</template>

<script lang="ts" setup>
import { ref } from 'vue';
import AssigneeList from './Assignees/AssigneesList.vue';
import CreateAssignee from './Assignees/createAssignee.vue';
import SearchAssignee from './Assignees/SearchAssignee.vue';
import ToDoList from "@/components/Todos/ToDoList.vue";
import ToDoCreateModal from "@/components/Todos/ToDoCreateModal.vue";
import SearchTodo from "@/components/Todos/SearchTodo.vue";
import TodoCsvDownloader from "@/components/Todos/TodoCsvDownloader.vue";

const todos = ref([]); // To-Do Liste

// Methode zum Hinzufügen eines neuen To-Dos
const handleNewToDo = (newTodo) => {
  todos.value.push(newTodo); // Füge das neue To-Do direkt der Liste hinzu
};
</script>
