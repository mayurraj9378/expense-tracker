import axios from "axios";

// API URL from environment variable
const API_URL = process.env.REACT_APP_API_URL || "http://localhost:8080/api";

const api = axios.create({
  baseURL: API_URL,
  timeout: Number(process.env.REACT_APP_API_TIMEOUT) || 30000,
  headers: {
    "Content-Type": "application/json",
  },
});

// Add JWT token to every request
api.interceptors.request.use(
  (config) => {
    let token = localStorage.getItem("token");

    if (token) {
      token = token.replace(/\s/g, "");
      localStorage.setItem("token", token);
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error),
);

// Handle unauthorized responses
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem("token");
      localStorage.removeItem("user");
      window.location.href = "/login";
    }

    return Promise.reject(error);
  },
);

export const authAPI = {
  login: (data) => api.post("/auth/login", data),
  register: (data) => api.post("/auth/register", data),
};

export const expenseAPI = {
  getAll: () => api.get("/expenses"),
  getTotal: (start, end) =>
    api.get(`/expenses/total?startDate=${start}&endDate=${end}`),
  create: (data) => api.post("/expenses", data),
  delete: (id) => api.delete(`/expenses/${id}`),
};

export const categoryAPI = {
  getAll: () => api.get("/categories"),
  create: (data) => api.post("/categories", data),
  delete: (id) => api.delete(`/categories/${id}`),
  createDefaults: () => api.post("/categories/default"),
};

export default api;
