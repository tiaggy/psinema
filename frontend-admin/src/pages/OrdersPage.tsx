import { useQuery } from '@tanstack/react-query';
import { getAllOrders } from '../api/orders';
import styles from './Page.module.css';

export default function OrdersPage() {
  const { data: orders, isLoading } = useQuery({ queryKey: ['orders'], queryFn: getAllOrders });

  return (
    <div>
      <div className={styles.header}>
        <h1 className={styles.heading}>All Orders</h1>
      </div>
      {isLoading ? <p>Loading…</p> : (
        <table className={styles.table}>
          <thead>
            <tr><th>#</th><th>Customer</th><th>Movie</th><th>Screening</th><th>Total</th><th>Status</th></tr>
          </thead>
          <tbody>
            {orders?.map(o => (
              <tr key={o.id}>
                <td>#{o.id}</td>
                <td>{o.user.firstName} {o.user.lastName}<br /><small>{o.user.email}</small></td>
                <td>{o.screening.movie.title}</td>
                <td>{new Date(o.screening.startTime).toLocaleString()}<br />{o.screening.hall.name}</td>
                <td>€{o.totalAmount}</td>
                <td>
                  <span className={`${styles.badge} ${
                    o.status === 'CONFIRMED' ? styles.badgeGreen
                    : o.status === 'CANCELLED' ? styles.badgeRed
                    : styles.badgeGrey
                  }`}>{o.status}</span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
