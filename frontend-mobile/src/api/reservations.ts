import client from './client';

export interface ReservationResponse {
  reservationIds: number[];
  expiresAt: string;
}

export const reserveSeats = (screeningId: number, seatIds: number[]) =>
  client.post<ReservationResponse>('/reservations', { screeningId, seatIds }).then(r => r.data);
