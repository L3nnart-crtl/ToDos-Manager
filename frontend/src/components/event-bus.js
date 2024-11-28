import { reactive } from 'vue';

export const EventBus = reactive({
    events: {},

    // Methode zum Erzeugen eines Events
    $on(event, callback) {
        if (!this.events[event]) {
            this.events[event] = [];
        }
        this.events[event].push(callback);
    },

    // Methode zum Auslösen eines Events
    $emit(event, data) {
        if (this.events[event]) {
            this.events[event].forEach(callback => callback(data));
        }
    },

    // Methode zum Entfernen eines Events
    $off(event, callback) {
        if (!this.events[event]) return;

        const index = this.events[event].indexOf(callback);
        if (index > -1) {
            this.events[event].splice(index, 1);
        }
    }
});
