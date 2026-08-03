# 💰 Expense Tracker

A full-stack personal expense tracking application with JWT-based authentication, category management, and expense analytics — built with **React** on the frontend and **Spring Boot + MySQL** on the backend.

![Java](https://img.shields.io/badge/Java-17%2B-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.2-brightgreen)
![React](https://img.shields.io/badge/React-18.2-blue)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

---

## 📖 Overview

Expense Tracker lets users register, log in, and manage their personal expenses across customizable categories. It features secure JWT authentication, a dashboard with spending summaries, and full CRUD for both expenses and categories.

---

## ✨ Features

- 🔐 **JWT-based authentication** — secure registration and login
- 📊 **Dashboard** — total expenses, monthly totals, and daily average at a glance
- 📝 **Expense management** — add, view, and delete expenses with amount, date, description, and payment method
- 🏷️ **Category management** — auto-seeded default categories on signup, plus custom category creation
- 👤 **Per-user data isolation** — every user only sees their own expenses and categories
- 📱 **Responsive UI** — built with React Bootstrap

---

## 🛠️ Tech Stack

**Frontend**
- React 18 (Create React App)
- React Router DOM v6
- React Bootstrap + Bootstrap 5
- Axios
- React Toastify
- Chart.js / React-Chartjs-2

**Backend**
- Java 17, Spring Boot 3.2.2
- Spring Security + JWT (jjwt)
- Spring Data JPA / Hibernate
- MySQL 8
- Maven

---

## 📂 Project Structure

```
expense-tracker/
├── backend/
│   └── src/main/java/com/expensetracker/backend/
│       ├── config/          # JWT & app configuration
│       ├── controller/      # REST controllers (Auth, Expense, Category)
│       ├── dto/             # Request/response DTOs
│       ├── entity/          # JPA entities (User, Expense, Category)
│       ├── repository/      # Spring Data repositories
│       ├── security/        # JWT filter & token provider
│       └── service/         # Business logic
└── frontend/
    └── src/
        ├── api/              # Axios instance & API service methods
        ├── auth/             # Login & Register pages
        ├── common/           # Navbar, PrivateRoute, LoadingSpinner
        ├── components/       # Reusable UI (CategoryList, etc.)
        ├── context/          # AuthContext (global auth state)
        ├── dashboard/        # Dashboard page
        └── expenses/         # ExpenseList & AddExpense pages
```

---

## 🚀 Getting Started

### Prerequisites
- Java 17+ (project targets Java 17, tested and run on JDK 25 LTS)
- Node.js 16+ and npm
- MySQL 8+

### 1. Clone the repository
```bash
git clone https://github.com/mayurraj9378/expense-tracker.git
cd expense-tracker
```

### 2. Backend Setup

```bash
cd backend
```

Create a MySQL database:
```sql
CREATE DATABASE expense_tracker;
```

Copy the example config and fill in your own values:
```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/expense_tracker
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password

jwt.secret=your_own_secret_key_here
jwt.expiration=86400000
jwt.header=Authorization
jwt.prefix=Bearer
```

> ⚠️ `application.properties` is git-ignored on purpose since it holds real credentials — never commit it. Use `application.properties.example` as the template.

Run the backend:
```bash
.\mvnw.cmd spring-boot:run    # Windows
./mvnw spring-boot:run        # macOS/Linux
```

Backend runs on `http://localhost:8080`.

### 3. Frontend Setup
```bash
cd frontend
npm install
npm start
```

Frontend runs on `http://localhost:3000` and proxies API calls to the backend.

---

## 🔑 Configuration

| File | Purpose | Committed to Git? |
|---|---|---|
| `backend/src/main/resources/application.properties.example` | Template showing required config keys | ✅ Yes |
| `backend/src/main/resources/application.properties` | Your real local DB credentials & JWT secret | ❌ No (git-ignored) |

---

## 📡 Key API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register a new user (auto-creates default categories) |
| POST | `/api/auth/login` | Log in and receive a JWT |
| GET | `/api/expenses` | Get current user's expenses |
| POST | `/api/expenses` | Add a new expense |
| DELETE | `/api/expenses/{id}` | Delete an expense |
| GET | `/api/categories` | Get current user's categories |
| POST | `/api/categories` | Add a custom category |
| POST | `/api/categories/default` | Seed default categories |
| DELETE | `/api/categories/{id}` | Delete a custom category |

All endpoints except `/api/auth/**` require an `Authorization: Bearer <token>` header.

---

## 🐛 Notable Engineering Decisions

A few real debugging and design decisions from building this project:

1. **JWT prefix parsing** — the auth filter now trims the `Bearer` prefix consistently before slicing the token, avoiding any stray whitespace in the extracted token.
2. **Client-side routing** — routes are wired to real page components under `PrivateRoute`, covering dashboard, expenses, add-expense, and categories.
3. **Per-user category seeding** — default categories are automatically created for every user at registration time, so the app is usable immediately after signup.
4. **Secrets management** — database credentials and JWT secret are kept out of version control via `.gitignore`, with a safe `.example` template checked in instead.

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

---

## 👤 Author

**Mayur Raj**
GitHub: [@mayurraj9378](https://github.com/mayurraj9378)
