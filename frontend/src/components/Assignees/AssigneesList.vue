<template>
  <div>
    <h1>Assignees List</h1>
    <div class="table-container">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>First Name</th>
            <th>Last Name</th>
            <th>Email</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(assignee, index) in assignees" :key="assignee.id">
            <td>{{ assignee.id }}</td>
            <td>{{ assignee.prename }}</td>
            <td>{{ assignee.name }}</td>
            <td>{{ assignee.email }}</td>
            <td>
              <button @click="editAssignee(index)">Edit</button>
              <button @click="deleteAssignee(assignee.id)">Delete</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

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

<style scoped>
.table-container {
  max-height: 400px; /* Maximalhöhe für den Container */
  overflow-y: auto;  /* Scrollbar wird angezeigt, wenn der Inhalt die Maximalhöhe überschreitet */
  margin-top: 20px;

}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

th {
  background-color: #4CAF50;
  color: white;
}

button {
  background-color: #4CAF50;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  margin-right: 5px;
}

button:hover {
  background-color: #45a049;
}

button:focus {
  outline: none;
}
</style>
