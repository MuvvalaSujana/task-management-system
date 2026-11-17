# Task Management System

Full-stack CRUD task manager using Java 17, Spring Boot, React (Vite), and MySQL.

## Features
- Create, list, edit, and delete tasks
- Status: TODO, IN_PROGRESS, DONE
- Priority: LOW, MEDIUM, HIGH
- Due date, description, search, and status filter
- REST API, validation, centralized error handling, and a basic controller test

This version does not include login/JWT/role-based access. Do not claim those features on your resume unless you add them.

## Requirements
- JDK 17+
- Maven 3.9+
- Node.js 20.19+ or 22.12+
- MySQL 8+

## Database
In MySQL Workbench, run:
```sql
CREATE DATABASE task_manager_db;
```
Default backend connection is `root` / `password`. Change it in `backend/src/main/resources/application.properties`.

## Run backend (PowerShell)
```powershell
cd task-management-system\backend
mvn spring-boot:run
```
Backend runs at http://localhost:8080

## Run frontend
Open a SECOND PowerShell window:
```powershell
cd task-management-system\frontend
npm install
npm run dev
```
Open http://localhost:5173

## Test backend
From `backend`:
```powershell
mvn test
mvn clean package
```

## API endpoints
- GET `/api/tasks` (optional `?status=TODO&search=report`)
- GET `/api/tasks/{id}`
- POST `/api/tasks`
- PUT `/api/tasks/{id}`
- DELETE `/api/tasks/{id}`

Example POST JSON:
```json
{
  "title": "Prepare interview",
  "description": "Revise Java, SQL and REST APIs",
  "status": "TODO",
  "priority": "HIGH",
  "dueDate": "2026-11-20"
}
```

## Troubleshooting
- Access denied: verify MySQL username/password.
- Unknown database: create `task_manager_db`.
- `mvn` not recognized: install Maven, add its `bin` directory to PATH, reopen PowerShell.
- `npm` not recognized: install Node.js LTS, reopen PowerShell.
- Port 8080 busy: stop the other process or change `server.port`.
