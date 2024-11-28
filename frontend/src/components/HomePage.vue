<template>
  <div class="container">
    <!-- Assignee-Container für AssigneeList und CreateAssignee nebeneinander -->
    <div class="assignee-container">
      <div class="component">
        <AssigneeList />
      </div>
      <div class="component">
        <CreateAssignee />
      </div>
    </div>

    <!-- SearchAssignee oben oder unten angezeigt -->
    <div class="component">
      <SearchAssignee />
    </div>

    <!-- ToDoList und ToDoCreateModal untereinander -->
    <div class="todo-container">
      <div class="component">
        <ToDoList />
      </div>
      <div class="component">
        <ToDoCreateModal />
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import AssigneeList from './Assignees/AssigneesList.vue';
import CreateAssignee from './Assignees/createAssignee.vue';
import SearchAssignee from './Assignees/SearchAssignee.vue';
import ToDoList from "@/components/Todos/ToDoList.vue";
import ToDoCreateModal from "@/components/Todos/ToDoCreateModal.vue";
</script>
