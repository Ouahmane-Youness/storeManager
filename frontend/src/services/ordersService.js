import api from './api';

const ordersService = {
    getAll: async () => {
        const response = await api.get('/orders');
        return response.data;
    },

    getById: async (id) => {
        const response = await api.get(`/orders/${id}`);
        return response.data;
    },

    getByClientId: async (clientId) => {
        const response = await api.get(`/orders/client/${clientId}`);
        return response.data;
    },

    getByStatus: async (status) => {
        const response = await api.get(`/orders/status/${status}`);
        return response.data;
    },

    create: async (data) => {
        const response = await api.post('/orders', data);
        return response.data;
    },

    confirm: async (id) => {
        const response = await api.patch(`/orders/${id}/confirm`);
        return response.data;
    },

    cancel: async (id) => {
        const response = await api.patch(`/orders/${id}/cancel`);
        return response.data;
    }
};

export default ordersService;
