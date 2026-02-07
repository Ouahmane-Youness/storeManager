import React, { createContext, useContext, useState, useEffect } from 'react';
import api from '../services/api';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        checkStatus();
    }, []);

    const checkStatus = async () => {
        try {
            const response = await api.get('/auth/status');
            if (response.data.authenticated) {
                setUser({
                    userId: response.data.userId,
                    username: response.data.usernNme || response.data.userName || 'Utilisateur',
                    role: response.data.userRole,
                });
            } else {
                setUser(null);
            }
        } catch (error) {
            setUser(null);
        } finally {
            setLoading(false);
        }
    };

    const login = async (username, password) => {
        const response = await api.post('/auth/login', { username, password });
        setUser({
            userId: response.data.userId,
            username: response.data.username,
            role: response.data.role,
        });
        return response.data;
    };

    const logout = async () => {
        await api.post('/auth/logout');
        setUser(null);
    };

    if (loading) {
        return <div className="loading-screen">Chargement...</div>;
    }

    return (
        <AuthContext.Provider value={{ user, login, logout, checkStatus }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};
