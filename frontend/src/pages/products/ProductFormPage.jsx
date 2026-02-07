import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import productsService from '../../services/productsService';
import ErrorMessage from '../../components/ErrorMessage';
import { ArrowLeft, Save } from 'lucide-react';

const ProductFormPage = () => {
    const { id } = useParams();
    const isEditMode = !!id;
    const navigate = useNavigate();

    const [formData, setFormData] = useState({
        name: '',
        price: '',
        stock: ''
    });
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);
    const [initialLoading, setInitialLoading] = useState(isEditMode);

    useEffect(() => {
        if (isEditMode) {
            loadProduct();
        }
    }, [id]);

    const loadProduct = async () => {
        try {
            const product = await productsService.getById(id);
            setFormData({
                name: product.name,
                price: product.price.toString(),
                stock: product.stock.toString()
            });
        } catch (err) {
            setError(err.message || 'Impossible de charger le produit');
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
            const payload = {
                name: formData.name,
                price: parseFloat(formData.price),
                stock: parseInt(formData.stock, 10)
            };

            if (isEditMode) {
                await productsService.update(id, payload);
            } else {
                await productsService.create(payload);
            }

            navigate('/products');
        } catch (err) {
            setError(err.message || 'Erreur lors de la sauvegarde');
        } finally {
            setLoading(false);
        }
    };

    if (initialLoading) return <div className="loading-screen">Chargement...</div>;

    return (
        <div style={{ maxWidth: '800px', margin: '0 auto' }}>
            <div className="page-header">
                <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                    <button className="btn-icon" onClick={() => navigate('/products')}>
                        <ArrowLeft size={24} />
                    </button>
                    <h1 className="page-title">{isEditMode ? 'Modifier Produit' : 'Nouveau Produit'}</h1>
                </div>
            </div>

            <ErrorMessage message={error} />

            <div className="glass-card fade-in">
                <form onSubmit={handleSubmit}>

                    <div className="form-group">
                        <label className="form-label" htmlFor="name">Nom du produit</label>
                        <input
                            type="text"
                            id="name"
                            name="name"
                            className="form-input"
                            value={formData.name}
                            onChange={handleChange}
                            placeholder="Ex: iPhone 13 Pro"
                            required
                            maxLength={200}
                        />
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label className="form-label" htmlFor="price">Prix Unitaire HT (DH)</label>
                            <input
                                type="number"
                                id="price"
                                name="price"
                                className="form-input"
                                value={formData.price}
                                onChange={handleChange}
                                placeholder="0.00"
                                step="0.01"
                                min="0.01"
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label className="form-label" htmlFor="stock">Quantité en stock</label>
                            <input
                                type="number"
                                id="stock"
                                name="stock"
                                className="form-input"
                                value={formData.stock}
                                onChange={handleChange}
                                placeholder="0"
                                step="1"
                                min="0"
                                required
                            />
                        </div>
                    </div>

                    <div style={{ marginTop: '2rem', display: 'flex', justifyContent: 'flex-end', gap: '1rem' }}>
                        <button type="button" className="btn btn-secondary" onClick={() => navigate('/products')}>
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

export default ProductFormPage;
