#!/bin/bash
echo "Starting the backend and frontend..."

# Start backend (Spring Boot) - Gehe ins Backend-Verzeichnis
(cd api && java -jar target/rest-api.jar) &

# Start frontend (Vue.js mit Vite) - Gehe ins Frontend-Verzeichnis
(cd frontend && npm run dev) &
