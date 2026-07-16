
import apiClient from "./axiosConfig";

import {
  ACTION_TRANSFER,
  ACTION_DEPOSIT,
  ACTION_WITHDRAW,
  ACTION_QR_PAY,

  ACTION_ADD_BANK,
  ACTION_CREATE_BALANCE,

  ACTION_SCHEDULED_CREATE,
  ACTION_SCHEDULED_GET,
  ACTION_SCHEDULED_CANCEL,

  ACTION_SPLIT_CREATE,
  ACTION_SPLIT_PAY,
  ACTION_SPLIT_CANCEL,

  ACTION_UPI_PIN_CREATE,
  ACTION_UPI_PIN_CHANGE,
  ACTION_UPI_PIN_VALIDATE,
} from "./endpoints";

export const transferAmount = (payload) =>
  apiClient.post(ACTION_TRANSFER, payload);

export const depositAmount = (payload) =>
  apiClient.post(ACTION_DEPOSIT, payload);

export const withdrawAmount = (payload) =>
  apiClient.post(ACTION_WITHDRAW, payload);

export const qrPay = (payload) =>
  apiClient.post(ACTION_QR_PAY, payload);


// Bank
export const addBankDetails = (payload) =>
  apiClient.post(ACTION_ADD_BANK, payload);

export const createBalance = (payload) =>
  apiClient.post(ACTION_CREATE_BALANCE, payload);


// Scheduled Transfer
export const createScheduledTransfer = (payload) =>
  apiClient.post(ACTION_SCHEDULED_CREATE, payload);

export const getScheduledTransfer = (id) =>
  apiClient.get(ACTION_SCHEDULED_GET(id));

export const cancelScheduledTransfer = (id) =>
  apiClient.patch(ACTION_SCHEDULED_CANCEL(id));


// Split Bill
export const createSplitBill = (payload) =>
  apiClient.post(ACTION_SPLIT_CREATE, payload);

export const paySplitBill = (payload) =>
  apiClient.post(ACTION_SPLIT_PAY, payload);

export const cancelSplitBill = (splitBillId, userId) =>
  apiClient.put(
    ACTION_SPLIT_CANCEL(splitBillId),
    null,
    {
      params: { userId },
    }
  );


// UPI PIN
export const createUpiPin = (payload) =>
  apiClient.post(ACTION_UPI_PIN_CREATE, payload);

export const changeUpiPin = (payload) =>
  apiClient.post(ACTION_UPI_PIN_CHANGE, payload);

export const validateUpiPin = (payload) =>
  apiClient.post(ACTION_UPI_PIN_VALIDATE, payload);