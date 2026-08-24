import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { orderApi } from '../api/orders';

const nextStatuses = {
  PENDING: ['PICKED_UP'],
  PICKED_UP: ['IN_TRANSIT'],
  IN_TRANSIT: ['OUT_FOR_DELIVERY'],
  OUT_FOR_DELIVERY: ['DELIVERED', 'FAILED'],
  FAILED: ['PENDING'],
  DELIVERED: [],
};

function AgentOrdersPage() {
  const [orders, setOrders] = useState([]);
  const [error, setError] = useState('');

  const fetchOrders = () => orderApi.getAssignedOrders()
    .then(({ data }) => setOrders(data))
    .catch(() => setError('Unable to load assigned orders.'));

  useEffect(() => { fetchOrders(); }, []);

  const updateStatus = async (orderId, status) => {
    try {
      await orderApi.updateStatus(orderId, status);
      await fetchOrders();
    } catch (e) {
      setError(e.response?.data?.message || 'Unable to update order status.');
    }
  };

  return (
    <div className="dashboard">
      <div className="dashboard-header">
        <h1>Assigned Orders</h1>
        <Link to="/dashboard" className="btn btn-secondary" style={{ width: 'auto' }}>Back</Link>
      </div>
      {error && <p className="error-message">{error}</p>}
      {orders.length === 0 ? <p>No assigned orders.</p> : (
        <div className="user-info">
          {orders.map(order => (
            <div key={order.id} style={{ borderBottom: '1px solid #e2e8f0', padding: '1rem 0' }}>
              <strong>Order #{order.id}</strong><span className="route-label">{order.pickupPincode} to {order.dropPincode}</span><span className={`status-badge status-${order.status.toLowerCase()}`}>{order.status.replaceAll('_', ' ')}</span>
              <div style={{ marginTop: '0.5rem' }}>
                {(nextStatuses[order.status] || []).map(status => (
                  <button key={status} type="button" className="btn btn-primary" style={{ width: 'auto', marginRight: '0.5rem' }} onClick={() => updateStatus(order.id, status)}>{status}</button>
                ))}
                <Link to={`/orders/${order.id}/tracking`}>Track</Link>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default AgentOrdersPage;
