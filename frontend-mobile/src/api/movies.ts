import client from './client';

export interface Movie {
  id: number;
  title: string;
  description: string;
  genre: string;
  durationMinutes: number;
  posterUrl: string;
  director: string;
  releaseDate: string;
}

export const getMovies = (params?: { genre?: string; title?: string }) =>
  client.get<Movie[]>('/movies', { params }).then(r => r.data);

export const getMovie = (id: number) =>
  client.get<Movie>(`/movies/${id}`).then(r => r.data);
