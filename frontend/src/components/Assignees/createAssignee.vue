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
import { EventBus } from '../event-bus';
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

