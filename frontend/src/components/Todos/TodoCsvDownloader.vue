<template>
  <div class="todo-csv-downloader">
    <!-- Button to download Todos as CSV -->
    <button @click="openModal" class="download-button">
      Download Todos as CSV
    </button>

    <!-- Confirmation Modal -->
    <ConfirmModal
        v-if="isModalOpen"
        :isOpen="isModalOpen"
        message="Are you sure you want to download the CSV file?"
        @close="closeModal"
        @confirm="downloadTodosCsv"
    />
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue';
import axios from 'axios';
import ConfirmModal from '@/components/Modals/confirmModal.vue';

export default defineComponent({
  name: 'TodoCsvDownloader',
  components: {
    ConfirmModal,
  },
  setup() {
    const isModalOpen = ref(false);

    // Open the modal when the user clicks the download button
    const openModal = () => {
      isModalOpen.value = true;
    };

    // Close the modal
    const closeModal = () => {
      isModalOpen.value = false;
    };

    // Download the CSV if confirmed
    const downloadTodosCsv = async () => {
      try {
        // Backend API endpoint to fetch the CSV data
        const response = await axios.get('/api/v1/csv-downloads/todos', {
          responseType: 'blob', // Important to handle the file properly
        });

        // Create a blob from the CSV data and trigger the download
        const blob = new Blob([response.data], { type: 'text/csv;charset=utf-8;' });
        const url = window.URL.createObjectURL(blob);

        // Create a temporary link and start the download
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', 'todos.csv');
        document.body.appendChild(link);
        link.click();

        // Clean up resources
        link.remove();
        window.URL.revokeObjectURL(url);
      } catch (error) {
        console.error('Error downloading CSV:', error);
      }
    };

    return {
      isModalOpen,
      openModal,
      closeModal,
      downloadTodosCsv,
    };
  },
});
</script>

<style scoped>
/* Button styling for download */
.download-button {
  background-color: #4CAF50;
  color: white;
  padding: 5px 15px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 18px;
  font-family: 'Roboto', sans-serif;
}

/* Hover effect for the download button */
.download-button:hover {
  background-color: #45a049;
}

/* Focus style for the button */
.download-button:focus {
  outline: none;
}
</style>
