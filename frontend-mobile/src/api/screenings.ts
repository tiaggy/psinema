import client from './client';

export interface Screening {
  id: number;
  movie: { id: number; title: string; durationMinutes: number };
  hall: { id: number; name: string };
  startTime: string;
  endTime: string;
  basePrice: number;
  cancelled: boolean;
}

export interface SeatAvailability {
  seatId: number;
  rowNumber: number;
  seatNumber: number;
  type: string;
  status: 'AVAILABLE' | 'TEMPORARILY_RESERVED' | 'TAKEN';
}

export const getScreenings = (params?: { movieId?: number; date?: string }) =>
  client.get<Screening[]>('/screenings', { params }).then(r => r.data);

export const getScreening = (id: number) =>
  client.get<Screening>(`/screenings/${id}`).then(r => r.data);

export const getScreeningSeats = (id: number) =>
  client.get<SeatAvailability[]>(`/screenings/${id}/seats`).then(r => r.data);
