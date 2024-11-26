import { createApp } from 'vue';
import App from './App.vue';
import axios from 'axios';


const app = createApp(App);


app.config.globalProperties.$axios = axios.create({
    baseURL: 'http://localhost:8080/api/v1',
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    }
});


app.mount('#app');
