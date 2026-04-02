import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { getSnacks, createSnack, updateSnack, deleteSnack, getPendingSnackOrders, updateSnackOrderStatus } from '../api/snacks';
import type { SnackItem } from '../api/snacks';
import styles from './Page.module.css';

const EMPTY = { name: '', description: '', price: 0, stockQuantity: 0, imageUrl: '', available: true };

export default function SnacksPage() {
  const qc = useQueryClient();
  const { data: snacks } = useQuery({ queryKey: ['snacks'], queryFn: getSnacks });
  const { data: pending } = useQuery({ queryKey: ['snack-orders-pending'], queryFn: getPendingSnackOrders, refetchInterval: 15000 });
  const [form, setForm] = useState(EMPTY);
  const [editId, setEditId] = useState<number | null>(null);
  const [showForm, setShowForm] = useState(false);

  const saveMutation = useMutation({
    mutationFn: () => editId ? updateSnack(editId, form) : createSnack(form),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['snacks'] }); resetForm(); },
  });

  const deleteMutation = useMutation({
    mutationFn: deleteSnack,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['snacks'] }),
  });

  const statusMutation = useMutation({
    mutationFn: ({ id, status }: { id: number; status: string }) => updateSnackOrderStatus(id, status),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['snack-orders-pending'] }),
  });

  const resetForm = () => { setForm(EMPTY); setEditId(null); setShowForm(false); };

  const startEdit = (s: SnackItem) => {
    setForm({ name: s.name, description: s.description, price: s.price, stockQuantity: s.stockQuantity, imageUrl: s.imageUrl, available: s.available });
    setEditId(s.id);
    setShowForm(true);
  };

  return (
    <div>
      <div className={styles.header}>
        <h1 className={styles.heading}>Snacks & Orders</h1>
        <button className={styles.btn} onClick={() => { resetForm(); setShowForm(true); }}>+ Add Snack</button>
      </div>

      {showForm && (
        <div className={styles.formCard}>
          <h2 className={styles.subheading}>{editId ? 'Edit Snack' : 'New Snack'}</h2>
          <div className={styles.grid2}>
            <div>
              <label className={styles.label}>Name *</label>
              <input className={styles.input} value={form.name} onChange={e => setForm(p => ({ ...p, name: e.target.value }))} />
            </div>
            <div>
              <label className={styles.label}>Price (€)</label>
              <input className={styles.input} type="number" step="0.01" value={form.price} onChange={e => setForm(p => ({ ...p, price: +e.target.value }))} />
            </div>
            <div>
              <label className={styles.label}>Stock Quantity</label>
              <input className={styles.input} type="number" value={form.stockQuantity} onChange={e => setForm(p => ({ ...p, stockQuantity: +e.target.value }))} />
            </div>
            <div>
              <label className={styles.label}>Image URL</label>
              <input className={styles.input} value={form.imageUrl} onChange={e => setForm(p => ({ ...p, imageUrl: e.target.value }))} />
            </div>
          </div>
          <div>
            <label className={styles.label}>Description</label>
            <textarea className={styles.textarea} value={form.description} onChange={e => setForm(p => ({ ...p, description: e.target.value }))} rows={2} />
          </div>
          <div className={styles.actions}>
            <button className={styles.btn} onClick={() => saveMutation.mutate()} disabled={saveMutation.isPending || !form.name}>
              {saveMutation.isPending ? 'Saving…' : 'Save'}
            </button>
            <button className={styles.btnSecondary} onClick={resetForm}>Cancel</button>
          </div>
        </div>
      )}

      <h2 className={styles.subheading} style={{ marginBottom: 12 }}>Snack Menu</h2>
      <table className={styles.table} style={{ marginBottom: 32 }}>
        <thead><tr><th>Name</th><th>Price</th><th>Stock</th><th>Available</th><th>Actions</th></tr></thead>
        <tbody>
          {snacks?.map(s => (
            <tr key={s.id}>
              <td>{s.name}</td>
              <td>€{s.price}</td>
              <td>{s.stockQuantity}</td>
              <td><span className={`${styles.badge} ${s.available ? styles.badgeGreen : styles.badgeRed}`}>{s.available ? 'Yes' : 'No'}</span></td>
              <td style={{ display: 'flex', gap: 6 }}>
                <button className={styles.btnSmall} onClick={() => startEdit(s)}>Edit</button>
                <button className={styles.btnDanger} onClick={() => deleteMutation.mutate(s.id)}>Disable</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <h2 className={styles.subheading} style={{ marginBottom: 12 }}>Pending Snack Orders</h2>
      {!pending?.length ? <p className={styles.empty}>No pending orders.</p> : (
        <table className={styles.table}>
          <thead><tr><th>Order #</th><th>Cinema Order</th><th>Items</th><th>Total</th><th>Status</th><th>Actions</th></tr></thead>
          <tbody>
            {pending.map(o => (
              <tr key={o.id}>
                <td>#{o.id}</td>
                <td>Order #{o.order.id}</td>
                <td>{o.items.map(i => `${i.snackItem.name} ×${i.quantity}`).join(', ')}</td>
                <td>€{o.totalAmount}</td>
                <td><span className={`${styles.badge} ${styles.badgeBlue}`}>{o.status}</span></td>
                <td style={{ display: 'flex', gap: 6 }}>
                  <button className={styles.btnSmall} onClick={() => statusMutation.mutate({ id: o.id, status: 'PREPARING' })}>Preparing</button>
                  <button className={styles.btnSmall} onClick={() => statusMutation.mutate({ id: o.id, status: 'READY' })}>Ready</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
