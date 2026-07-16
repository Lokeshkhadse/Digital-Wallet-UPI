


// ==============================
// API Gateway Base URL
// ==============================
export const GATEWAY_BASE_URL = "http://localhost:8085";


// ==============================
// AUTH
// ==============================
export const AUTH_LOGIN = "/auth/login";
export const AUTH_REGISTER = "/auth/register";
export const AUTH_REFRESH = "/auth/refresh";
export const AUTH_UPDATE_PASSWORD = (email) =>
  `/auth/update-password/${email}`;
export const AUTH_USER_BY_ID = (id) =>
  `/auth/users/${id}`;
export const AUTH_GET_ALL_USERS = "/auth/users";


// ==============================
// ACTION SERVICE
// ==============================
export const ACTION_TRANSFER = "/action/transfer/amount";
export const ACTION_DEPOSIT = "/action/deposit/amount";
export const ACTION_WITHDRAW = "/action/withdraw/amount";
export const ACTION_QR_PAY = "/action/qr/pay";

// Bank
export const ACTION_ADD_BANK = "/action/bank/add-bank-details";
export const ACTION_CREATE_BALANCE =
  "/action/user-bank-balance/create";

// Scheduled Transfer
export const ACTION_SCHEDULED_CREATE =
  "/action/scheduled-transfer/create";
export const ACTION_SCHEDULED_GET = (id) =>
  `/action/scheduled-transfer/${id}`;
export const ACTION_SCHEDULED_CANCEL = (id) =>
  `/action/scheduled-transfer/cancel/${id}`;

// Split Bill
export const ACTION_SPLIT_CREATE =
  "/action/split-bill/create";
export const ACTION_SPLIT_PAY =
  "/action/split-bill/pay";
export const ACTION_SPLIT_CANCEL = (id) =>
  `/action/split-bill/cancel/${id}`;

// UPI PIN
export const ACTION_UPI_PIN_CREATE =
  "/action/upi-pin/create";
export const ACTION_UPI_PIN_CHANGE =
  "/action/upi-pin/change";
export const ACTION_UPI_PIN_VALIDATE =
  "/action/upi-pin/validate";


// ==============================
// QUERY SERVICE
// ==============================
export const QUERY_DASHBOARD_USER = (userId) =>
  `/query/dashboard/user/${userId}`;

export const QUERY_DASHBOARD_ACCOUNT = (bankId) =>
  `/query/dashboard/account/${bankId}`;

export const QUERY_TRANSACTIONS_USER = (userId) =>
  `/query/transactions/user/${userId}`;

export const QUERY_TRANSACTIONS_REFNO = (refNo) =>
  `/query/transactions/getByRefNo/${refNo}`;

export const QUERY_TRANSACTIONS_MINI = (userId) =>
  `/query/transactions/mini-statement/${userId}`;

export const QUERY_QR_LOOKUP = (upiId) =>
  `/query/qr/lookup/${upiId}`;

export const QUERY_USER_BANK_DETAILS = (userId) =>
  `/query/user-bank-details/user/${userId}`;

export const QUERY_USER_BANK_BALANCE = (userId) =>
  `/query/user-bank-balance/getByUserId/${userId}`;

export const QUERY_BANK_BALANCE = (bankId) =>
  `/query/user-bank-balance/getByBankId/${bankId}`;

export const QUERY_SPLIT_BILL = (id) =>
  `/query/split-bill/${id}`;

export const QUERY_SPLIT_PARTICIPANTS = (id) =>
  `/query/split-bill/${id}/participants`;

export const QUERY_SPLIT_SUMMARY = (id) =>
  `/query/split-bill/${id}/summary`;

export const QUERY_SPLIT_CREATED = (userId) =>
  `/query/split-bill/created/${userId}`;

export const QUERY_SPLIT_PENDING = (userId) =>
  `/query/split-bill/pending/${userId}`;