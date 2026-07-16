import apiClient from './axiosConfig';
import {
  QUERY_DASHBOARD_USER,
  QUERY_DASHBOARD_ACCOUNT,
  QUERY_TRANSACTIONS_USER,
  QUERY_TRANSACTIONS_REFNO,
  QUERY_TRANSACTIONS_MINI,
  QUERY_QR_LOOKUP,
  QUERY_USER_BANK_DETAILS,
  QUERY_USER_BANK_BALANCE,
  QUERY_BANK_BALANCE,
  QUERY_SPLIT_BILL,
  QUERY_SPLIT_PARTICIPANTS,
  QUERY_SPLIT_SUMMARY,
  QUERY_SPLIT_CREATED,
  QUERY_SPLIT_PENDING,
} from './endpoints';

export const getDashboardUser = (userId) =>
  apiClient.get(QUERY_DASHBOARD_USER(userId));

export const getDashboardAccount = (bankId) =>
  apiClient.get(QUERY_DASHBOARD_ACCOUNT(bankId));

export const getUserTransactions = (userId, page = 0, size = 10) =>
  apiClient.get(QUERY_TRANSACTIONS_USER(userId), { params: { page, size } });

export const getTransactionByRefNo = (refNo) =>
  apiClient.get(QUERY_TRANSACTIONS_REFNO(refNo));

export const getMiniStatement = (userId) =>
  apiClient.get(QUERY_TRANSACTIONS_MINI(userId));

export const lookupQr = (upiId) =>
  apiClient.get(QUERY_QR_LOOKUP(upiId));

export const getUserBankDetails = (userId) =>
  apiClient.get(QUERY_USER_BANK_DETAILS(userId));

export const getUserBankBalances = (userId) =>
  apiClient.get(QUERY_USER_BANK_BALANCE(userId));

export const getBankBalance = (bankId) =>
  apiClient.get(QUERY_BANK_BALANCE(bankId));

// Split bill
export const getSplitBill = (id) =>
  apiClient.get(QUERY_SPLIT_BILL(id));
export const getSplitParticipants = (id) =>
  apiClient.get(QUERY_SPLIT_PARTICIPANTS(id));
export const getSplitSummary = (id) =>
  apiClient.get(QUERY_SPLIT_SUMMARY(id));
export const getCreatedSplitBills = (userId) =>
  apiClient.get(QUERY_SPLIT_CREATED(userId));
export const getPendingSplitBills = (userId) =>
  apiClient.get(QUERY_SPLIT_PENDING(userId));