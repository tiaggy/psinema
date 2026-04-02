import client from './client';

export interface AuthResponse {
  token: string;
  email: string;
  role: string;
  firstName: string;
  lastName: string;
}

export const login = (email: string, password: string) =>
  client.post<AuthResponse>('/auth/login', { email, password }).then(r => r.data);

export const register = (data: { email: string; password: string; firstName: string; lastName: string }) =>
  client.post<AuthResponse>('/auth/register', data).then(r => r.data);
