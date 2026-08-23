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
};
