import { NavLink, useNavigate } from 'react-router-dom';
import { useAuthStore } from '../../store/authStore';
import styles from './Sidebar.module.css';

const links = [
  { to: '/dashboard', label: '📊 Dashboard' },
  { to: '/screenings', label: '🎬 Screenings' },
  { to: '/movies', label: '🎥 Movies' },
  { to: '/qr-validation', label: '📷 QR Validation' },
  { to: '/snacks', label: '🍿 Snacks' },
  { to: '/orders', label: '🧾 Orders' },
];

export default function Sidebar() {
  const { user, logout } = useAuthStore();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <aside className={styles.sidebar}>
      <div className={styles.logo}>PSInema Admin</div>
      <div className={styles.user}>{user?.firstName} {user?.lastName}</div>
      <nav className={styles.nav}>
        {links.map(l => (
          <NavLink
            key={l.to}
            to={l.to}
            className={({ isActive }) => `${styles.link} ${isActive ? styles.active : ''}`}
          >
            {l.label}
          </NavLink>
        ))}
      </nav>
      <button className={styles.logout} onClick={handleLogout}>Logout</button>
    </aside>
  );
}
