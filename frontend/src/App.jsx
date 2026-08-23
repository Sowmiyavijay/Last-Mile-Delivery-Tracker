import { Navigate, Route, Routes } from 'react-router-dom';
import ProtectedRoute from './components/ProtectedRoute';
import { useAuth } from './context/AuthContext';
import DashboardPage from './pages/DashboardPage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import ZoneManagement from './pages/admin/ZoneManagement';
import AreaManagement from './pages/admin/AreaManagement';
import RateCardManagement from './pages/admin/RateCardManagement';
import CodSurchargeManagement from './pages/admin/CodSurchargeManagement';

function App() {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return <div className="loading">Loading...</div>;
  }

  return (
    <Routes>
      <Route
        path="/"
        element={<Navigate to={isAuthenticated ? '/dashboard' : '/login'} replace />}
      />
      <Route path="/login" element={isAuthenticated ? <Navigate to="/dashboard" replace /> : <LoginPage />} />
      <Route path="/register" element={isAuthenticated ? <Navigate to="/dashboard" replace /> : <RegisterPage />} />
      <Route
        path="/dashboard"
        element={
          <ProtectedRoute>
            <DashboardPage />
          </ProtectedRoute>
        }
      />
      <Route path="/admin/zones" element={<ProtectedRoute><ZoneManagement /></ProtectedRoute>} />
      <Route path="/admin/areas" element={<ProtectedRoute><AreaManagement /></ProtectedRoute>} />
      <Route path="/admin/rate-cards" element={<ProtectedRoute><RateCardManagement /></ProtectedRoute>} />
      <Route path="/admin/cod-surcharges" element={<ProtectedRoute><CodSurchargeManagement /></ProtectedRoute>} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

export default App;
