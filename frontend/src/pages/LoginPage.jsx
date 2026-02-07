import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../state/AuthContext';
import ErrorMessage from '../components/ErrorMessage';
import { LogIn } from 'lucide-react';

const LoginPage = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);
    const { login } = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);
        setLoading(true);

        try {
            await login(username, password);
            navigate('/products');
        } catch (err) {
            setError('Identifiants incorrects. Veuillez réessayer.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="login-container">
            <div className="login-card fade-in">
                <div className="login-header">
                    <h1>SmartShop</h1>
                    <p>Connectez-vous à votre espace</p>
                </div>

                <ErrorMessage message={error} />

                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label className="form-label" htmlFor="username">Nom d'utilisateur</label>
                        <input
                            id="username"
                            type="text"
                            className="form-input"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            placeholder="Saisissez votre nom d'utilisateur"
                            required
                            disabled={loading}
                        />
                    </div>

                    <div className="form-group mb-4">
                        <label className="form-label" htmlFor="password">Mot de passe</label>
                        <input
                            id="password"
                            type="password"
                            className="form-input"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="••••••••"
                            required
                            disabled={loading}
                        />
                    </div>

                    <button
                        type="submit"
                        className="btn btn-primary"
                        style={{ width: '100%', marginTop: '1rem' }}
                        disabled={loading}
                    >
                        {loading ? 'Connexion...' : (
                            <>
                                <LogIn size={18} />
                                <span>Se connecter</span>
                            </>
                        )}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default LoginPage;
