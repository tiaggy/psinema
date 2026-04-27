import axios from 'axios';
import { useAuthStore } from '../store/authStore';

const BASE_URL = 'http://change-your-ip:8080/api';

const client = axios.create({ baseURL: BASE_URL });

client.interceptors.request.use(async (config) => {
  const token = useAuthStore.getState().token;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default client;
