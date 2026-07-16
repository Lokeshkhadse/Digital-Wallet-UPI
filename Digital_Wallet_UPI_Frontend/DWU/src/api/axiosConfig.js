import axios from 'axios';
import { GATEWAY_BASE_URL } from './endpoints';

// Create axios instance with base URL
const apiClient = axios.create({
  baseURL: GATEWAY_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to attach token
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor for error handling (optional)
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    // Optional: handle 401 and refresh token logic later
    return Promise.reject(error);
  }
);

export default apiClient;