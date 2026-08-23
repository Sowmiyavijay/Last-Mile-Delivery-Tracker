import apiClient from './client';

export const orderApi = {
    calculatePrice: (data) => apiClient.post('/api/orders/price', data),
    createOrder: (data) => apiClient.post('/api/orders', data),
    getMyOrders: () => apiClient.get('/api/orders/my'),
    getOrderById: (id) => apiClient.get(`/api/orders/${id}`),
};
