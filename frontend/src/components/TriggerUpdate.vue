<template>
  <div>
    <button @click="sendUpdateRequest">Update Assignees</button>
  </div>
</template>

<script>
import { EventBus } from './event-bus';

export default {
  name: 'TriggerUpdate',
  methods: {
    sendUpdateRequest() {

      EventBus.$emit('request-assignees-update');
    },
  },
};
</script>
