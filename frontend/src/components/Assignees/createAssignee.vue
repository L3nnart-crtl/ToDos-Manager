<template>
  <form @submit.prevent="onSubmit">
    <h1>Create Assignee</h1>

    <!-- Container für Prename und Name nebeneinander -->
    <div class="name-container">
      <div>
        <input type="text" id="prename" v-model="assignee.prename" placeholder="Prename:" />
      </div>
      <div>
        <input type="text" id="name" v-model="assignee.name" placeholder="Name:"/>
      </div>
    </div>

    <div>
      <input type="text" id="email" v-model="assignee.email" placeholder="Email"/>
    </div>

    <button type="submit">Create</button>
  </form>
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
        EventBus.$emit('new-assignee', response.data);
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
/* Container für Prename und Name nebeneinander */
.name-container {
  display: flex;
  justify-content: space-between; /* Platz gleichmäßig verteilen */
  gap: 10px; /* Abstand zwischen den Feldern */
}

/* Formularelemente */
input,
select {
  width: 100%;
  padding: 12px;
  font-size: 16px;
  background-color: #333; /* Dunkler Hintergrund für Eingabefelder */
  border: 1px solid #444; /* Dunkle Umrandung */
  border-radius: 4px;
  color: #e0e0e0; /* Helle Textfarbe */
  margin-bottom: 10px; /* Verringert den Abstand zwischen den Feldern */
}

input:focus,
select:focus {
  border-color: #4CAF50; /* Grüne Umrandung bei Fokus */
  outline: none;
  background-color: #444; /* Etwas dunklerer Hintergrund bei Fokus */
}

/* Button zum Hinzufügen eines Assignees */
button {
  background-color: #4CAF50; /* Grüner Button */
  color: white;
  font-size: 16px;
  padding: 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  width: 100%;
}

button:hover {
  background-color: #45a049; /* Etwas dunkleres Grün bei Hover */
}

button:disabled {
  background-color: #666; /* Grauer Button, wenn deaktiviert */
  cursor: not-allowed;
}

</style>