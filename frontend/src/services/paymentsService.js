import api from './api';

const paymentsService = {
    getByOrderId: async (orderId) => {
        const response = await api.get(`/payments/order/${orderId}`);
        return response.data;
    },

    create: async (data) => {
        const response = await api.post('/payments', data);
        return response.data;
    }
};

export default paymentsService;
