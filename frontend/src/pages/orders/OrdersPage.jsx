import React, { useState, useEffect, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import ordersService from '../../services/ordersService';
import Table from '../../components/Table';
import Pagination from '../../components/Pagination';
import ErrorMessage from '../../components/ErrorMessage';
import { formatAmount, formatDate, formatStatus } from '../../utils/format';
import { Plus, Eye } from 'lucide-react';

const OrdersPage = () => {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const [statusFilter, setStatusFilter] = useState('');
    const [clientFilter, setClientFilter] = useState('');
    const [page, setPage] = useState(1);
    const pageSize = 10;

    const navigate = useNavigate();

    useEffect(() => {
        fetchOrders();
    }, []);

    const fetchOrders = async () => {
        try {
            setLoading(true);
            const data = await ordersService.getAll();
            setOrders(data);
            setError(null);
        } catch (err) {
            setError(err.message || 'Erreur lors du chargement des commandes');
        } finally {
            setLoading(false);
        }
    };

    const filteredOrders = useMemo(() => {
        return orders.filter(o => {
            const matchStatus = statusFilter ? o.status === statusFilter : true;
            const matchClient = clientFilter ? o.clientName.toLowerCase().includes(clientFilter.toLowerCase()) : true;
            return matchStatus && matchClient;
        });
    }, [orders, statusFilter, clientFilter]);

    const paginatedOrders = useMemo(() => {
        const start = (page - 1) * pageSize;
        return filteredOrders.slice(start, start + pageSize);
    }, [filteredOrders, page]);

    const columns = [
        { header: 'N°', accessor: 'id', width: '60px' },
        {
            header: 'Client',
            accessor: 'clientName',
            render: (row) => <span style={{ fontWeight: 500 }}>{row.clientName}</span>
        },
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
            header: 'Reste à Payer',
            accessor: 'remainingAmount',
            render: (row) => (
                <span style={{
                    color: row.remainingAmount <= 0 ? 'var(--success-color)' : 'var(--warning-color)',
                    fontWeight: 600
                }}>
                    {formatAmount(row.remainingAmount)}
                </span>
            )
        },
        {
            header: 'Statut',
            accessor: 'status',
            render: (row) => (
                <span className={`badge ${row.status === 'CONFIRMED' ? 'badge-success' :
                        row.status === 'CANCELED' ? 'badge-danger' :
                            row.status === 'PENDING' ? 'badge-warning' : 'badge-default'
                    }`}>
                    {formatStatus(row.status)}
                </span>
            )
        },
        {
            header: 'Actions',
            accessor: 'actions',
            width: '80px',
            render: (row) => (
                <div className="flex-gap" style={{ justifyContent: 'flex-end' }}>
                    <button
                        className="btn-icon"
                        onClick={(e) => { e.stopPropagation(); navigate(`/orders/${row.id}`); }}
                        title="Détails"
                    >
                        <Eye size={16} />
                    </button>
                </div>
            )
        }
    ];

    if (loading && orders.length === 0) return <div className="loading-screen">Chargement...</div>;

    return (
        <>
            <div className="page-header">
                <h1 className="page-title">Gestion des Commandes</h1>
                <button className="btn btn-primary" onClick={() => navigate('/orders/new')}>
                    <Plus size={18} /> Nouvelle Commande
                </button>
            </div>

            <ErrorMessage message={error} />

            <div className="glass-card" style={{ padding: '2rem' }}>
                <div className="flex-gap mb-4" style={{ flexWrap: 'wrap' }}>
                    <input
                        type="text"
                        placeholder="Filtrer par client..."
                        className="form-input"
                        style={{ maxWidth: '300px' }}
                        value={clientFilter}
                        onChange={(e) => { setClientFilter(e.target.value); setPage(1); }}
                    />
                    <select
                        className="form-select"
                        style={{ maxWidth: '200px' }}
                        value={statusFilter}
                        onChange={(e) => { setStatusFilter(e.target.value); setPage(1); }}
                    >
                        <option value="">Tous les statuts</option>
                        <option value="PENDING">En attente</option>
                        <option value="CONFIRMED">Confirmée</option>
                        <option value="CANCELED">Annulée</option>
                    </select>

                    <div className="text-secondary" style={{ display: 'flex', alignItems: 'center', marginLeft: 'auto', fontSize: '0.875rem' }}>
                        {filteredOrders.length} commande(s)
                    </div>
                </div>

                <Table
                    columns={columns}
                    data={paginatedOrders}
                    onRowClick={(row) => navigate(`/orders/${row.id}`)}
                />

                <Pagination
                    total={filteredOrders.length}
                    page={page}
                    pageSize={pageSize}
                    onPageChange={setPage}
                />
            </div>
        </>
    );
};

export default OrdersPage;
