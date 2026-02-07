import api from './api';

const clientsService = {
    getAll: async () => {
        const response = await api.get('/clients');
        return response.data;
    },

    getById: async (id) => {
        const response = await api.get(`/clients/${id}`);
        return response.data;
    },

    getByEmail: async (email) => {
        const response = await api.get(`/clients/email/${encodeURIComponent(email)}`);
        return response.data;
    },

    create: async (data) => {
        const response = await api.post('/clients', data);
        return response.data;
    },

    update: async (id, data) => {
        const response = await api.put(`/clients/${id}`, data);
        return response.data;
    },

    remove: async (id) => {
        await api.delete(`/clients/${id}`);
    }
};

export default clientsService;
