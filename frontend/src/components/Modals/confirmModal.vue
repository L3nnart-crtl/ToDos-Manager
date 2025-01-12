<template>
  <div v-if="isOpen" class="modal-overlay">
    <div class="modal-content">
      <p>{{ message }}</p>
      <div class="modal-actions">
        <button @click="confirmAction">Confirm</button> <!-- Translated from "Bestätigen" -->
        <button @click="closeModal">Cancel</button> <!-- Translated from "Abbrechen" -->
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "ConfirmModal",
  props: {
    isOpen: {
      type: Boolean,
      required: true,
    },
    message: {
      type: String,
      default: "Are you sure you want to proceed?", // Default message in English
    },
  },
  methods: {
    closeModal() {
      this.$emit("close"); // Emit "close" event to close the modal
    },
    confirmAction() {
      this.$emit("confirm"); // Emit "confirm" event to confirm the action
      this.closeModal();  // Close the modal after confirmation
    },
  },
};
</script>

<style scoped>
/* Modal Overlay */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5); /* Semi-transparent background */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999; /* Ensure modal appears on top */
}

/* Modal Content */
.modal-content {
  background-color: #333;
  padding: 20px;
  border-radius: 8px;
  width: 500px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* Slight shadow effect */
}

/* Modal Actions (Buttons) */
.modal-actions {
  display: flex;
  justify-content: flex-end; /* Align buttons to the right */
  margin-top: 10px;
}

/* Button Styling */
button {
  background-color: #4CAF50; /* Green button */
  color: white;
  padding: 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-left: 10px; /* Space between buttons */
}

button:hover {
  background-color: #45a049; /* Darker green on hover */
}

/* Cancel Button (Red) */
button:nth-child(2) {
  background-color: #f44336; /* Red button */
}

button:nth-child(2):hover {
  background-color: #e53935; /* Darker red on hover */
}
</style>
