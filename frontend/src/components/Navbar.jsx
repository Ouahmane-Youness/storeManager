import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../state/AuthContext';
import { LogOut, Package, Users, ShoppingCart, Activity } from 'lucide-react';

const Navbar = () => {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = async () => {
        await logout();
        navigate('/login');
    };

    return (
        <nav className="navbar">
            <div className="navbar-container">
                <div className="navbar-brand">
                    <Activity className="brand-icon" />
                    <span>SmartShop</span>
                </div>

                <div className="navbar-links">
                    <NavLink to="/products" className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}>
                        <Package size={18} />
                        <span>Produits</span>
                    </NavLink>
                    <NavLink to="/clients" className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}>
                        <Users size={18} />
                        <span>Clients</span>
                    </NavLink>
                    <NavLink to="/orders" className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}>
                        <ShoppingCart size={18} />
                        <span>Commandes</span>
                    </NavLink>
                </div>

                <div className="navbar-actions">
                    <div className="user-info">
                        <span className="user-name">{user?.username}</span>
                        <span className="user-role">{user?.role}</span>
                    </div>
                    <button onClick={handleLogout} className="btn-logout" aria-label="Déconnexion">
                        <LogOut size={18} />
                        <span>Déconnexion</span>
                    </button>
                </div>
            </div>
        </nav>
    );
};

export default Navbar;
