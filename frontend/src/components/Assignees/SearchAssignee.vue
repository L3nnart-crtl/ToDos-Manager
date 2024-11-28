<template>
  <div class="container">
    <h1>Search Assignee by ID</h1>
    <div class="search-form">
      <input type="number" v-model="searchId" placeholder="Enter Assignee ID" />
      <button @click="searchAssignee">Search</button>
    </div>
    <div v-if="assignee">
      <h2>Assignee Details</h2>
      <p><strong>ID:</strong> {{ assignee.id }}</p>
      <p><strong>First Name:</strong> {{ assignee.prename }}</p>
      <p><strong>Last Name:</strong> {{ assignee.name }}</p>
      <p><strong>Email:</strong> {{ assignee.email }}</p>
    </div>
    <div v-if="errorMessage" class="error-message">
      <p>{{ errorMessage }}</p>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue';
import axios from 'axios';

export default defineComponent({
  name: 'SearchAssignee',
  setup() {
    const searchId = ref<number | null>(null);
    const assignee = ref<any | null>(null);
    const errorMessage = ref<string | null>(null);

    const searchAssignee = async () => {
      if (searchId.value !== null) {
        try {
          const response = await axios.get(`/api/v1/assignees/${searchId.value}`);
          assignee.value = response.data;
          errorMessage.value = null;
        } catch (error) {
          assignee.value = null;
          errorMessage.value = 'Assignee not found';
        }
      }
    };

    return {
      searchId,
      assignee,
      errorMessage,
      searchAssignee,
    };
  },
});
</script>
