<template>
  <div>
    <h2>Assignee bearbeiten</h2>
    <form @submit.prevent="onSubmit">
      <div>
        <label for="prename">Vorname:</label>
        <input type="text" v-model="assignee.prename" id="prename" />

        <label for="name">Nachname:</label>
        <input type="text" v-model="assignee.name" id="name" />

        <label for="email">E-Mail:</label>
        <input type="text" v-model="assignee.email" id="email" />
      </div>
      <button type="submit">Speichern</button>
      <button type="button" @click="cancel">Abbrechen</button>
    </form>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, PropType } from 'vue';

export default defineComponent({
  name: 'EditAssignee',
  props: {
    assignee: {
      type: Object as PropType<any>,
      required: true,
    },
  },
  setup(props, { emit }) {
    const assignee = ref(props.assignee);

    const onSubmit = () => {
      emit('save', assignee.value);
    };

    const cancel = () => {
      emit('cancel');
    };

    return {
      assignee,
      onSubmit,
      cancel,
    };
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
