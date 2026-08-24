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
import CreateOrderPage from './pages/CreateOrderPage';
import MyOrdersPage from './pages/MyOrdersPage';
import AgentManagement from './pages/admin/AgentManagement';
import OrderAssignment from './pages/admin/OrderAssignment';
import OrderTrackingPage from './pages/OrderTrackingPage';
import AgentOrdersPage from './pages/AgentOrdersPage';
import RescheduleRequestPage from './pages/RescheduleRequestPage';
import RescheduleManagement from './pages/admin/RescheduleManagement';
import NotificationsPage from './pages/NotificationsPage';

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
      <Route path="/notifications" element={<ProtectedRoute><NotificationsPage /></ProtectedRoute>} />
      <Route path="/admin/zones" element={<ProtectedRoute roles={['ADMIN']}><ZoneManagement /></ProtectedRoute>} />
      <Route path="/admin/areas" element={<ProtectedRoute roles={['ADMIN']}><AreaManagement /></ProtectedRoute>} />
      <Route path="/admin/rate-cards" element={<ProtectedRoute roles={['ADMIN']}><RateCardManagement /></ProtectedRoute>} />
      <Route path="/admin/cod-surcharges" element={<ProtectedRoute roles={['ADMIN']}><CodSurchargeManagement /></ProtectedRoute>} />
      <Route path="/admin/agents" element={<ProtectedRoute roles={['ADMIN']}><AgentManagement /></ProtectedRoute>} />
      <Route path="/admin/assignments" element={<ProtectedRoute roles={['ADMIN']}><OrderAssignment /></ProtectedRoute>} />
      <Route path="/orders/create" element={<ProtectedRoute roles={['CUSTOMER']}><CreateOrderPage /></ProtectedRoute>} />
      <Route path="/orders/my" element={<ProtectedRoute roles={['CUSTOMER']}><MyOrdersPage /></ProtectedRoute>} />
      <Route path="/orders/:id/tracking" element={<ProtectedRoute roles={['CUSTOMER', 'DELIVERY_AGENT', 'ADMIN']}><OrderTrackingPage /></ProtectedRoute>} />
      <Route path="/orders/:id/reschedule" element={<ProtectedRoute roles={['CUSTOMER']}><RescheduleRequestPage /></ProtectedRoute>} />
      <Route path="/agent/orders" element={<ProtectedRoute roles={['DELIVERY_AGENT']}><AgentOrdersPage /></ProtectedRoute>} />
      <Route path="/admin/reschedule-requests" element={<ProtectedRoute roles={['ADMIN']}><RescheduleManagement /></ProtectedRoute>} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

export default App;
