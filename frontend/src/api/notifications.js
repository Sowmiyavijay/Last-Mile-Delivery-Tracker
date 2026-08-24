import apiClient from './client';

export const notificationApi = {
  getAll: () => apiClient.get('/api/notifications'),
  getUnreadCount: () => apiClient.get('/api/notifications/unread-count'),
  markRead: id => apiClient.put(`/api/notifications/${id}/read`),
  markAllRead: () => apiClient.put('/api/notifications/read-all'),
};