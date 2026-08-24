import apiClient from './client';

export const adminApi = {
    // Zones
    getZones: () => apiClient.get('/api/admin/zones'),
    createZone: (data) => apiClient.post('/api/admin/zones', data),
    updateZone: (id, data) => apiClient.put(`/api/admin/zones/${id}`, data),
    deleteZone: (id) => apiClient.delete(`/api/admin/zones/${id}`),

    // Areas
    getAreas: () => apiClient.get('/api/admin/areas'),
    createArea: (data) => apiClient.post('/api/admin/areas', data),
    updateArea: (id, data) => apiClient.put(`/api/admin/areas/${id}`, data),
    deleteArea: (id) => apiClient.delete(`/api/admin/areas/${id}`),

    // Rate Cards
    getRateCards: () => apiClient.get('/api/admin/rate-cards'),
    createRateCard: (data) => apiClient.post('/api/admin/rate-cards', data),
    updateRateCard: (id, data) => apiClient.put(`/api/admin/rate-cards/${id}`, data),
    deleteRateCard: (id) => apiClient.delete(`/api/admin/rate-cards/${id}`),

    // COD Surcharges
    getCodSurcharges: () => apiClient.get('/api/admin/cod-surcharges'),
    updateCodSurcharge: (orderType, data) => apiClient.put(`/api/admin/cod-surcharges/${orderType}`, data),

    // Delivery Agents
    getAgents: () => apiClient.get('/api/admin/agents'),
    createAgent: (data) => apiClient.post('/api/admin/agents', data),
    updateAgent: (id, data) => apiClient.put(`/api/admin/agents/${id}`, data),
    updateAvailability: (id, available) => apiClient.patch(`/api/admin/agents/${id}/availability?available=${available}`),

    // Order Assignment
    getOrder: (orderId) => apiClient.get(`/api/admin/orders/${orderId}`),
    manualAssign: (orderId, agentId) => apiClient.put(`/api/admin/orders/${orderId}/assign/${agentId}`),
    autoAssign: (orderId) => apiClient.post(`/api/admin/orders/${orderId}/auto-assign`),

    // Reschedule requests
    getRescheduleRequests: () => apiClient.get('/api/admin/reschedule-requests'),
    approveReschedule: (id) => apiClient.put(`/api/admin/reschedule-requests/${id}/approve`),
    rejectReschedule: (id) => apiClient.put(`/api/admin/reschedule-requests/${id}/reject`),
};
