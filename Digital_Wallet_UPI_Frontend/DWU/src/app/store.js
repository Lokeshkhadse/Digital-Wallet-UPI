// import { configureStore } from '@reduxjs/toolkit';
// import authReducer from '../features/auth/authSlice';
// import toastReducer from '../features/toast/toastSlice';
// import dashboardReducer from '../features/dashboard/dashboardSlice';
// import transactionsReducer from '../features/transactions/transactionsSlice';

// export const store = configureStore({
//   reducer: {
//     auth: authReducer,
//     toast: toastReducer,
//     dashboard: dashboardReducer,
//     transactions: transactionsReducer,
//   },
// });

// export type RootState = ReturnType<typeof store.getState>;
// export type AppDispatch = typeof store.dispatch;

import { configureStore } from '@reduxjs/toolkit';
import authReducer from '../features/auth/authSlice';
import toastReducer from '../features/toast/toastSlice';
import dashboardReducer from '../features/dashboard/dashboardSlice';
import transactionsReducer from '../features/transactions/transactionsSlice';

export const store = configureStore({
  reducer: {
    auth: authReducer,
    toast: toastReducer,
    dashboard: dashboardReducer,
    transactions: transactionsReducer,
  },
});

export default store;