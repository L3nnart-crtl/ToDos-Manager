<template>
  <div>
    <h1>Assignees List</h1>

    <!-- Search Functionality -->
    <div class="search-container">
      <div class="search-form">
        <h1>Search Assignee</h1>
        <div class="search-fields">
          <select v-model="searchCriterion">
            <option value="id">ID</option>
            <option value="prename">First Name</option>
            <option value="name">Last Name</option>
            <option value="email">Email</option>
          </select>
          <input v-model="searchValue" placeholder="Enter search value" />
          <button @click="searchAssignee">Search</button>
        </div>
        <button class="show-all" @click="showAllAssignees">Show All</button>
      </div>
    </div>

    <!-- Assignees Table -->
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
        <tr v-for="(assignee, index) in filteredAssignees" :key="assignee.id">
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

    <!-- Error Message -->
    <div v-if="errorMessage" class="error-message">
      <p>{{ errorMessage }}</p>
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
import EditAssignee from './EditAssignee.vue';

export default defineComponent({
  name: 'AssigneeList',
  components: {
    EditAssignee,
  },
  setup() {
    const assignees = ref<any[]>([]);
    const filteredAssignees = ref<any[]>([]);
    const searchCriterion = ref('prename');
    const searchValue = ref('');
    const errorMessage = ref<string | null>(null);
    const editIndex = ref<number | null>(null);

    const getAssignees = async () => {
      try {
        const response = await axios.get('/api/v1/assignees');
        assignees.value = response.data;
        filteredAssignees.value = response.data; // Default to showing all assignees
      } catch (error) {
        console.error('Error fetching assignees:', error);
      }
    };

    const searchAssignee = () => {
      errorMessage.value = null;

      if (!searchValue.value.trim()) {
        errorMessage.value = 'Please enter a value to search';
        return;
      }

      filteredAssignees.value = assignees.value.filter((assignee: any) => {
        const field = assignee[searchCriterion.value];

        // Für ID-Vergleich: Konvertiere die Eingabe und den Wert in Strings
        if (searchCriterion.value === 'id') {
          return String(field) === searchValue.value.trim();
        }

        // Für andere Felder: Case-Insensitive Vergleich
        return field?.toLowerCase().includes(searchValue.value.toLowerCase());
      });

      if (filteredAssignees.value.length === 0) {
        errorMessage.value = 'No results found';
      }
    };

    const showAllAssignees = () => {
      errorMessage.value = null;
      filteredAssignees.value = [...assignees.value];
      searchValue.value = ''; // Clear search input
    };

    const editAssignee = (index: number) => {
      editIndex.value = index;
    };

    const deleteAssignee = async (id: number) => {
      const confirmed = window.confirm('Are you sure you want to delete this assignee?');
      if (confirmed) {
        try {
          await axios.delete(`/api/v1/assignees/${id}`);
          getAssignees(); // Refresh the list after deletion
        } catch (error) {
          console.error('Error deleting assignee:', error);
        }
      }
    };

    const saveAssignee = async (updatedAssignee: any) => {
      try {
        await axios.put(`/api/v1/assignees/${updatedAssignee.id}`, updatedAssignee);
        assignees.value[editIndex.value!] = updatedAssignee;
        filteredAssignees.value = [...assignees.value];
        editIndex.value = null;
      } catch (error) {
        console.error('Error saving assignee:', error);
      }
    };

    const cancelEdit = () => {
      editIndex.value = null;
    };

    getAssignees();

    return {
      assignees,
      filteredAssignees,
      searchCriterion,
      searchValue,
      errorMessage,
      editIndex,
      searchAssignee,
      showAllAssignees,
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
  max-height: 550px;
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
.search-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.search-form {
  display: flex;
  flex-direction: column;
  gap: 3px; /* Reduzierter Abstand zwischen den Elementen */
}

.search-fields {
  display: flex;
  gap: 10px; /* Abstand zwischen Filter, Eingabefeld und Button */
  align-items: center;
}

select,
input,
button {
  padding: 12px;
  font-size: 16px;
  background-color: #333;
  color: #e0e0e0;
  border-radius: 4px;
  border: 1px solid #444;
}

select {
  flex: 1;
}

input {
  flex: 2;
}

button {
  flex: 1;
  background-color: #4CAF50;
  color: white;
  padding: 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #45a049;
}
button.show-all {
  margin-bottom: 20px; /* Abstand zwischen Button und Tabelle */
}
</style>
