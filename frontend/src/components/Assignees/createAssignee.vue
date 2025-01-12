<template>
  <div>
    <form @submit.prevent="onSubmit">
      <h1>Create Assignee</h1>

      <!-- Container for Prename and Name side by side -->
      <div class="name-container">
        <div>
          <input type="text" id="prename" v-model="assignee.prename" placeholder="Prename" />
        </div>
        <div>
          <input type="text" id="name" v-model="assignee.name" placeholder="Name" />
        </div>
      </div>

      <div>
        <input type="text" id="email" v-model="assignee.email" placeholder="Email" />
      </div>

      <button type="submit">Create</button>
    </form>

    <!-- MessageModal -->
    <MessageModal
        :isOpen="isModalOpen"
        :message="modalMessage"
        @close="isModalOpen = false"
    />
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import { EventBus } from '../event-bus';
import axios from 'axios';
import MessageModal from '@/components/Modals/messageModal.vue';

export default defineComponent({
  name: 'CreateAssignee',
  components: {
    MessageModal,
  },
  data() {
    return {
      assignee: {
        prename: '',  // First name
        name: '',     // Last name
        email: '',    // Email address
      },
      isModalOpen: false, // State of the MessageModal
      modalMessage: '',  // Message to be displayed in the modal
    };
  },
  methods: {
    async onSubmit() {
      try {
        const response = await axios.post('/api/v1/assignees', this.assignee);
        EventBus.$emit('new-assignee', response.data); // Use EventBus for reactive communication

        // Show success message
        this.modalMessage = 'Assignee created!';
        this.isModalOpen = true;

        // Reset the form
        this.assignee = {
          prename: '',
          name: '',
          email: '',
        };
      } catch (error) {
        console.error(error);

        // Show error message
        this.modalMessage = 'ERROR: First name must have at least one character, Last name must have at least one character, Email must end with uni-stuttgart.de';
        this.isModalOpen = true;
      }
    },
  },
});
</script>

<style scoped>
/* Container for Prename and Name side by side */
.name-container {
  display: flex;
  justify-content: space-between; /* Distribute space evenly */
  gap: 10px; /* Space between fields */
}

/* Form elements */
input,
select {
  width: 100%;
  padding: 12px;
  font-size: 16px;
  background-color: #333; /* Dark background for input fields */
  border: 1px solid #444; /* Dark border */
  border-radius: 4px;
  color: #e0e0e0; /* Light text color */
  margin-bottom: 10px; /* Reduces space between fields */
}

input:focus,
select:focus {
  border-color: #4CAF50; /* Green border on focus */
  outline: none;
  background-color: #444; /* Slightly darker background on focus */
}

/* Button to add an Assignee */
button {
  background-color: #4CAF50; /* Green button */
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
  background-color: #45a049; /* Slightly darker green on hover */
}

button:disabled {
  background-color: #666; /* Gray button when disabled */
  cursor: not-allowed;
}
</style>
