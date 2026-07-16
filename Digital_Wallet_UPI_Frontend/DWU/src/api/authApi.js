

import apiClient from "./axiosConfig";

import {
  AUTH_LOGIN,
  AUTH_REGISTER,
  AUTH_REFRESH,
  AUTH_UPDATE_PASSWORD,
  AUTH_USER_BY_ID,
  AUTH_GET_ALL_USERS,
} from "./endpoints";

export const login = (email, password) =>
  apiClient.post(AUTH_LOGIN, {
    email,
    password,
  });

export const register = (userData) =>
  apiClient.post(AUTH_REGISTER, userData);

export const refreshToken = (refreshToken) =>
  apiClient.post(
    AUTH_REFRESH,
    null,
    {
      params: { refreshToken },
    }
  );

export const updatePassword = (
  email,
  newPassword
) =>
  apiClient.patch(
    AUTH_UPDATE_PASSWORD(email),
    null,
    {
      params: {
        newPassword,
      },
    }
  );

export const getUserById = (id) =>
  apiClient.get(AUTH_USER_BY_ID(id));


export const getAllUsers = () => apiClient.get(AUTH_GET_ALL_USERS);