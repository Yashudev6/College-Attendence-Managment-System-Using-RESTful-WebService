# Pillai System - Postman API Testing Guide

This directory contains the official Postman API collection for testing all RESTful web services in **Pillai System**.

## 1. Files Included
* `Pillai_System_API.postman_collection.json` - Comprehensive API endpoints collection (Auth, Students, Teachers, Departments, Classes, Subjects, Attendance, Results).

## 2. How to Import
1. Open **Postman**.
2. Click **Import** (top left).
3. Choose `Pillai_System_API.postman_collection.json` file.
4. Set collection variable `baseUrl` to `http://localhost:8080` (default).

## 3. How to Test JWT Authenticated Endpoints
1. Execute `Auth > Login (Admin)` or `Login (Teacher)` or `Login (Student)`.
2. Copy the `token` string from the JSON response:
   ```json
   {
     "success": true,
     "message": "Login successful",
     "data": {
       "token": "eyJhbGciOiJIUzI1NiJ9...",
       "role": "ADMIN"
     }
   }
   ```
3. In Postman, for protected requests (e.g. `GET /api/students`), go to the **Authorization** tab, select **Bearer Token**, and paste the token string.
4. Click **Send**.
