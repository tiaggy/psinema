import client from './client';

export interface Hall {
  id: number;
  name: string;
  totalRows: number;
  seatsPerRow: number;
}

export const getHalls = () =>
  client.get<Hall[]>('/halls').then(r => r.data);

export const createHall = (data: { name: string; totalRows: number; seatsPerRow: number }) =>
  client.post<Hall>('/halls', data).then(r => r.data);
