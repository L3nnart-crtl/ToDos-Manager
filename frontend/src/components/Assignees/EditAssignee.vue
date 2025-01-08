<template>
  <div>
    <!-- Modal Overlay, das nur sichtbar ist, wenn assignee gesetzt ist -->
    <div v-if="assignee" class="modal-overlay" @click="cancel">
      <div class="modal" @click.stop>
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
    </div>
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


