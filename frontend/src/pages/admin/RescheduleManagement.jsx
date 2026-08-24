import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { adminApi } from '../../api/admin';

function RescheduleManagement() {
  const [requests, setRequests] = useState([]);
  const [error, setError] = useState('');

  const fetchRequests = () => adminApi.getRescheduleRequests()
    .then(({ data }) => setRequests(data))
    .catch(() => setError('Unable to load reschedule requests.'));

  useEffect(() => { fetchRequests(); }, []);

  const updateRequest = async (id, action) => {
    try {
      await action(id);
      await fetchRequests();
    } catch (e) {
      setError(e.response?.data?.message || 'Unable to update request.');
    }
  };

  return (
    <div className="dashboard">
      <div className="dashboard-header">
        <h1>Reschedule Requests</h1>
        <Link to="/dashboard" className="btn btn-secondary" style={{ width: 'auto' }}>Back</Link>
      </div>
      {error && <p className="error-message">{error}</p>}
      {requests.length === 0 ? <p>No reschedule requests.</p> : (
        <div className="user-info" style={{ overflowX: 'auto' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
            <thead><tr style={{ borderBottom: '1px solid #ddd' }}><th>Order</th><th>Customer</th><th>Date</th><th>Reason</th><th>Status</th><th>Actions</th></tr></thead>
            <tbody>{requests.map(request => (
              <tr key={request.id} style={{ borderBottom: '1px solid #eee' }}>
                <td>#{request.orderId}</td><td>{request.requestedByName}</td><td>{request.requestedDate}</td><td>{request.reason}</td><td>{request.status}</td>
                <td>{request.status === 'REQUESTED' && <><button type="button" className="btn btn-primary" style={{ width: 'auto', marginRight: '0.5rem' }} onClick={() => updateRequest(request.id, adminApi.approveReschedule)}>Approve</button><button type="button" className="btn btn-secondary" style={{ width: 'auto' }} onClick={() => updateRequest(request.id, adminApi.rejectReschedule)}>Reject</button></>}</td>
              </tr>
            ))}</tbody>
          </table>
        </div>
      )}
    </div>
  );
}

export default RescheduleManagement;
