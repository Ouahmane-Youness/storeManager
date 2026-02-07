import React from 'react';
import { Outlet, Navigate } from 'react-router-dom';
import { useAuth } from '../state/AuthContext';
import Navbar from './Navbar';

const Layout = () => {
    const { user } = useAuth();

    if (!user) {
        return <Navigate to="/login" replace />;
    }

    return (
        <div className="app-layout">
            <Navbar />
            <main className="main-content">
                <div className="page-container fade-in">
                    <Outlet />
                </div>
            </main>
        </div>
    );
};

export default Layout;
