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

export interface ScreeningRequest {
  movieId: number;
  hallId: number;
  startTime: string;
  basePrice: number;
}

export const getScreenings = (params?: { movieId?: number; date?: string }) =>
  client.get<Screening[]>('/screenings', { params }).then(r => r.data);

export const createScreening = (data: ScreeningRequest) =>
  client.post<Screening>('/screenings', data).then(r => r.data);

export const updateScreening = (id: number, data: ScreeningRequest) =>
  client.put<Screening>(`/screenings/${id}`, data).then(r => r.data);

export const cancelScreening = (id: number) =>
  client.post(`/screenings/${id}/cancel`);
