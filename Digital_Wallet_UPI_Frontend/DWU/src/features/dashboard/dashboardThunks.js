import { createAsyncThunk } from '@reduxjs/toolkit';
import { getDashboardUser, getDashboardAccount } from '../../api/queryApi';

export const fetchDashboard = createAsyncThunk(
  'dashboard/fetch',
  async (userId, { rejectWithValue }) => {
    try {
      const response = await getDashboardUser(userId);
      // response.data: GenericResponse<DashboardHomeResponse>
      return response.data.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Failed to load dashboard');
    }
  }
);

export const fetchAccountDashboard = createAsyncThunk(
  'dashboard/fetchAccount',
  async (bankId, { rejectWithValue }) => {
    try {
      const response = await getDashboardAccount(bankId);
      return response.data.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Failed to load account dashboard');
    }
  }
);