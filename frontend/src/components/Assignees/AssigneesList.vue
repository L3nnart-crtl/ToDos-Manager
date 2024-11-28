<template>
  <div>
    <h1>Assignees List</h1>
    <table>
      <thead>
      <tr>
        <th>ID</th>
        <th>Vorname</th>
        <th>Nachname</th>
        <th>E-Mail</th>
        <th>Aktionen</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="(assignee, index) in assignees" :key="assignee.id">
        <td>{{ assignee.id }}</td>
        <td>{{ assignee.prename }}</td>
        <td>{{ assignee.name }}</td>
        <td>{{ assignee.email }}</td>
        <td>
          <button @click="editAssignee(index)">Bearbeiten</button>
          <button @click="deleteAssignee(assignee.id)">Löschen</button>
        </td>
      </tr>
      </tbody>
    </table>

    <EditAssignee
        v-if="editIndex !== null"
        :assignee="assignees[editIndex]"
        @cancel="cancelEdit"
        @save="saveAssignee"
    />
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, watch } from 'vue';
import axios from 'axios';
import { EventBus } from '../event-bus';
import EditAssignee from './EditAssignee.vue';

export default defineComponent({
  name: 'AssigneeList',
  components: {
    EditAssignee
  },
  setup() {
    const assignees = ref<any[]>([]);
    const editIndex = ref<number | null>(null);

    const getAssignees = async () => {
      try {
        const response = await axios.get('/api/v1/assignees');
        assignees.value = response.data;
      } catch (error) {
        console.error('Fehler beim Abrufen der Assignees:', error);
      }
    };

    watch(() => EventBus.newAssignee, (newAssignee) => {
      if (newAssignee) {
        assignees.value.push(newAssignee);
        EventBus.newAssignee = null;
      }
    });

    const editAssignee = (index: number) => {
      editIndex.value = index;
    };

    const deleteAssignee = async (id: number) => {
      try {
        await axios.delete(`/api/v1/assignees/${id}`);
        getAssignees();
      } catch (error) {
        console.error('Fehler beim Löschen des Assignees:', error);
      }
    };

    const saveAssignee = async (updatedAssignee: any) => {
      try {
        await axios.put(`/api/v1/assignees/${updatedAssignee.id}`, updatedAssignee);
        assignees.value[editIndex.value!] = updatedAssignee;
        editIndex.value = null;
      } catch (error) {
        console.error('Fehler beim Speichern des Assignees:', error);
      }
    };

    const cancelEdit = () => {
      editIndex.value = null;
    };

    getAssignees();

    return {
      assignees,
      editIndex,
      editAssignee,
      deleteAssignee,
      saveAssignee,
      cancelEdit,
    };
  },
});
</script>

