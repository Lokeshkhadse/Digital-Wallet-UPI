import { createAsyncThunk } from '@reduxjs/toolkit';
import { login, register, getUserById } from '../../api/authApi';

// Helper to decode JWT token and extract payload
const decodeToken = (token) => {
  try {
    const payload = token.split('.')[1];
    return JSON.parse(atob(payload));
  } catch {
    return null;
  }
};

export const loginUser = createAsyncThunk(
  'auth/login',
  async ({ email, password }, { rejectWithValue, dispatch }) => {
    try {
      const response = await login(email, password);
      const { accessToken, refreshToken, tokenType } = response.data;

      // Decode token to extract userId (if present)
      const decoded = decodeToken(accessToken);
      let userId = null;
      if (decoded && decoded.id) {
        userId = decoded.id;
      } else {
        console.warn('Token does not contain "id" claim. Dashboard may not work.');
      }

      // Store all auth data in localStorage
      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('refreshToken', refreshToken);
      localStorage.setItem('userEmail', email);
      if (userId) {
        localStorage.setItem('userId', userId);
      }

      // If we have userId, fetch the full user profile and return it
      if (userId) {
        const profileResponse = await getUserById(userId);
        const user = profileResponse.data; // UserResponseDto
        return { accessToken, refreshToken, tokenType, email, user };
      }

      // Otherwise, return only tokens (profile will be null)
      return { accessToken, refreshToken, tokenType, email, user: null };
    } catch (error) {
      const message = error.response?.data?.message || 'Login failed';
      return rejectWithValue(message);
    }
  }
);

export const registerUser = createAsyncThunk(
  'auth/register',
  async (userData, { rejectWithValue }) => {
    try {
      const response = await register(userData);
      return response.data;
    } catch (error) {
      const message = error.response?.data?.message || 'Registration failed';
      return rejectWithValue(message);
    }
  }
);

export const fetchUserProfile = createAsyncThunk(
  'auth/fetchProfile',
  async (userId, { rejectWithValue }) => {
    try {
      const response = await getUserById(userId);
      return response.data; // UserResponseDto
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Failed to fetch profile');
    }
  }
);

// Optional: restore user on refresh (called from App.jsx)
export const restoreUser = createAsyncThunk(
  'auth/restore',
  async (_, { rejectWithValue }) => {
    const userId = localStorage.getItem('userId');
    if (!userId) return rejectWithValue('No user ID found');
    try {
      const response = await getUserById(parseInt(userId, 10));
      return response.data;
    } catch (error) {
      // If profile fetch fails, clear invalid storage
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('userId');
      localStorage.removeItem('userEmail');
      return rejectWithValue('Session expired');
    }
  }
);