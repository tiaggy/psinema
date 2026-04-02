import client from './client';

export interface Order {
  id: number;
  status: string;
  totalAmount: number;
  createdAt: string;
  user: { id: number; email: string; firstName: string; lastName: string };
  screening: { id: number; startTime: string; movie: { title: string }; hall: { name: string } };
}

export const getAllOrders = () =>
  client.get<Order[]>('/orders/all').then(r => r.data);

export const getOrder = (id: number) =>
  client.get<Order>(`/orders/${id}`).then(r => r.data);
