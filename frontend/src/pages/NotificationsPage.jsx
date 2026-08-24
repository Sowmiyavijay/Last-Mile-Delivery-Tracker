import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { notificationApi } from '../api/notifications';

function NotificationsPage() {
  const [notifications, setNotifications] = useState([]);
  const [error, setError] = useState('');

  const load = async () => {
    try {
      const { data } = await notificationApi.getAll();
      setNotifications(data);
    } catch {
      setError('Unable to load notifications.');
    }
  };

  useEffect(() => { load(); }, []);

  const markRead = async id => {
    await notificationApi.markRead(id);
    await load();
  };

  const markAllRead = async () => {
    await notificationApi.markAllRead();
    await load();
  };

  return (
    <div className="dashboard">
      <div className="dashboard-header">
        <h1>Notifications</h1>
        <Link to="/dashboard" className="btn btn-secondary" style={{ width: 'auto' }}>Back</Link>
      </div>
      {error && <p className="error-message">{error}</p>}
      <button type="button" className="btn btn-secondary" onClick={markAllRead} style={{ width: 'auto', marginBottom: '1rem' }}>Mark all as read</button>
      <div className="user-info">
        {notifications.length === 0 ? <p>No notifications.</p> : notifications.map(notification => (
          <div key={notification.id} className={`notification-row${notification.read ? '' : ' unread'}`}>
            <div>
              <strong>{notification.title}</strong>
              <p>{notification.message}</p>
              <small>{new Date(notification.createdAt).toLocaleString()}</small>
            </div>
            {!notification.read && <button type="button" className="btn btn-secondary" onClick={() => markRead(notification.id)} style={{ width: 'auto' }}>Mark read</button>}
          </div>
        ))}
      </div>
    </div>
  );
}

export default NotificationsPage;