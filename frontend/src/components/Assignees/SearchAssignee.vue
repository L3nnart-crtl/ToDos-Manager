<template>
    <h1>Search Assignee</h1>
    <div class="search-form">
      <select v-model="searchCriterion">
        <option value="id">ID</option> <!-- Neue Option für die ID -->
        <option value="prename">First Name</option>
        <option value="name">Last Name</option>
        <option value="email">Email</option>
      </select>
      <input v-model="searchValue" placeholder="Enter search value" />
      <button @click="searchAssignee">Search</button>
    </div>
    <div v-if="assignees.length > 0">
      <h2>Assignee Details</h2>
      <ul>
        <li v-for="assignee in assignees" :key="assignee.id">
          <p><strong>ID:</strong> {{ assignee.id }}</p>
          <p><strong>First Name:</strong> {{ assignee.prename }}</p>
          <p><strong>Last Name:</strong> {{ assignee.name }}</p>
          <p><strong>Email:</strong> {{ assignee.email }}</p>
        </li>
      </ul>
    </div>
    <div v-if="errorMessage" class="error-message">
      <p>{{ errorMessage }}</p>
    </div>
</template>


<script lang="ts">
import { defineComponent, ref } from 'vue';
import axios from 'axios';

export default defineComponent({
  name: 'SearchAssignee',
  setup() {
    const searchCriterion = ref('prename'); // Standardkriterium
    const searchValue = ref(''); // Eingabewert
    const assignees = ref([]); // Ergebnisliste
    const errorMessage = ref(null); // Fehlermeldung

    const searchAssignee = async () => {
      errorMessage.value = null;
      assignees.value = [];

      if (!searchValue.value.trim()) {
        errorMessage.value = 'Please enter a value to search';
        return;
      }

      try {
        if (searchCriterion.value === 'id') {
          // Suche nach ID
          const response = await axios.get(`/api/v1/assignees/${searchValue.value.trim()}`);
          assignees.value = [response.data];
        } else {
          // Allgemeine Suche
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

    return {
      searchCriterion,
      searchValue,
      assignees,
      errorMessage,
      searchAssignee,
    };
  },
});
</script>


<style scoped>
/* Gesamter Container */
.container {
  background-color: #1e1e1e; /* Dunkler Hintergrund für den Container */
  color: #e0e0e0; /* Helle Textfarbe */
  padding: 20px;
  display: grid;
  border-radius: 8px;
  max-width: 600px;
  margin: 20px auto;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
}

/* Titel */
h1 {
  font-size: 24px;
  color: #e0e0e0;
  margin-bottom: 20px;
}

/* Formular zur Suche */
.search-form {
  display: grid;
  flex-direction: column;
  gap: 15px;
  margin-bottom: 20px;
}

/* Auswahlfeld für Kriterien */
select {
  background-color: #333; /* Dunkler Hintergrund für das Dropdown */
  color: #e0e0e0; /* Helle Textfarbe */
  padding: 12px;
  font-size: 16px;
  border-radius: 4px;
  border: 1px solid #444; /* Dunkle Umrandung */
}

select:focus {
  border-color: #4CAF50; /* Grüne Umrandung bei Fokus */
  outline: none;
  background-color: #444; /* Dunklerer Hintergrund bei Fokus */
}

/* Eingabefeld für den Suchwert */
input {
  background-color: #333; /* Dunkler Hintergrund für das Eingabefeld */
  color: #e0e0e0; /* Helle Textfarbe */
  padding: 12px;
  font-size: 16px;
  border-radius: 4px;
  border: 1px solid #444; /* Dunkle Umrandung */
}

input:focus {
  border-color: #4CAF50; /* Grüne Umrandung bei Fokus */
  outline: none;
  background-color: #444; /* Dunklerer Hintergrund bei Fokus */
}

/* Such-Button */
button {
  background-color: #4CAF50; /* Grüner Button */
  color: white;
  font-size: 16px;
  padding: 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  width: 100%;
}

button:hover {
  background-color: #45a049; /* Etwas dunkleres Grün bei Hover */
}

button:disabled {
  background-color: #666; /* Grauer Button, wenn deaktiviert */
  cursor: not-allowed;
}

/* Fehlernachricht */
.error-message {
  color: red;
  font-size: 14px;
  margin-top: 15px;
}

/* Details der Assignees */
h2 {
  font-size: 20px;
  color: #e0e0e0;
  margin-top: 20px;
  margin-bottom: 10px;
}

ul {
  list-style: none;
  padding: 0;
}

ul li {
  padding: 10px;
  border-bottom: 1px solid #444; /* Dunkler Rand für jedes Listenelement */
}

ul li p {
  margin: 5px 0;
}

ul li strong {
  color: #ccc; /* Helle Farbe für die Labels */
}

/* Anpassung für das Layout der Formular-Elemente */
.search-form {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.search-form input,
.search-form select,
.search-form button {
  width: 100%;
}

</style>
