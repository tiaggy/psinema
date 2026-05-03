import client from './client';

export interface SnackItem {
  id: number;
  name: string;
  description: string;
  price: number;
  stockQuantity: number;
  imageUrl: string;
  available: boolean;
}

export interface SnackOrder {
  id: number;
  status: string;
  totalAmount: number;
  createdAt: string;
  order: { id: number };
  items: { id: number; snackItem: SnackItem; quantity: number; unitPrice: number }[];
}

export const getSnacks = () =>
  client.get<SnackItem[]>('/snacks').then(r => r.data);

export const createSnack = (data: Partial<SnackItem>) =>
  client.post<SnackItem>('/snacks', data).then(r => r.data);

export const updateSnack = (id: number, data: Partial<SnackItem>) =>
  client.put<SnackItem>(`/snacks/${id}`, data).then(r => r.data);

export const deleteSnack = (id: number) =>
  client.delete(`/snacks/${id}`);

export const getPendingSnackOrders = () =>
  client.get<SnackOrder[]>('/snack-orders/pending').then(r => r.data);

export const updateSnackOrderStatus = (id: number, status: string) =>
  client.put<SnackOrder>(`/snack-orders/${id}/status`, null, { params: { status } }).then(r => r.data);
