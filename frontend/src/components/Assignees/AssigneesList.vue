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
          <td class="email-actions">{{ assignee.email }}</td>
          <td class="actions">
            <button class="edit" @click="editAssignee(index)">Edit</button>
            <button class="delete" @click="deleteAssignee(assignee.id)">Delete</button>
          </td>
        </tr>
        </tbody>
      </table>
    </div>

    <!-- Edit Assignee Modal -->
    <div v-if="editIndex !== null" class="modal-overlay">
      <div class="modal-content">
        <EditAssignee
            :assignee="assignees[editIndex]"
            @cancel="cancelEdit"
            @save="saveAssignee"
        />
      </div>
    </div>
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
    EditAssignee,
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
      // Bestätigungsfenster anzeigen
      const confirmed = window.confirm('Are you sure you want to delete this assignee?');

      if (confirmed) {
        try {
          await axios.delete(`/api/v1/assignees/${id}`);
          getAssignees(); // Liste der Assignees nach dem Löschen aktualisieren
        } catch (error) {
          console.error('Fehler beim Löschen des Assignees:', error);
        }
      } else {
        console.log('Delete operation was canceled.');
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
  max-height: 380px;
  overflow-y: auto;
  width: 100%;
  position: relative; /* Ensure the table container is positioned */
}

table {
  width: 100%;
  border-collapse: collapse;
  table-layout: auto;
}

th, td {
  padding: 3px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

th {
  background-color: #4CAF50;
  color: white;
  position: sticky; /* Keep the header sticky */
  top: 0; /* Stick to the top of the table container */
  z-index: 1; /* Ensure the header stays above the table body */
}

td {
  word-wrap: break-word;
  overflow: hidden;
  text-overflow: ellipsis;
}

td.email-actions {
  max-width: 120px;
}

td.actions {
  width: 40px;
  text-align: center;
}

button {
  margin: 1px;
  font-size: 12px;
  padding: 6px 3px;
  border-radius: 4px;
  cursor: pointer;
  text-align: center;
  width: 100%;
}

button.edit {
  background-color: #4CAF50;
  color: white;
  border: none;
}

button.edit:hover {
  background-color: #45a049;
}

button.delete {
  background-color: #f44336;
  color: white;
  border: none;
}

button.delete:hover {
  background-color: #e53935;
}

button:focus {
  outline: none;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  width: 400px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}
</style>
