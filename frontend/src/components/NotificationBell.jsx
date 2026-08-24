import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { notificationApi } from '../api/notifications';

function NotificationBell() {
  const [count, setCount] = useState(0);
  const [notifications, setNotifications] = useState([]);
  const [open, setOpen] = useState(false);

  const refresh = async () => {
    try {
      const [{ data: unreadCount }, { data }] = await Promise.all([
        notificationApi.getUnreadCount(),
        notificationApi.getAll(),
      ]);
      setCount(unreadCount);
      setNotifications(data.slice(0, 5));
    } catch {
    }
  };

  useEffect(() => {
    refresh();
    const interval = window.setInterval(refresh, 30000);
    return () => window.clearInterval(interval);
  }, []);

  const markRead = async notification => {
    if (!notification.read) {
      await notificationApi.markRead(notification.id);
      await refresh();
    }
  };

  return (
    <div style={{ position: 'relative' }}>
      <button type="button" className="btn btn-secondary" aria-label="Notifications" title="Notifications" onClick={() => setOpen(!open)} style={{ width: 'auto' }}>
        &#128276; {count > 0 && `(${count})`}
      </button>
      {open && (
        <div className="notification-menu">
          {notifications.length === 0 ? <p>No notifications.</p> : notifications.map(notification => (
            <button type="button" key={notification.id} className={`notification-item${notification.read ? '' : ' unread'}`} onClick={() => markRead(notification)}>
              <strong>{notification.title}</strong>
              <span>{notification.message}</span>
              <small>{new Date(notification.createdAt).toLocaleString()}</small>
            </button>
          ))}
          <Link to="/notifications" onClick={() => setOpen(false)}>View all notifications</Link>
        </div>
      )}
    </div>
  );
}

export default NotificationBell;