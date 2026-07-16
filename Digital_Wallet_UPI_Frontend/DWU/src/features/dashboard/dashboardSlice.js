import { createSlice } from '@reduxjs/toolkit';
import { fetchDashboard, fetchAccountDashboard } from './dashboardThunks';

const initialState = {
  userDashboard: null,      // { userId, totalBalance, linkedAccounts: [...] }
  accountDashboard: null,   // { bankId, bankName, accountNumber, upiId, balance, ... }
  isLoading: false,
  error: null,
};

const dashboardSlice = createSlice({
  name: 'dashboard',
  initialState,
  reducers: {
    clearDashboard: (state) => {
      state.userDashboard = null;
      state.accountDashboard = null;
      state.isLoading = false;
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    // User dashboard
    builder.addCase(fetchDashboard.pending, (state) => {
      state.isLoading = true;
      state.error = null;
    });
    builder.addCase(fetchDashboard.fulfilled, (state, action) => {
      state.isLoading = false;
      state.userDashboard = action.payload;
    });
    builder.addCase(fetchDashboard.rejected, (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
    });

    // Account dashboard
    builder.addCase(fetchAccountDashboard.pending, (state) => {
      state.isLoading = true;
      state.error = null;
    });
    builder.addCase(fetchAccountDashboard.fulfilled, (state, action) => {
      state.isLoading = false;
      state.accountDashboard = action.payload;
    });
    builder.addCase(fetchAccountDashboard.rejected, (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
    });
  },
});

export const { clearDashboard } = dashboardSlice.actions;
export default dashboardSlice.reducer;