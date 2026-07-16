import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  message: null,
  type: 'info', // 'success' | 'error' | 'info'
  visible: false,
};

const toastSlice = createSlice({
  name: 'toast',
  initialState,
  reducers: {
    showToast: (state, action) => {
      state.message = action.payload.message;
      state.type = action.payload.type || 'info';
      state.visible = true;
    },
    hideToast: (state) => {
      state.visible = false;
      state.message = null;
    },
  },
});

export const { showToast, hideToast } = toastSlice.actions;
export default toastSlice.reducer;