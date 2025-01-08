<template>
  <div class="todo-csv-downloader">
    <button @click="downloadTodosCsv" class="download-button">
      Download Todos as CSV
    </button>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import axios from 'axios';

export default defineComponent({
  name: 'TodoCsvDownloader',
  methods: {
    async downloadTodosCsv() {
      try {
        // Backend-API-Endpoint aufrufen, um die CSV-Daten zu erhalten
        const response = await axios.get('/api/v1/csv-downloads/todos', {
          responseType: 'blob', // Wichtiger Parameter, um die Datei korrekt zu handhaben
        });

        // CSV-Datei im Browser herunterladen
        const blob = new Blob([response.data], { type: 'text/csv;charset=utf-8;' });
        const url = window.URL.createObjectURL(blob);

        // Virtueller Link erstellen und den Download starten
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', 'todos.csv');
        document.body.appendChild(link);
        link.click();

        // Ressourcen bereinigen
        link.remove();
        window.URL.revokeObjectURL(url);
      } catch (error) {
        console.error('Error downloading CSV:', error);
      }
    },
  },
});
</script>

<style scoped>
.download-button {
  background-color: #4CAF50;
  color: white;
  padding: 10px 15px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

.download-button:hover {
  background-color: #45a049;
}

.download-button:focus {
  outline: none;
}
</style>
