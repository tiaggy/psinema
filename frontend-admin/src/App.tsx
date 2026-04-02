import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import ProtectedRoute from './components/common/ProtectedRoute';
import Layout from './components/common/Layout';
import LoginPage from './pages/LoginPage';
import DashboardPage from './pages/DashboardPage';
import MoviesPage from './pages/MoviesPage';
import ScreeningsPage from './pages/ScreeningsPage';
import QrValidationPage from './pages/QrValidationPage';
import SnacksPage from './pages/SnacksPage';
import OrdersPage from './pages/OrdersPage';

const qc = new QueryClient();

export default function App() {
  return (
    <QueryClientProvider client={qc}>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/dashboard" element={<ProtectedRoute><Layout><DashboardPage /></Layout></ProtectedRoute>} />
          <Route path="/movies" element={<ProtectedRoute><Layout><MoviesPage /></Layout></ProtectedRoute>} />
          <Route path="/screenings" element={<ProtectedRoute><Layout><ScreeningsPage /></Layout></ProtectedRoute>} />
          <Route path="/qr-validation" element={<ProtectedRoute><Layout><QrValidationPage /></Layout></ProtectedRoute>} />
          <Route path="/snacks" element={<ProtectedRoute><Layout><SnacksPage /></Layout></ProtectedRoute>} />
          <Route path="/orders" element={<ProtectedRoute><Layout><OrdersPage /></Layout></ProtectedRoute>} />
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </BrowserRouter>
    </QueryClientProvider>
  );
}
