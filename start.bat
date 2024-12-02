@echo off
echo Starting the backend, frontend

:: Start backend (Spring Boot) - Gehe ins Backend-Verzeichnis
start java -jar api/target/rest-api.jar

:: Start frontend (Vue.js mit Vite) - Gehe ins Frontend-Verzeichnis
start cmd /k "cd frontend && npm run dev"
