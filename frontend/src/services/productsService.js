import api from './api';

const productsService = {
    getAll: async () => {
        const response = await api.get('/products');
        return response.data;
    },

    getActive: async () => {
        const response = await api.get('/products/active');
        return response.data;
    },

    search: async (keyword) => {
        const response = await api.get(`/products/search?keyword=${encodeURIComponent(keyword)}`);
        return response.data;
    },

    getById: async (id) => {
        const response = await api.get(`/products/${id}`);
        return response.data;
    },

    create: async (data) => {
        const response = await api.post('/products', data);
        return response.data;
    },

    update: async (id, data) => {
        const response = await api.put(`/products/${id}`, data);
        return response.data;
    },

    remove: async (id) => {
        await api.delete(`/products/${id}`);
    }
};

export default productsService;
