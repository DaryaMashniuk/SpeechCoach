import axiosInstance from './axiosInstance';

export const login = async (credentials) => {
  const response = await axiosInstance.post('/api/v1/auth/login', credentials);
  const { token, refreshToken, expiresAt } = response.data;
  if (!token) throw new Error('Token not received');
  return { token, refreshToken, expiresAt };
};

export const register = async (userData) => {
  const payload = { username: userData.username, email: userData.email, password: userData.password };
  const response = await axiosInstance.post('/api/v1/auth/register', payload);
  return response.data;
};

export const refreshToken = async (token) => {
  const response = await axiosInstance.post('/api/v1/auth/refresh', { token });
  return response.data;
};