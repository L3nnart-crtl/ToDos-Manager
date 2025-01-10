<template>
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
    </div>

    <div v-show="assignees.length > 0" class="assignee-details">
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
    </div>

    <div v-if="errorMessage" class="error-message">
      <p>{{ errorMessage }}</p>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue';
import axios from 'axios';

export default defineComponent({
  name: 'SearchAssignee',
  setup() {
    const searchCriterion = ref('prename');
    const searchValue = ref('');
    const assignees = ref<any[]>([]);
    const errorMessage = ref<string | null>(null);

    const searchAssignee = async () => {
      errorMessage.value = null;
      assignees.value = [];

      if (!searchValue.value.trim()) {
        errorMessage.value = 'Please enter a value to search';
        return;
      }

      try {
        if (searchCriterion.value === 'id') {
          const response = await axios.get(`/api/v1/assignees/${searchValue.value.trim()}`);
          assignees.value = [response.data];
        } else {
          const response = await axios.get('/api/v1/assignees');
          assignees.value = response.data.filter((assignee: any) => {
            const field = assignee[searchCriterion.value]?.toLowerCase();
            return field && field.includes(searchValue.value.toLowerCase());
          });
        }
      } catch (error) {
        errorMessage.value = searchCriterion.value === 'id'
            ? 'No assignee found with the given ID'
            : 'Error fetching assignees';
      }
    };

    const editAssignee = (index: number) => {
      alert(`Editing assignee at index ${index}`);
    };

    const deleteAssignee = async (id: number) => {
      const confirmed = window.confirm('Are you sure you want to delete this assignee?');
      if (confirmed) {
        try {
          await axios.delete(`/api/v1/assignees/${id}`);
          assignees.value = assignees.value.filter(assignee => assignee.id !== id);
        } catch (error) {
          console.error('Error deleting assignee:', error);
        }
      }
    };

    return {
      searchCriterion,
      searchValue,
      assignees,
      errorMessage,
      searchAssignee,
      editAssignee,
      deleteAssignee,
    };
  },
});
</script>

<style scoped>
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

.error-message {
  color: red;
  font-size: 14px;
  margin-top: 15px;
}

.table-container {
  max-height: 180px;
  overflow-y: auto;
  width: 100%;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  padding: 3px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

th {
  background-color: #4CAF50;
  color: white;
  position: sticky;
  top: 0; /* Fixiert die Kopfzeile am oberen Rand */
  z-index: 1; /* Stellt sicher, dass die Kopfzeile immer oben bleibt, wenn gescrollt wird */
}

td {
  word-wrap: break-word;
}

td.actions {
  width: 100px;
  text-align: center;
}

button.edit {
  background-color: #4CAF50;
  color: white;
  font-size: 12px;
  padding: 6px 3px;
  border-radius: 4px;
  cursor: pointer;
}

button.delete {
  background-color: #f44336;
  color: white;
  font-size: 12px;
  padding: 6px 3px;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  opacity: 0.8;
}
</style>
