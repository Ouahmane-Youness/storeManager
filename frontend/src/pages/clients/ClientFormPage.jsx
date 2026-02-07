import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import clientsService from '../../services/clientsService';
import ErrorMessage from '../../components/ErrorMessage';
import { ArrowLeft, Save } from 'lucide-react';

const ClientFormPage = () => {
    const { id } = useParams();
    const isEditMode = !!id;
    const navigate = useNavigate();

    const [formData, setFormData] = useState({
        name: '',
        email: '',
        userId: ''
    });
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);
    const [initialLoading, setInitialLoading] = useState(isEditMode);

    useEffect(() => {
        if (isEditMode) {
            loadClient();
        }
    }, [id]);

    const loadClient = async () => {
        try {
            const client = await clientsService.getById(id);
            setFormData({
                name: client.name,
                email: client.email,
                userId: '' // not needed for edit unless requested
            });
        } catch (err) {
            setError(err.message || 'Impossible de charger le client');
        } finally {
            setInitialLoading(false);
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);
        setLoading(true);

        try {
            if (isEditMode) {
                // Edit only requires name and email
                await clientsService.update(id, {
                    name: formData.name,
                    email: formData.email
                });
            } else {
                // Create requires userId as well
                await clientsService.create({
                    name: formData.name,
                    email: formData.email,
                    userId: parseInt(formData.userId, 10)
                });
            }

            navigate('/clients');
        } catch (err) {
            setError(err.message || 'Erreur lors de la sauvegarde');
        } finally {
            setLoading(false);
        }
    };

    if (initialLoading) return <div className="loading-screen">Chargement...</div>;

    return (
        <div style={{ maxWidth: '600px', margin: '0 auto' }}>
            <div className="page-header">
                <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                    <button className="btn-icon" onClick={() => navigate('/clients')}>
                        <ArrowLeft size={24} />
                    </button>
                    <h1 className="page-title">{isEditMode ? 'Modifier Client' : 'Nouveau Client'}</h1>
                </div>
            </div>

            <ErrorMessage message={error} />

            <div className="glass-card fade-in">
                <form onSubmit={handleSubmit}>

                    <div className="form-group">
                        <label className="form-label" htmlFor="name">Nom / Raison Sociale</label>
                        <input
                            type="text"
                            id="name"
                            name="name"
                            className="form-input"
                            value={formData.name}
                            onChange={handleChange}
                            placeholder="Ex: John Doe ou Entreprise X"
                            required
                            maxLength={100}
                        />
                    </div>

                    <div className="form-group">
                        <label className="form-label" htmlFor="email">Email</label>
                        <input
                            type="email"
                            id="email"
                            name="email"
                            className="form-input"
                            value={formData.email}
                            onChange={handleChange}
                            placeholder="Ex: contact@exemple.com"
                            required
                            maxLength={100}
                        />
                    </div>

                    {!isEditMode && (
                        <div className="form-group">
                            <label className="form-label" htmlFor="userId">ID Utilisateur associé (Backend ref)</label>
                            <input
                                type="number"
                                id="userId"
                                name="userId"
                                className="form-input"
                                value={formData.userId}
                                onChange={handleChange}
                                placeholder="Ex: 1"
                                required={!isEditMode}
                                min="1"
                            />
                            <small style={{ color: 'var(--text-muted)' }}>
                                Nécessaire pour la création du client.
                            </small>
                        </div>
                    )}

                    <div style={{ marginTop: '2rem', display: 'flex', justifyContent: 'flex-end', gap: '1rem' }}>
                        <button type="button" className="btn btn-secondary" onClick={() => navigate('/clients')}>
                            Annuler
                        </button>
                        <button type="submit" className="btn btn-primary" disabled={loading}>
                            <Save size={18} />
                            {loading ? 'Sauvegarde...' : 'Enregistrer'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default ClientFormPage;
