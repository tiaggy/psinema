import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { getScreenings, createScreening, updateScreening, cancelScreening } from '../api/screenings';
import type { Screening, ScreeningRequest } from '../api/screenings';
import { getMovies } from '../api/movies';
import { getHalls } from '../api/halls';
import styles from './Page.module.css';

const EMPTY: ScreeningRequest = { movieId: 0, hallId: 0, startTime: '', basePrice: 0 };

export default function ScreeningsPage() {
  const qc = useQueryClient();
  const { data: screenings, isLoading } = useQuery({ queryKey: ['screenings'], queryFn: () => getScreenings() });
  const { data: movies } = useQuery({ queryKey: ['movies'], queryFn: () => getMovies() });
  const { data: halls } = useQuery({ queryKey: ['halls'], queryFn: getHalls });
  const [form, setForm] = useState<ScreeningRequest>(EMPTY);
  const [editId, setEditId] = useState<number | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [error, setError] = useState('');

  const saveMutation = useMutation({
    mutationFn: () => editId ? updateScreening(editId, form) : createScreening(form),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['screenings'] }); resetForm(); setError(''); },
    onError: (e: any) => setError(e.response?.data?.message ?? 'Error saving screening'),
  });

  const cancelMutation = useMutation({
    mutationFn: cancelScreening,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['screenings'] }),
  });

  const resetForm = () => { setForm(EMPTY); setEditId(null); setShowForm(false); };

  const startEdit = (s: Screening) => {
    setForm({ movieId: s.movie.id, hallId: s.hall.id, startTime: s.startTime.slice(0, 16), basePrice: s.basePrice });
    setEditId(s.id);
    setShowForm(true);
  };

  return (
    <div>
      <div className={styles.header}>
        <h1 className={styles.heading}>Screenings</h1>
        <button className={styles.btn} onClick={() => { resetForm(); setShowForm(true); }}>+ Add Screening</button>
      </div>

      {showForm && (
        <div className={styles.formCard}>
          <h2 className={styles.subheading}>{editId ? 'Edit Screening' : 'New Screening'}</h2>
          {error && <div className={styles.errorMsg}>{error}</div>}
          <div className={styles.grid2}>
            <div>
              <label className={styles.label}>Movie *</label>
              <select className={styles.input} value={form.movieId} onChange={e => setForm(p => ({ ...p, movieId: +e.target.value }))}>
                <option value={0}>Select movie</option>
                {movies?.map(m => <option key={m.id} value={m.id}>{m.title}</option>)}
              </select>
            </div>
            <div>
              <label className={styles.label}>Hall *</label>
              <select className={styles.input} value={form.hallId} onChange={e => setForm(p => ({ ...p, hallId: +e.target.value }))}>
                <option value={0}>Select hall</option>
                {halls?.map(h => <option key={h.id} value={h.id}>{h.name}</option>)}
              </select>
            </div>
            <div>
              <label className={styles.label}>Start Time *</label>
              <input className={styles.input} type="datetime-local" value={form.startTime} onChange={e => setForm(p => ({ ...p, startTime: e.target.value }))} />
            </div>
            <div>
              <label className={styles.label}>Base Price (€)</label>
              <input className={styles.input} type="number" step="0.01" value={form.basePrice} onChange={e => setForm(p => ({ ...p, basePrice: +e.target.value }))} />
            </div>
          </div>
          <div className={styles.actions}>
            <button className={styles.btn} onClick={() => saveMutation.mutate()} disabled={saveMutation.isPending || !form.movieId || !form.hallId || !form.startTime}>
              {saveMutation.isPending ? 'Saving…' : 'Save'}
            </button>
            <button className={styles.btnSecondary} onClick={resetForm}>Cancel</button>
          </div>
        </div>
      )}

      {isLoading ? <p>Loading…</p> : (
        <table className={styles.table}>
          <thead>
            <tr><th>Movie</th><th>Hall</th><th>Date & Time</th><th>Duration</th><th>Price</th><th>Status</th><th>Actions</th></tr>
          </thead>
          <tbody>
            {screenings?.map(s => (
              <tr key={s.id}>
                <td>{s.movie.title}</td>
                <td>{s.hall.name}</td>
                <td>{new Date(s.startTime).toLocaleString()}</td>
                <td>{s.movie.durationMinutes} min</td>
                <td>€{s.basePrice}</td>
                <td>
                  <span className={`${styles.badge} ${s.cancelled ? styles.badgeRed : styles.badgeGreen}`}>
                    {s.cancelled ? 'Cancelled' : 'Active'}
                  </span>
                </td>
                <td style={{ display: 'flex', gap: 6 }}>
                  {!s.cancelled && (
                    <>
                      <button className={styles.btnSmall} onClick={() => startEdit(s)}>Edit</button>
                      <button className={styles.btnDanger} onClick={() => { if (confirm('Cancel this screening? All tickets will be refunded.')) cancelMutation.mutate(s.id); }}>Cancel</button>
                    </>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
