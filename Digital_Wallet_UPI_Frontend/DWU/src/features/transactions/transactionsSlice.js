import { createSlice } from '@reduxjs/toolkit';
import { fetchTransactions, fetchMiniStatement } from './transactionsThunks';

const initialState = {
  transactions: [],           // list of TransferTransactionResponse
  miniStatement: [],
  pagination: { page: 0, size: 10, totalElements: 0, totalPages: 0 },
  isLoading: false,
  error: null,
};

const transactionsSlice = createSlice({
  name: 'transactions',
  initialState,
  reducers: {
    clearTransactions: (state) => {
      state.transactions = [];
      state.miniStatement = [];
      state.isLoading = false;
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    // Fetch paginated transactions
    builder.addCase(fetchTransactions.pending, (state) => {
      state.isLoading = true;
      state.error = null;
    });
    builder.addCase(fetchTransactions.fulfilled, (state, action) => {
      state.isLoading = false;
      state.transactions = action.payload.content;
      state.pagination = {
        page: action.payload.pageable.pageNumber,
        size: action.payload.pageable.pageSize,
        totalElements: action.payload.totalElements,
        totalPages: action.payload.totalPages,
      };
    });
    builder.addCase(fetchTransactions.rejected, (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
    });

    // Mini statement
    builder.addCase(fetchMiniStatement.pending, (state) => {
      state.isLoading = true;
    });
    builder.addCase(fetchMiniStatement.fulfilled, (state, action) => {
      state.isLoading = false;
      state.miniStatement = action.payload;
    });
    builder.addCase(fetchMiniStatement.rejected, (state) => {
      state.isLoading = false;
    });
  },
});

export const { clearTransactions } = transactionsSlice.actions;
export default transactionsSlice.reducer;