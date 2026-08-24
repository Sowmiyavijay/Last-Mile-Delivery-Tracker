import apiClient from './client';

export const orderApi = {
    calculatePrice: (data) => apiClient.post('/api/orders/price', data),
    createOrder: (data) => apiClient.post('/api/orders', data),
    getMyOrders: () => apiClient.get('/api/orders/my'),
    getOrderById: (id) => apiClient.get(`/api/orders/${id}`),
    getTracking: (id) => apiClient.get(`/api/orders/${id}/tracking`),
    updateStatus: (id, status) => apiClient.put(`/api/orders/${id}/status`, { status }),
    getAssignedOrders: () => apiClient.get('/api/agent/orders'),
    requestReschedule: (id, data) => apiClient.post(`/api/orders/${id}/reschedule`, data),
    getRescheduleRequests: (id) => apiClient.get(`/api/orders/${id}/reschedule`),
};
