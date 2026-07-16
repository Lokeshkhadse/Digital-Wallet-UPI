import { createAsyncThunk } from '@reduxjs/toolkit';
import { getUserTransactions, getMiniStatement } from '../../api/queryApi';

export const fetchTransactions = createAsyncThunk(
  'transactions/fetch',
  async ({ userId, page = 0, size = 10 }, { rejectWithValue }) => {
    try {
      const response = await getUserTransactions(userId, page, size);
      return response.data; // Page<TransferTransactionResponse>
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Failed to load transactions');
    }
  }
);

export const fetchMiniStatement = createAsyncThunk(
  'transactions/fetchMini',
  async (userId, { rejectWithValue }) => {
    try {
      const response = await getMiniStatement(userId);
      return response.data; // List<TransferTransactionResponse>
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Failed to load mini statement');
    }
  }
);