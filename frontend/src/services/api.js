import axios from 'axios';

const api = axios.create({
    baseURL: '/api',
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json',
    },
});

api.interceptors.response.use(
    (response) => response,
    (error) => {
        let message = 'Une erreur est survenue';

        if (error.response) {
            if (error.response.status === 401) {
                message = 'Session expirée, veuillez vous reconnecter.';
                // Optional: redirect to login or handle session expiration
            } else if (error.response.status === 403) {
                message = 'Accès refusé - Droits administrateur requis.';
            } else if (error.response.data && error.response.data.message) {
                message = error.response.data.message;
            } else if (error.response.status === 404) {
                message = 'Ressource introuvable.';
            } else if (error.response.status === 400 || error.response.status === 422) {
                message = 'Données invalides. Veuillez vérifier le formulaire.';
            }
        } else if (error.request) {
            message = 'Impossible de contacter le serveur.';
        }

        return Promise.reject(new Error(message));
    }
);

export default api;
