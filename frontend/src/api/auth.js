import apiClient from './client';

export const register = async (name, email, password) => {
  const response = await apiClient.post('/api/auth/register', {
    name,
    email,
    password,
  });
  return response.data;
};

export const login = async (email, password) => {
  const response = await apiClient.post('/api/auth/login', {
    email,
    password,
  });
  return response.data;
};

export const getCurrentUser = async () => {
  const response = await apiClient.get('/api/auth/me');
  return response.data;
};
