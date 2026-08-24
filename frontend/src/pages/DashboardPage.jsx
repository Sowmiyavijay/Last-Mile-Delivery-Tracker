import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import NotificationBell from '../components/NotificationBell';

function DashboardPage() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="dashboard">
      <div className="dashboard-header">
        <h1>Dashboard</h1>
        <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
          <NotificationBell />
          <Link to="/notifications" className="btn btn-secondary" style={{ width: 'auto' }}>Inbox</Link>
          <button type="button" className="btn btn-secondary" onClick={handleLogout} style={{ width: 'auto' }}>Logout</button>
        </div>
      </div>

      <div className="user-info">
        <h2>Welcome, {user?.name}</h2>
        <dl>
          <dt>Name</dt>
          <dd>{user?.name}</dd>
          <dt>Email</dt>
          <dd>{user?.email}</dd>
          <dt>Role</dt>
          <dd>
            <span className="role-badge">{user?.role}</span>
          </dd>
        </dl>
      </div>

      <p style={{ marginTop: '2rem', color: '#64748b', fontSize: '0.9rem' }}>
        Phase 1: Foundation and Authentication. Delivery features will be added in future modules.
      </p>

      {user?.role === 'ADMIN' && (
        <div style={{ marginTop: '2rem' }}>
          <h3>Admin Management</h3>
          <ul style={{ display: 'flex', gap: '1rem', listStyle: 'none', padding: 0 }}>
            <li><Link to="/admin/zones" className="btn btn-primary">Zones</Link></li>
            <li><Link to="/admin/areas" className="btn btn-primary">Areas</Link></li>
            <li><Link to="/admin/rate-cards" className="btn btn-primary">Rate Cards</Link></li>
            <li><Link to="/admin/cod-surcharges" className="btn btn-primary">COD Surcharges</Link></li>
            <li><Link to="/admin/agents" className="btn btn-primary">Agents</Link></li>
            <li><Link to="/admin/assignments" className="btn btn-primary">Assignments</Link></li>
            <li><Link to="/admin/reschedule-requests" className="btn btn-primary">Reschedules</Link></li>
            <li><Link to="/notifications" className="btn btn-secondary">Notifications</Link></li>
          </ul>
        </div>
      )}

      {user?.role === 'CUSTOMER' && (
        <div style={{ marginTop: '2rem' }}>
          <h3>Customer Actions</h3>
          <ul style={{ display: 'flex', gap: '1rem', listStyle: 'none', padding: 0 }}>
            <li><Link to="/orders/create" className="btn btn-primary">Create Order</Link></li>
            <li><Link to="/orders/my" className="btn btn-primary">My Orders</Link></li>
            <li><Link to="/notifications" className="btn btn-secondary">Notifications</Link></li>
          </ul>
        </div>
      )}

      {user?.role === 'DELIVERY_AGENT' && (
        <div style={{ marginTop: '2rem' }}>
          <h3>Delivery Actions</h3>
          <Link to="/agent/orders" className="btn btn-primary" style={{ width: 'auto' }}>Assigned Orders</Link>
          <Link to="/notifications" className="btn btn-secondary" style={{ width: 'auto', marginLeft: '0.5rem' }}>Notifications</Link>
        </div>
      )}
    </div>
  );
}

export default DashboardPage;
