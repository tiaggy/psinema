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

export const getSnacks = () =>
  client.get<SnackItem[]>('/snacks').then(r => r.data);

export const placeSnackOrder = (orderId: number, items: { snackItemId: number; quantity: number }[]) =>
  client.post('/snack-orders', { orderId, items }).then(r => r.data);
