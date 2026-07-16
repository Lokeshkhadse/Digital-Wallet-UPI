import { createSlice } from '@reduxjs/toolkit';
import { loginUser, registerUser, fetchUserProfile, restoreUser } from './authThunks';

const initialState = {
  user: null,
  token: localStorage.getItem('accessToken') || null,
  isLoading: false,
  error: null,
  isAuthenticated: !!localStorage.getItem('accessToken'),
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    logout: (state) => {
      state.user = null;
      state.token = null;
      state.isAuthenticated = false;
      // Clear ALL auth data from localStorage
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('userId');
      localStorage.removeItem('userEmail');
    },
    setToken: (state, action) => {
      state.token = action.payload;
      state.isAuthenticated = true;
      localStorage.setItem('accessToken', action.payload);
    },
  },
  extraReducers: (builder) => {
    // ---- Login ----
    builder.addCase(loginUser.pending, (state) => {
      state.isLoading = true;
      state.error = null;
    });
    builder.addCase(loginUser.fulfilled, (state, action) => {
      state.isLoading = false;
      state.token = action.payload.accessToken;
      state.isAuthenticated = true;
      state.user = action.payload.user || null;
      // If user object is present, store it; localStorage already has userId & email
    });
    builder.addCase(loginUser.rejected, (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
      state.isAuthenticated = false;
      state.user = null;
    });

    // ---- Register ----
    builder.addCase(registerUser.pending, (state) => {
      state.isLoading = true;
      state.error = null;
    });
    builder.addCase(registerUser.fulfilled, (state) => {
      state.isLoading = false;
    });
    builder.addCase(registerUser.rejected, (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
    });

    // ---- Fetch Profile ----
    builder.addCase(fetchUserProfile.pending, (state) => {
      state.isLoading = true;
    });
    builder.addCase(fetchUserProfile.fulfilled, (state, action) => {
      state.isLoading = false;
      state.user = action.payload;
    });
    builder.addCase(fetchUserProfile.rejected, (state) => {
      state.isLoading = false;
      state.user = null;
    });

    // ---- Restore User on Refresh ----
    builder.addCase(restoreUser.pending, (state) => {
      state.isLoading = true;
    });
    builder.addCase(restoreUser.fulfilled, (state, action) => {
      state.isLoading = false;
      state.user = action.payload;
      state.isAuthenticated = true;
      state.token = localStorage.getItem('accessToken');
    });
    builder.addCase(restoreUser.rejected, (state) => {
      state.isLoading = false;
      state.user = null;
      state.isAuthenticated = false;
      state.token = null;
    });
  },
});

export const { logout, setToken } = authSlice.actions;
export default authSlice.reducer;