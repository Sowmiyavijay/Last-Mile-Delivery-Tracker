import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { orderApi } from '../api/orders';

function OrderTrackingPage() {
  const { id } = useParams();
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    orderApi.getTracking(id)
      .then(({ data }) => setHistory(data))
      .catch(() => setError('Unable to load tracking history.'))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return <div className="dashboard"><p>Loading tracking history...</p></div>;
  if (error) return <div className="dashboard"><p className="error-message">{error}</p><Link to="/orders/my">Back to orders</Link></div>;

  return (
    <div className="dashboard">
      <div className="dashboard-header">
        <h1>Order #{id} Tracking</h1>
        <Link to="/orders/my" className="btn btn-secondary" style={{ width: 'auto' }}>Back</Link>
      </div>
      <div className="user-info">
        {history.length === 0 ? <p>No tracking events recorded.</p> : (
          <ol className="timeline">
            {history.map((event, index) => (
              <li key={`${event.timestamp}-${index}`} className="timeline-item">
                <span className={`status-badge status-${event.status.toLowerCase()}`}>{event.status.replaceAll('_', ' ')}</span>
                <div>{new Date(event.timestamp).toLocaleString()}</div>
                <small>{event.actorName} ({event.actorRole})</small>
              </li>
            ))}
          </ol>
        )}
      </div>
    </div>
  );
}

export default OrderTrackingPage;
