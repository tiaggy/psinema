import client from './client';

export interface Order {
  id: number;
  status: string;
  totalAmount: number;
  createdAt: string;
  screening: { id: number; startTime: string; movie: { title: string }; hall: { name: string } };
}

export interface Ticket {
  id: number;
  status: string;
  type: string;
  qrCodeData: string;
  price: number;
  seat: { rowNumber: number; seatNumber: number; type: string };
  order: { id: number; screening: { startTime: string; movie: { title: string }; hall: { name: string } } };
}

export const confirmOrder = (data: {
  screeningId: number;
  reservationIds: number[];
  ticketTypes?: string[];
  paymentToken?: string;
}) => client.post<Order>('/orders', data).then(r => r.data);

export const getMyOrders = () =>
  client.get<Order[]>('/orders').then(r => r.data);

export const cancelOrder = (id: number, ticketIds?: number[]) =>
  client.post(`/orders/${id}/cancel`, { ticketIds });

export const getMyTickets = () =>
  client.get<Ticket[]>('/tickets').then(r => r.data);

export const getTicketQrUrl = (id: number) =>
  `${client.defaults.baseURL}/tickets/${id}/qr`;
