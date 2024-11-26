<template>
  <div class="container">
    <h1>Create Assignee</h1>
    <form @submit.prevent="onSubmit" class="form">
      <div class="form-group">
        <label for="prename">Prename</label>
        <input type="text" id="prename" v-model="assignee.prename" />
      </div>
      <div class="form-group">
        <label for="name">Name</label>
        <input type="text" id="name" v-model="assignee.name" />
      </div>
      <div class="form-group">
        <label for="email">Email</label>
        <input type="text" id="email" v-model="assignee.email" />
      </div>
      <button type="submit">Create</button>
    </form>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import { EventBus } from './event-bus';
import axios from 'axios';

export default defineComponent({
  name: 'CreateAssignee',
  data() {
    return {
      assignee: {
        prename: '',
        name: '',
        email: '',
      },
    };
  },
  methods: {
    async onSubmit() {
      try {
        const response = await axios.post('/api/v1/assignees', this.assignee);
        EventBus.newAssignee = response.data;
        alert('Assignee created!');
      } catch (error) {
        console.log(error);
        alert('Assignee not created!');
      }
    },
  },
});
</script>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 20px;
}

.form {
  width: 80%;
  max-width: 500px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #f9f9f9;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

input[type="text"] {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

button {
  display: block;
  width: 100%;
  padding: 10px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

button:hover {
  background-color: #45a049;
}
</style>
