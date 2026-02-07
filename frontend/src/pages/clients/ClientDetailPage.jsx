import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import clientsService from '../../services/clientsService';
import ordersService from '../../services/ordersService';
import ErrorMessage from '../../components/ErrorMessage';
import Table from '../../components/Table';
import { formatAmount, formatDate, formatStatus, getTierColor } from '../../utils/format';
import { ArrowLeft, User, DollarSign, ShoppingBag, Calendar, Trophy, ShoppingCart } from 'lucide-react';

const ClientDetailPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();

    const [client, setClient] = useState(null);
    const [orders, setOrders] = useState([]);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchData();
    }, [id]);

    const fetchData = async () => {
        try {
            setLoading(true);
            const [clientData, ordersData] = await Promise.all([
                clientsService.getById(id),
                ordersService.getByClientId(id)
            ]);
            setClient(clientData);
            setOrders(ordersData);
        } catch (err) {
            setError(err.message || 'Erreur lors de la récupération des données');
        } finally {
            setLoading(false);
        }
    };

    const orderColumns = [
        { header: 'N° Commande', accessor: 'id' },
        {
            header: 'Date',
            accessor: 'orderDate',
            render: (row) => formatDate(row.orderDate, true)
        },
        {
            header: 'Total TTC',
            accessor: 'totalTTC',
            render: (row) => formatAmount(row.totalTTC)
        },
        {
            header: 'Statut',
            accessor: 'status',
            render: (row) => <span className="badge badge-default">{formatStatus(row.status)}</span>
        }
    ];

    if (loading && !client) return <div className="loading-screen">Chargement...</div>;

    return (
        <div style={{ maxWidth: '1200px', margin: '0 auto' }}>
            <div className="page-header">
                <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                    <button className="btn-icon" onClick={() => navigate('/clients')}>
                        <ArrowLeft size={24} />
                    </button>
                    <h1 className="page-title">Profil Client</h1>
                </div>
            </div>

            <ErrorMessage message={error} />

            {client && (
                <div className="fade-in">
                    <div className="glass-card mb-4" style={{ display: 'flex', gap: '2rem', alignItems: 'center' }}>
                        <div
                            style={{
                                width: '80px', height: '80px', borderRadius: '50%',
                                background: getTierColor(client.tier),
                                display: 'flex', alignItems: 'center', justifyContent: 'center',
                                boxShadow: `0 0 20px ${getTierColor(client.tier)}40`
                            }}
                        >
                            <User size={40} color="#fff" />
                        </div>
                        <div>
                            <h2 style={{ fontSize: '2rem', margin: '0 0 0.5rem 0' }}>{client.name}</h2>
                            <div style={{ color: 'var(--text-secondary)', display: 'flex', gap: '1.5rem' }}>
                                <span>{client.email}</span>
                                {client.username && <span>Utilisateur: {client.username}</span>}
                            </div>
                        </div>
                    </div>

                    <div className="detail-grid">
                        <div className="stat-card">
                            <span className="stat-label flex-gap" style={{ alignItems: 'center' }}><Trophy size={16} /> Niveau Fidélité</span>
                            <span className="stat-value" style={{ color: getTierColor(client.tier) }}>{client.tier}</span>
                        </div>
                        <div className="stat-card">
                            <span className="stat-label flex-gap" style={{ alignItems: 'center' }}><ShoppingBag size={16} /> Commandes Totales</span>
                            <span className="stat-value">{client.totalOrders || 0}</span>
                        </div>
                        <div className="stat-card">
                            <span className="stat-label flex-gap" style={{ alignItems: 'center' }}><DollarSign size={16} /> Montant Dépensé</span>
                            <span className="stat-value" style={{ color: 'var(--success-color)' }}>{formatAmount(client.totalSpent)}</span>
                        </div>
                    </div>

                    <div className="detail-grid">
                        <div className="stat-card">
                            <span className="stat-label flex-gap" style={{ alignItems: 'center' }}><Calendar size={16} /> Première Commande</span>
                            <span className="stat-value">{formatDate(client.firstOrderDate)}</span>
                        </div>
                        <div className="stat-card">
                            <span className="stat-label flex-gap" style={{ alignItems: 'center' }}><Calendar size={16} /> Dernière Commande</span>
                            <span className="stat-value">{formatDate(client.lastOrderDate)}</span>
                        </div>
                    </div>

                    <div className="glass-card mt-4">
                        <h3 style={{ marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                            <ShoppingCart size={20} /> Historique des Commandes
                        </h3>
                        <Table
                            columns={orderColumns}
                            data={orders}
                            onRowClick={(row) => navigate(`/orders/${row.id}`)}
                        />
                    </div>
                </div>
            )}
        </div>
    );
};

export default ClientDetailPage;
