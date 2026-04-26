import axios from 'axios';
import { useAuthStore } from '../store/authStore';

const BASE_URL = 'http://147.175.163.102:8080/api'; // Android emulator → localhost; change to your IP for physical device

const client = axios.create({ baseURL: BASE_URL });

client.interceptors.request.use(async (config) => {
  const token = useAuthStore.getState().token;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default client;
