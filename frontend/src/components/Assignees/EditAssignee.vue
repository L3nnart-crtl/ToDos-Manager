<template>
  <div>
    <!-- Modal Overlay, which is only visible if assignee is set -->
    <div v-if="assignee" class="modal-overlay" @click="cancel">
      <div class="modal" @click.stop>
        <h1>Edit Assignee</h1>
        <form @submit.prevent="onSubmit">
          <div class="form-group">
            <label for="prename">First name:</label> <!-- Translated "Prename" -->
            <input type="text" v-model="editedAssignee.prename" id="prename" />
          </div>

          <div class="form-group">
            <label for="name">Last name:</label> <!-- Translated "Name" -->
            <input type="text" v-model="editedAssignee.name" id="name" />
          </div>

          <div class="form-group">
            <label for="email">Email:</label>
            <input type="text" v-model="editedAssignee.email" id="email" />
          </div>

          <button type="submit">Save</button>
          <button type="button" @click="cancel">Cancel</button>
        </form>
      </div>
    </div>

    <!-- MessageModal -->
    <MessageModal
        :isOpen="isModalOpen"
        :message="modalMessage"
        @close="isModalOpen = false"
    />
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, PropType } from 'vue';
import MessageModal from '@/components/Modals/messageModal.vue';

export default defineComponent({
  name: 'EditAssignee',
  props: {
    assignee: {
      type: Object as PropType<any>,
      required: true,
    },
  },
  components: {
    MessageModal,
  },
  setup(props, { emit }) {
    // Create a copy of the assignee data that can be edited
    const editedAssignee = ref({ ...props.assignee });
    const isModalOpen = ref(false);
    const modalMessage = ref('');

    const onSubmit = () => {
      // Validation logic
      if (!editedAssignee.value.prename || !editedAssignee.value.name) {
        modalMessage.value = 'ERROR: First name and Last name must have at least one character.';
        isModalOpen.value = true;
        return;
      }
      if (!editedAssignee.value.email || !editedAssignee.value.email.endsWith('uni-stuttgart.de')) {
        modalMessage.value = 'ERROR: Email must end with @uni-stuttgart.de';
        isModalOpen.value = true;
        return;
      }

      // If validation is successful, emit the changes
      emit('save', editedAssignee.value);
    };

    const cancel = () => {
      emit('cancel');
    };

    return {
      editedAssignee,
      isModalOpen,
      modalMessage,
      onSubmit,
      cancel,
    };
  },
});
</script>

<style scoped>
/* Modal Overlay */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

/* Modal Container */
.modal {
  background-color: #2e2e2e; /* Dark background */
  padding: 20px;
  border-radius: 8px;
  width: 400px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

/* Form Title */
h1 {
  font-size: 24px;
  margin-bottom: 20px;
  color: #ffffff;
  text-align: center;
}

/* Form Group Styling */
.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  font-size: 16px;
  color: #e0e0e0;
  margin-bottom: 5px;
}

/* Form Elements */
input {
  width: 100%;
  padding: 12px;
  font-size: 16px;
  background-color: #333;
  border: 1px solid #444;
  border-radius: 4px;
  color: #e0e0e0;
}

input:focus {
  border-color: #4CAF50;
  outline: none;
  background-color: #444;
}

/* Button Styles */
button {
  width: 100%;
  padding: 10px;
  font-size: 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  margin-top: 10px;
}

button[type="submit"] {
  background-color: #4CAF50; /* Green Button */
  color: white;
}

button[type="submit"]:hover {
  background-color: #45a049;
}

button[type="button"] {
  background-color: #e53935; /* Red Button */
  color: white;
}

button[type="button"]:hover {
  background-color: #f44336;
}
</style>
