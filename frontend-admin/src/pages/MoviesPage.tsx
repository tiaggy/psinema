import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { getMovies, createMovie, updateMovie, deleteMovie } from '../api/movies';
import type { Movie, MovieRequest } from '../api/movies';
import styles from './Page.module.css';

const GENRES = ['ACTION','DRAMA','COMEDY','HORROR','SCI_FI','ANIMATION','THRILLER','ROMANCE','DOCUMENTARY','OTHER'];
const EMPTY: MovieRequest = { title: '', durationMinutes: 90, description: '', genre: '', director: '', posterUrl: '', releaseDate: '' };

export default function MoviesPage() {
  const qc = useQueryClient();
  const { data: movies, isLoading } = useQuery({ queryKey: ['movies'], queryFn: () => getMovies() });
  const [form, setForm] = useState<MovieRequest>(EMPTY);
  const [editId, setEditId] = useState<number | null>(null);
  const [showForm, setShowForm] = useState(false);

  const saveMutation = useMutation({
    mutationFn: () => editId ? updateMovie(editId, form) : createMovie(form),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['movies'] }); resetForm(); },
  });

  const deleteMutation = useMutation({
    mutationFn: deleteMovie,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['movies'] }),
  });

  const resetForm = () => { setForm(EMPTY); setEditId(null); setShowForm(false); };

  const startEdit = (m: Movie) => {
    setForm({ title: m.title, description: m.description, genre: m.genre, durationMinutes: m.durationMinutes, posterUrl: m.posterUrl, director: m.director, releaseDate: m.releaseDate });
    setEditId(m.id);
    setShowForm(true);
  };

  return (
    <div>
      <div className={styles.header}>
        <h1 className={styles.heading}>Movies</h1>
        <button className={styles.btn} onClick={() => { resetForm(); setShowForm(true); }}>+ Add Movie</button>
      </div>

      {showForm && (
        <div className={styles.formCard}>
          <h2 className={styles.subheading}>{editId ? 'Edit Movie' : 'New Movie'}</h2>
          <div className={styles.grid2}>
            <div>
              <label className={styles.label}>Title *</label>
              <input className={styles.input} value={form.title} onChange={e => setForm(p => ({ ...p, title: e.target.value }))} />
            </div>
            <div>
              <label className={styles.label}>Genre</label>
              <select className={styles.input} value={form.genre} onChange={e => setForm(p => ({ ...p, genre: e.target.value }))}>
                <option value="">Select genre</option>
                {GENRES.map(g => <option key={g}>{g}</option>)}
              </select>
            </div>
            <div>
              <label className={styles.label}>Duration (min) *</label>
              <input className={styles.input} type="number" value={form.durationMinutes} onChange={e => setForm(p => ({ ...p, durationMinutes: +e.target.value }))} />
            </div>
            <div>
              <label className={styles.label}>Director</label>
              <input className={styles.input} value={form.director} onChange={e => setForm(p => ({ ...p, director: e.target.value }))} />
            </div>
            <div>
              <label className={styles.label}>Release Date</label>
              <input className={styles.input} type="date" value={form.releaseDate} onChange={e => setForm(p => ({ ...p, releaseDate: e.target.value }))} />
            </div>
            <div>
              <label className={styles.label}>Poster URL</label>
              <input className={styles.input} value={form.posterUrl} onChange={e => setForm(p => ({ ...p, posterUrl: e.target.value }))} />
            </div>
          </div>
          <div>
            <label className={styles.label}>Description</label>
            <textarea className={styles.textarea} value={form.description} onChange={e => setForm(p => ({ ...p, description: e.target.value }))} rows={3} />
          </div>
          <div className={styles.actions}>
            <button className={styles.btn} onClick={() => saveMutation.mutate()} disabled={saveMutation.isPending || !form.title}>
              {saveMutation.isPending ? 'Saving…' : 'Save'}
            </button>
            <button className={styles.btnSecondary} onClick={resetForm}>Cancel</button>
          </div>
        </div>
      )}

      {isLoading ? <p>Loading…</p> : (
        <div className={styles.grid3}>
          {movies?.map(m => (
            <div key={m.id} className={styles.card}>
              {m.posterUrl && <img src={m.posterUrl} alt={m.title} className={styles.poster} />}
              <div className={styles.cardBody}>
                <div className={styles.cardTitle}>{m.title}</div>
                <div className={styles.cardMeta}>{m.genre} · {m.durationMinutes} min</div>
                {m.director && <div className={styles.cardMeta}>Dir. {m.director}</div>}
              </div>
              <div className={styles.cardActions}>
                <button className={styles.btnSmall} onClick={() => startEdit(m)}>Edit</button>
                <button className={styles.btnDanger} onClick={() => { if (confirm('Deactivate this movie?')) deleteMutation.mutate(m.id); }}>Remove</button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
