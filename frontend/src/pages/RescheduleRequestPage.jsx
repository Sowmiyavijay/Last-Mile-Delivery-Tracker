import { useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { orderApi } from '../api/orders';

function RescheduleRequestPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [requestedDate, setRequestedDate] = useState('');
  const [reason, setReason] = useState('');
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const submit = async event => {
    event.preventDefault();
    setSubmitting(true);
    setError('');
    try {
      await orderApi.requestReschedule(id, { requestedDate, reason });
      navigate('/orders/my');
    } catch (e) {
      setError(e.response?.data?.message || 'Unable to submit reschedule request.');
      setSubmitting(false);
    }
  };

  return (
    <div className="dashboard">
      <div className="dashboard-header">
        <h1>Request Reschedule</h1>
        <Link to="/orders/my" className="btn btn-secondary" style={{ width: 'auto' }}>Back</Link>
      </div>
      <div className="user-info">
        {error && <p className="error-message">{error}</p>}
        <form onSubmit={submit}>
          <div className="form-group">
            <label htmlFor="requested-date">Requested date</label>
            <input id="requested-date" type="date" value={requestedDate} onChange={event => setRequestedDate(event.target.value)} required />
          </div>
          <div className="form-group">
            <label htmlFor="reason">Reason</label>
            <textarea id="reason" value={reason} onChange={event => setReason(event.target.value)} required rows="4" style={{ width: '100%', padding: '0.6rem 0.75rem' }} />
          </div>
          <button type="submit" className="btn btn-primary" disabled={submitting}>{submitting ? 'Submitting...' : 'Submit Request'}</button>
        </form>
      </div>
    </div>
  );
}

export default RescheduleRequestPage;
