<template>
  <div>
    <h1>Assignees List</h1>
    <table>
      <thead>
      <tr>
        <th>Vorname</th>
        <th>Nachname</th>
        <th>E-Mail</th>
        <th>Aktionen</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="(assignee, index) in assignees" :key="assignee.id">
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
import { EventBus } from './event-bus';
import EditAssignee from './EditAssignee.vue';

export default defineComponent({
  name: 'AssigneeList',
  components: {
    EditAssignee
  },
  setup() {
    const assignees = ref<any[]>([]);
    const editIndex = ref<number | null>(null);

    // Abrufen der Assignees
    const getAssignees = async () => {
      try {
        const response = await axios.get('/api/v1/assignees');
        assignees.value = response.data;
      } catch (error) {
        console.error('Fehler beim Abrufen der Assignees:', error);
      }
    };

    // Aktualisieren der Liste, wenn ein neues Assignee über den EventBus hinzugefügt wird
    watch(() => EventBus.newAssignee, (newAssignee) => {
      if (newAssignee) {
        assignees.value.push(newAssignee); // Füge das neue Assignee zur Liste hinzu
        EventBus.newAssignee = null; // Event zurücksetzen
      }
    });

    // Bearbeiten eines Assignees
    const editAssignee = (index: number) => {
      editIndex.value = index;
    };

    // Löschen eines Assignees
    const deleteAssignee = async (id: number) => {
      try {
        await axios.delete(`/api/v1/assignees/${id}`);
        getAssignees(); // Liste nach dem Löschen erneut abrufen
      } catch (error) {
        console.error('Fehler beim Löschen des Assignees:', error);
      }
    };

    // Speichern eines bearbeiteten Assignees
    const saveAssignee = async (updatedAssignee: any) => {
      try {
        await axios.put(`/api/v1/assignees/${updatedAssignee.id}`, updatedAssignee);
        assignees.value[editIndex.value!] = updatedAssignee; // Assignee aktualisieren
        editIndex.value = null; // Zurück zur Liste
      } catch (error) {
        console.error('Fehler beim Speichern des Assignees:', error);
      }
    };

    // Bearbeitung abbrechen
    const cancelEdit = () => {
      editIndex.value = null;
    };

    // Initiales Laden der Assignees
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
.container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

table {
  width: 80%;
  border-collapse: collapse;
  margin-top: 20px;
}

th, td {
  border: 1px solid #ddd;
  padding: 8px;
}

th {
  background-color: #f2f2f2;
  text-align: left;
}

button {
  background-color: #f44336;
  color: white;
  border: none;
  padding: 5px 10px;
  cursor: pointer;
}

button:hover {
  background-color: #d32f2f;
}
</style>