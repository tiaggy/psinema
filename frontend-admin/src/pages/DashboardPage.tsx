import { useQuery } from '@tanstack/react-query';
import { getAllOrders } from '../api/orders';
import { getScreenings } from '../api/screenings';
import { getMovies } from '../api/movies';
import { useAuthStore } from '../store/authStore';
import styles from './DashboardPage.module.css';

export default function DashboardPage() {
  const { user } = useAuthStore();
  const { data: orders } = useQuery({ queryKey: ['orders'], queryFn: getAllOrders });
  const { data: screenings } = useQuery({ queryKey: ['screenings'], queryFn: () => getScreenings() });
  const { data: movies } = useQuery({ queryKey: ['movies'], queryFn: () => getMovies() });

  const today = new Date().toDateString();
  const todayScreenings = screenings?.filter(s => new Date(s.startTime).toDateString() === today && !s.cancelled) ?? [];
  const confirmedOrders = orders?.filter(o => o.status === 'CONFIRMED') ?? [];
  const revenue = confirmedOrders.reduce((sum, o) => sum + o.totalAmount, 0);

  return (
    <div>
      <h1 className={styles.heading}>Welcome back, {user?.firstName}!</h1>
      <div className={styles.cards}>
        <StatCard label="Active Movies" value={movies?.length ?? 0} color="#e94560" />
        <StatCard label="Today's Screenings" value={todayScreenings.length} color="#3498db" />
        <StatCard label="Total Orders" value={confirmedOrders.length} color="#2ecc71" />
        <StatCard label="Total Revenue" value={`€${revenue.toFixed(2)}`} color="#f39c12" />
      </div>
      <h2 className={styles.sectionTitle}>Upcoming Screenings Today</h2>
      {todayScreenings.length === 0 ? (
        <p className={styles.empty}>No screenings scheduled today.</p>
      ) : (
        <table className={styles.table}>
          <thead>
            <tr><th>Movie</th><th>Hall</th><th>Time</th></tr>
          </thead>
          <tbody>
            {todayScreenings.map(s => (
              <tr key={s.id}>
                <td>{s.movie.title}</td>
                <td>{s.hall.name}</td>
                <td>{new Date(s.startTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

function StatCard({ label, value, color }: { label: string; value: number | string; color: string }) {
  return (
    <div className={styles.card} style={{ borderTop: `4px solid ${color}` }}>
      <div className={styles.cardValue} style={{ color }}>{value}</div>
      <div className={styles.cardLabel}>{label}</div>
    </div>
  );
}
