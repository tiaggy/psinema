import client from './client';

export interface Ticket {
  id: number;
  status: string;
  type: string;
  qrCodeData: string;
  price: number;
  seat: { id: number; rowNumber: number; seatNumber: number };
  order: { id: number; screening: { id: number } };
}

export const validateTicket = (qrCodeData: string) =>
  client.post<Ticket>('/tickets/validate', { qrCodeData }).then(r => r.data);
