import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import ordersService from '../../services/ordersService';
import paymentsService from '../../services/paymentsService';
import ErrorMessage from '../../components/ErrorMessage';
import Table from '../../components/Table';
import { formatAmount, formatDate, formatStatus } from '../../utils/format';
import { ArrowLeft, CheckCircle, XCircle, CreditCard, DollarSign, Calendar, User } from 'lucide-react';

const OrderDetailPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();

    const [order, setOrder] = useState(null);
    const [payments, setPayments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Payment form state
    const [showPaymentForm, setShowPaymentForm] = useState(false);
    const [paymentData, setPaymentData] = useState({
        amount: '',
        paymentMethod: 'ESPECES',
        paymentDate: new Date().toISOString().split('T')[0],
        reference: '',
        bankName: '',
        dueDate: ''
    });
    const [submittingPayment, setSubmittingPayment] = useState(false);

    useEffect(() => {
        fetchData();
    }, [id]);

    const fetchData = async () => {
        try {
            setLoading(true);
            const [orderData, paymentsData] = await Promise.all([
                ordersService.getById(id),
                paymentsService.getByOrderId(id)
            ]);
            setOrder(orderData);
            setPayments(paymentsData);
        } catch (err) {
            setError(err.message || 'Erreur lors de la récupération de la commande');
        } finally {
            setLoading(false);
        }
    };

    const handleConfirm = async () => {
        if (!window.confirm('Confirmer cette commande ?')) return;
        try {
            await ordersService.confirm(id);
            fetchData(); // Refresh to get updated status
        } catch (err) {
            setError(err.message || 'Erreur lors de la confirmation');
        }
    };

    const handleCancel = async () => {
        if (!window.confirm('Annuler cette commande ? Les stocks seront restaurés.')) return;
        try {
            await ordersService.cancel(id);
            fetchData(); // Refresh
        } catch (err) {
            setError(err.message || 'Erreur lors de l’annulation');
        }
    };

    const submitPayment = async (e) => {
        e.preventDefault();
        try {
            setSubmittingPayment(true);
            setError(null);

            const payload = {
                orderId: parseInt(id),
                amount: parseFloat(paymentData.amount),
                paymentMethod: paymentData.paymentMethod,
                paymentDate: paymentData.paymentDate,
                ...(paymentData.paymentMethod !== 'ESPECES' && {
                    reference: paymentData.reference,
                    bankName: paymentData.bankName
                }),
                ...(paymentData.paymentMethod === 'CHEQUE' && {
                    dueDate: paymentData.dueDate
                })
            };

            await paymentsService.create(payload);
            setShowPaymentForm(false);

            // Reset form
            setPaymentData(prev => ({ ...prev, amount: '', reference: '', bankName: '', dueDate: '' }));

            // Refresh order and payments (to update remaining amount and payment list)
            fetchData();

        } catch (err) {
            setError(err.message || 'Erreur lors du paiement');
        } finally {
            setSubmittingPayment(false);
        }
    };

    const itemColumns = [
        { header: 'Produit', accessor: 'productName' },
        {
            header: 'Prix Unitaire HT',
            accessor: 'unitPrice',
            render: (row) => formatAmount(row.unitPrice)
        },
        { header: 'Quantité', accessor: 'quantity' },
        {
            header: 'Total HT',
            accessor: 'totalPrice',
            render: (row) => formatAmount(row.totalPrice)
        }
    ];

    const paymentColumns = [
        { header: 'N°', accessor: 'paymentNumber' },
        { header: 'Méthode', accessor: 'paymentMethod' },
        {
            header: 'Date',
            accessor: 'paymentDate',
            render: (row) => formatDate(row.paymentDate)
        },
        {
            header: 'Montant',
            accessor: 'amount',
            render: (row) => formatAmount(row.amount)
        },
        {
            header: 'Statut',
            accessor: 'status',
            render: (row) => (
                <span className={`badge ${row.status === 'ENCAISSE' ? 'badge-success' : row.status === 'REJETE' ? 'badge-danger' : 'badge-warning'}`}>
                    {formatStatus(row.status)}
                </span>
            )
        },
        {
            header: 'Détails',
            accessor: 'reference',
            render: (row) => row.reference ? `${row.reference} (${row.bankName})` : '-'
        }
    ];

    if (loading && !order) return <div className="loading-screen">Chargement...</div>;

    return (
        <div style={{ maxWidth: '1200px', margin: '0 auto' }}>
            <div className="page-header">
                <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                    <button className="btn-icon" onClick={() => navigate('/orders')}>
                        <ArrowLeft size={24} />
                    </button>
                    <div>
                        <h1 className="page-title" style={{ display: 'inline-block', marginRight: '1rem', marginBottom: 0 }}>
                            Commande #{order?.id}
                        </h1>
                        {order && (
                            <span className={`badge ${order.status === 'CONFIRMED' ? 'badge-success' :
                                order.status === 'CANCELED' ? 'badge-danger' :
                                    order.status === 'PENDING' ? 'badge-warning' : 'badge-default'
                                }`} style={{ verticalAlign: 'middle' }}>
                                {formatStatus(order.status)}
                            </span>
                        )}
                    </div>
                </div>

                {order && (
                    <div className="flex-gap">
                        {order.status === 'PENDING' && (
                            <>
                                <button className="btn btn-danger" onClick={handleCancel}>
                                    <XCircle size={18} /> Annuler
                                </button>
                                <button
                                    className="btn btn-accent"
                                    onClick={handleConfirm}
                                    disabled={order.remainingAmount > 0}
                                    title={order.remainingAmount > 0 ? "Le montant restant doit être de 0 pour confirmer" : ""}
                                >
                                    <CheckCircle size={18} /> Confirmer
                                </button>
                            </>
                        )}
                    </div>
                )}
            </div>

            <ErrorMessage message={error} />

            {order && (
                <div className="fade-in">

                    <div style={{ display: 'grid', gridTemplateColumns: 'minmax(0, 2fr) minmax(0, 1fr)', gap: '1.5rem', marginBottom: '2rem' }}>

                        {/* Left Column: Info & Items */}
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
                            <div className="glass-card">
                                <h3 style={{ marginBottom: '1rem', borderBottom: '1px solid var(--border-color)', paddingBottom: '0.5rem' }}>
                                    Informations
                                </h3>
                                <div className="detail-grid" style={{ gridTemplateColumns: '1fr 1fr' }}>
                                    <div>
                                        <span className="stat-label flex-gap" style={{ alignItems: 'center', marginBottom: '0.25rem' }}><User size={14} /> Client</span>
                                        <div style={{ fontWeight: 500 }}>{order.clientName}</div>
                                    </div>
                                    <div>
                                        <span className="stat-label flex-gap" style={{ alignItems: 'center', marginBottom: '0.25rem' }}><Calendar size={14} /> Date de commande</span>
                                        <div style={{ fontWeight: 500 }}>{formatDate(order.orderDate, true)}</div>
                                    </div>
                                </div>
                            </div>

                            <div className="glass-card">
                                <h3 style={{ marginBottom: '1rem', borderBottom: '1px solid var(--border-color)', paddingBottom: '0.5rem' }}>
                                    Articles ({order.items.length})
                                </h3>
                                <Table columns={itemColumns} data={order.items || []} />
                            </div>
                        </div>

                        {/* Right Column: Financial Summary */}
                        <div className="glass-card" style={{ alignSelf: 'start', position: 'sticky', top: '5rem' }}>
                            <h3 style={{ marginBottom: '1.5rem', borderBottom: '1px solid var(--border-color)', paddingBottom: '0.5rem' }}>
                                Récapitulatif
                            </h3>

                            <div className="summary-box">
                                <div className="summary-row">
                                    <span>Sous-total HT</span>
                                    <span>{formatAmount(order.subtotal)}</span>
                                </div>

                                {order.discountAmount > 0 && (
                                    <div className="summary-row discount">
                                        <span>Remise ({order.promoCode ? 'Fidélité + Promo : ' + order.promoCode : 'Fidélité'})</span>
                                        <span>- {formatAmount(order.discountAmount)}</span>
                                    </div>
                                )}

                                <div className="summary-row mt-4">
                                    <span>TVA (20%)</span>
                                    <span>{formatAmount(order.vatAmount)}</span>
                                </div>

                                <div className="summary-row total">
                                    <span>Total TTC</span>
                                    <span>{formatAmount(order.totalTTC)}</span>
                                </div>
                            </div>

                            <div style={{
                                marginTop: '1.5rem',
                                padding: '1.5rem',
                                borderRadius: 'var(--radius-lg)',
                                background: order.remainingAmount <= 0 ? 'rgba(16, 185, 129, 0.1)' : 'rgba(245, 158, 11, 0.1)',
                                border: `1px solid ${order.remainingAmount <= 0 ? 'rgba(16, 185, 129, 0.3)' : 'rgba(245, 158, 11, 0.3)'}`,
                                textAlign: 'center'
                            }}>
                                <span className="stat-label">Reste à payer</span>
                                <div style={{
                                    fontSize: '2rem',
                                    fontWeight: 700,
                                    color: order.remainingAmount <= 0 ? 'var(--success-color)' : 'var(--warning-color)',
                                    marginTop: '0.5rem'
                                }}>
                                    {formatAmount(order.remainingAmount)}
                                </div>
                            </div>
                        </div>

                    </div>

                    <div className="glass-card">
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem', borderBottom: '1px solid var(--border-color)', paddingBottom: '0.5rem' }}>
                            <h3 style={{ margin: 0, display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                                <CreditCard size={20} /> Paiements
                            </h3>

                            {order.status === 'PENDING' && order.remainingAmount > 0 && (
                                <button
                                    className="btn btn-secondary"
                                    style={{ padding: '0.4rem 0.75rem', fontSize: '0.75rem' }}
                                    onClick={() => setShowPaymentForm(!showPaymentForm)}
                                >
                                    <DollarSign size={14} /> Ajouter un paiement
                                </button>
                            )}
                        </div>

                        {showPaymentForm && (
                            <form onSubmit={submitPayment} className="summary-box mb-4 fade-in" style={{ backgroundColor: 'rgba(255,255,255,0.03)' }}>
                                <h4 style={{ marginBottom: '1rem' }}>Saisir un paiement</h4>
                                <div className="form-row">
                                    <div className="form-group">
                                        <label className="form-label">Montant (DH)</label>
                                        <input
                                            type="number" className="form-input" required min="0.01" step="0.01"
                                            value={paymentData.amount} onChange={e => setPaymentData({ ...paymentData, amount: e.target.value })}
                                            placeholder={`Max: ${order.remainingAmount}`}
                                        />
                                        {paymentData.paymentMethod === 'ESPECES' && Number(paymentData.amount) > 20000 && (
                                            <small style={{ color: 'var(--warning-color)', marginTop: '0.25rem', display: 'block' }}>
                                                Attention: Les paiements en espèces sont habituellement limités à 20 000 DH.
                                            </small>
                                        )}
                                    </div>
                                    <div className="form-group">
                                        <label className="form-label">Méthode de paiement</label>
                                        <select
                                            className="form-select"
                                            value={paymentData.paymentMethod}
                                            onChange={e => setPaymentData({ ...paymentData, paymentMethod: e.target.value })}
                                        >
                                            <option value="ESPECES">Espèces</option>
                                            <option value="CHEQUE">Chèque</option>
                                            <option value="VIREMENT">Virement</option>
                                        </select>
                                    </div>
                                </div>

                                <div className="form-row">
                                    <div className="form-group">
                                        <label className="form-label">Date du paiement</label>
                                        <input
                                            type="date" className="form-input" required
                                            value={paymentData.paymentDate} onChange={e => setPaymentData({ ...paymentData, paymentDate: e.target.value })}
                                        />
                                    </div>

                                    {paymentData.paymentMethod !== 'ESPECES' && (
                                        <>
                                            <div className="form-group">
                                                <label className="form-label">Référence ({paymentData.paymentMethod === 'CHEQUE' ? 'N° Chèque' : 'Opération'})</label>
                                                <input
                                                    type="text" className="form-input" required maxLength="100"
                                                    value={paymentData.reference} onChange={e => setPaymentData({ ...paymentData, reference: e.target.value })}
                                                />
                                            </div>
                                            <div className="form-group">
                                                <label className="form-label">Nom de la banque</label>
                                                <input
                                                    type="text" className="form-input" required maxLength="100"
                                                    value={paymentData.bankName} onChange={e => setPaymentData({ ...paymentData, bankName: e.target.value })}
                                                />
                                            </div>
                                        </>
                                    )}

                                    {paymentData.paymentMethod === 'CHEQUE' && (
                                        <div className="form-group">
                                            <label className="form-label">Date d'échéance</label>
                                            <input
                                                type="date" className="form-input" required
                                                value={paymentData.dueDate} onChange={e => setPaymentData({ ...paymentData, dueDate: e.target.value })}
                                            />
                                        </div>
                                    )}
                                </div>

                                <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '1rem', marginTop: '1rem' }}>
                                    <button type="button" className="btn btn-secondary" onClick={() => setShowPaymentForm(false)}>Annuler</button>
                                    <button type="submit" className="btn btn-primary" disabled={submittingPayment}>
                                        {submittingPayment ? 'Enregistrement...' : 'Enregistrer le paiement'}
                                    </button>
                                </div>
                            </form>
                        )}

                        <Table columns={paymentColumns} data={payments} />
                    </div>

                </div>
            )}
        </div>
    );
};

export default OrderDetailPage;
