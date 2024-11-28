<template>
  <div class="modal fade" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">Neues ToDo Erstellen</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="submit">
            <div class="mb-3">
              <label for="title" class="form-label">Titel</label>
              <input v-model="newTodo.title" type="text" class="form-control" id="title" required />
            </div>
            <div class="mb-3">
              <label for="description" class="form-label">Beschreibung</label>
              <textarea v-model="newTodo.description" class="form-control" id="description" required></textarea>
            </div>
            <button type="submit" class="btn btn-primary">Erstellen</button>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "ToDoCreateModal",
  data() {
    return {
      newTodo: {
        title: "",
        description: "",
        assigneeIdList: [],
        dueDate: null,
      },
    };
  },
  methods: {
    async submit() {
      try {
        const response = await axios.post("/api/v1/todos", this.newTodo);
        this.$emit("created", response.data);
        this.$emit("close");
      } catch (error) {
        console.error(error);
      }
    },
  },
};
</script>
