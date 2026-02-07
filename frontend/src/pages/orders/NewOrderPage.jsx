import React, { useState, useEffect, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import clientsService from '../../services/clientsService';
import productsService from '../../services/productsService';
import ordersService from '../../services/ordersService';
import ErrorMessage from '../../components/ErrorMessage';
import { formatAmount } from '../../utils/format';
import { ArrowLeft, Save, Trash2, Plus } from 'lucide-react';

const NewOrderPage = () => {
    const navigate = useNavigate();

    const [clients, setClients] = useState([]);
    const [products, setProducts] = useState([]);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);

    const [selectedClientId, setSelectedClientId] = useState('');
    const [promoCode, setPromoCode] = useState('');

    const [items, setItems] = useState([{ productId: '', quantity: 1 }]);

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            const [clientsData, productsData] = await Promise.all([
                clientsService.getAll(),
                productsService.getActive()
            ]);
            setClients(clientsData);
            setProducts(productsData);
        } catch (err) {
            setError(err.message || 'Impossible de charger les données');
        } finally {
            setLoading(false);
        }
    };

    const handleAddItem = () => {
        setItems([...items, { productId: '', quantity: 1 }]);
    };

    const handleRemoveItem = (index) => {
        const newItems = [...items];
        newItems.splice(index, 1);
        // Always keep at least one row, even if empty
        if (newItems.length === 0) {
            newItems.push({ productId: '', quantity: 1 });
        }
        setItems(newItems);
    };

    const handleItemChange = (index, field, value) => {
        const newItems = [...items];
        newItems[index][field] = value;
        setItems(newItems);
    };

    // Local calculation of subtotals (approximate, for UI only)
    // Real math happens on the server after submitting
    const calculatedTotalHT = useMemo(() => {
        return items.reduce((total, item) => {
            const p = products.find(prod => prod.id.toString() === item.productId);
            if (p && item.quantity > 0) {
                return total + (p.price * item.quantity);
            }
            return total;
        }, 0);
    }, [items, products]);

    const calculatedTotalTTC = calculatedTotalHT * 1.20;

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!selectedClientId) {
            setError('Veuillez sélectionner un client.');
            return;
        }

        const validItems = items
            .filter(item => item.productId && parseInt(item.quantity) > 0)
            .map(item => ({
                productId: parseInt(item.productId),
                quantity: parseInt(item.quantity)
            }));

        if (validItems.length === 0) {
            setError('Veuillez ajouter au moins un produit valide.');
            return;
        }

        try {
            setSubmitting(true);
            setError(null);

            const payload = {
                clientId: parseInt(selectedClientId),
                promoCode: promoCode || null,
                items: validItems
            };

            const newOrder = await ordersService.create(payload);
            navigate(`/orders/${newOrder.id}`);
        } catch (err) {
            setError(err.message || 'Erreur lors de la création de la commande');
        } finally {
            setSubmitting(false);
        }
    };

    if (loading) return <div className="loading-screen">Chargement...</div>;

    return (
        <div style={{ maxWidth: '1000px', margin: '0 auto' }}>
            <div className="page-header">
                <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                    <button className="btn-icon" onClick={() => navigate('/orders')}>
                        <ArrowLeft size={24} />
                    </button>
                    <h1 className="page-title">Nouvelle Commande</h1>
                </div>
            </div>

            <ErrorMessage message={error} />

            <form onSubmit={handleSubmit}>
                <div className="glass-card mb-4">
                    <h2 style={{ fontSize: '1.25rem', marginBottom: '1.5rem' }}>Informations de base</h2>
                    <div className="form-row">
                        <div className="form-group">
                            <label className="form-label" htmlFor="clientId">Client *</label>
                            <select
                                id="clientId"
                                className="form-select"
                                value={selectedClientId}
                                onChange={(e) => setSelectedClientId(e.target.value)}
                                required
                            >
                                <option value="">-- Sélectionner un client --</option>
                                {clients.map(c => (
                                    <option key={c.id} value={c.id}>{c.name} ({c.email})</option>
                                ))}
                            </select>
                        </div>
                        <div className="form-group">
                            <label className="form-label" htmlFor="promoCode">Code Promo (Optionnel)</label>
                            <input
                                type="text"
                                id="promoCode"
                                className="form-input"
                                value={promoCode}
                                onChange={(e) => setPromoCode(e.target.value)}
                                placeholder="PROMO-XXXX"
                                maxLength={20}
                            />
                        </div>
                    </div>
                </div>

                <div className="glass-card mb-4" style={{ overflow: 'visible' }}>
                    <h2 style={{ fontSize: '1.25rem', marginBottom: '1.5rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <span>Produits de la commande</span>
                        <button type="button" className="btn btn-secondary" onClick={handleAddItem} style={{ padding: '0.4rem 0.75rem', fontSize: '0.75rem' }}>
                            <Plus size={14} /> Ajouter un produit
                        </button>
                    </h2>

                    <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                        {items.map((item, index) => (
                            <div key={index} style={{ display: 'flex', gap: '1rem', alignItems: 'flex-start' }}>
                                <div className="form-group" style={{ flex: 1, marginBottom: 0 }}>
                                    <select
                                        className="form-select"
                                        value={item.productId}
                                        onChange={(e) => handleItemChange(index, 'productId', e.target.value)}
                                        required
                                    >
                                        <option value="">-- Sélectionner un produit --</option>
                                        {products.map(p => {
                                            const disabled = p.stock <= 0;
                                            return (
                                                <option key={p.id} value={p.id} disabled={disabled}>
                                                    {p.name} - {formatAmount(p.price)} {disabled ? '(Rupture)' : `(Stock: ${p.stock})`}
                                                </option>
                                            );
                                        })}
                                    </select>
                                </div>

                                <div className="form-group" style={{ width: '120px', marginBottom: 0 }}>
                                    <input
                                        type="number"
                                        className="form-input"
                                        value={item.quantity}
                                        onChange={(e) => handleItemChange(index, 'quantity', e.target.value)}
                                        min="1"
                                        required
                                    />
                                </div>

                                <div style={{ paddingTop: '0.5rem', width: '120px', textAlign: 'right', fontWeight: '500' }}>
                                    {item.productId ? (() => {
                                        const p = products.find(x => x.id.toString() === item.productId);
                                        return p ? formatAmount(p.price * item.quantity) : '-';
                                    })() : '-'}
                                </div>

                                <button
                                    type="button"
                                    className="btn-icon"
                                    onClick={() => handleRemoveItem(index)}
                                    style={{ color: 'var(--danger-color)', marginTop: '0.2rem' }}
                                >
                                    <Trash2 size={18} />
                                </button>
                            </div>
                        ))}
                    </div>
                </div>

                {/* Live Approximation Summary (Real values come from API after save) */}
                <div className="glass-card" style={{ maxWidth: '400px', marginLeft: 'auto' }}>
                    <h3 style={{ fontSize: '1rem', marginBottom: '1rem', borderBottom: '1px solid var(--border-color)', paddingBottom: '0.5rem' }}>Estimation *</h3>

                    <div className="summary-row">
                        <span>Sous-total HT</span>
                        <span>{formatAmount(calculatedTotalHT)}</span>
                    </div>
                    <div className="summary-row">
                        <span>TVA (20%)</span>
                        <span>{formatAmount(calculatedTotalHT * 0.20)}</span>
                    </div>
                    <div className="summary-row total">
                        <span>Total TTC (Avant remises)</span>
                        <span>{formatAmount(calculatedTotalTTC)}</span>
                    </div>

                    <p style={{ marginTop: '1rem', fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                        * Le montant final (incluant remises fidélité et codes promo) sera calculé par le serveur lors de la validation.
                    </p>
                </div>

                <div style={{ marginTop: '2rem', display: 'flex', justifyContent: 'flex-end', gap: '1rem' }}>
                    <button type="button" className="btn btn-secondary" onClick={() => navigate('/orders')}>
                        Annuler
                    </button>
                    <button type="submit" className="btn btn-primary" disabled={submitting}>
                        <Save size={18} />
                        {submitting ? 'Validation...' : 'Créer la commande'}
                    </button>
                </div>
            </form>
        </div>
    );
};

export default NewOrderPage;
