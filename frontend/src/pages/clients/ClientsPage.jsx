import React, { useState, useEffect, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import clientsService from '../../services/clientsService';
import Table from '../../components/Table';
import Pagination from '../../components/Pagination';
import SearchBar from '../../components/SearchBar';
import ErrorMessage from '../../components/ErrorMessage';
import { formatAmount, getTierColor } from '../../utils/format';
import { Plus, Eye, Edit } from 'lucide-react';

const ClientsPage = () => {
    const [clients, setClients] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const [searchTerm, setSearchTerm] = useState('');
    const [page, setPage] = useState(1);
    const pageSize = 10;

    const navigate = useNavigate();

    useEffect(() => {
        fetchClients();
    }, []);

    const fetchClients = async () => {
        try {
            setLoading(true);
            const data = await clientsService.getAll();
            setClients(data);
            setError(null);
        } catch (err) {
            setError(err.message || 'Erreur lors du chargement des clients');
        } finally {
            setLoading(false);
        }
    };

    const filteredClients = useMemo(() => {
        if (!searchTerm) return clients;
        const lowerSearch = searchTerm.toLowerCase();
        return clients.filter(c =>
            c.name.toLowerCase().includes(lowerSearch) ||
            c.email.toLowerCase().includes(lowerSearch)
        );
    }, [clients, searchTerm]);

    const paginatedClients = useMemo(() => {
        const start = (page - 1) * pageSize;
        return filteredClients.slice(start, start + pageSize);
    }, [filteredClients, page]);

    const handleSearch = (term) => {
        setSearchTerm(term);
        setPage(1);
    };

    const columns = [
        { header: 'ID', accessor: 'id', width: '60px' },
        {
            header: 'Client',
            accessor: 'name',
            render: (row) => <span style={{ fontWeight: 600 }}>{row.name}</span>
        },
        { header: 'Email', accessor: 'email' },
        {
            header: 'Fidélité',
            accessor: 'tier',
            render: (row) => (
                <span
                    style={{
                        color: getTierColor(row.tier),
                        fontWeight: 700,
                        textShadow: '0 0 10px rgba(0,0,0,0.5)'
                    }}
                >
                    {row.tier}
                </span>
            )
        },
        {
            header: 'Commandes',
            accessor: 'totalOrders',
            render: (row) => <span style={{ textAlign: 'center', display: 'block' }}>{row.totalOrders}</span>
        },
        {
            header: 'Total Dépensé',
            accessor: 'totalSpent',
            render: (row) => formatAmount(row.totalSpent)
        },
        {
            header: 'Actions',
            accessor: 'actions',
            width: '100px',
            render: (row) => (
                <div className="flex-gap" style={{ justifyContent: 'flex-end' }}>
                    <button
                        className="btn-icon"
                        onClick={(e) => { e.stopPropagation(); navigate(`/clients/${row.id}`); }}
                        title="Détails"
                    >
                        <Eye size={16} />
                    </button>
                    <button
                        className="btn-icon"
                        onClick={(e) => { e.stopPropagation(); navigate(`/clients/${row.id}/edit`); }}
                        title="Modifier"
                    >
                        <Edit size={16} />
                    </button>
                </div>
            )
        }
    ];

    if (loading && clients.length === 0) return <div className="loading-screen">Chargement...</div>;

    return (
        <>
            <div className="page-header">
                <h1 className="page-title">Gestion des Clients</h1>
                <button className="btn btn-primary" onClick={() => navigate('/clients/new')}>
                    <Plus size={18} /> Nouveau Client
                </button>
            </div>

            <ErrorMessage message={error} />

            <div className="glass-card" style={{ padding: '2rem' }}>
                <div className="flex-between mb-4">
                    <SearchBar onSearch={handleSearch} placeholder="Rechercher par nom ou email..." />
                    <div className="text-secondary" style={{ fontSize: '0.875rem' }}>
                        {filteredClients.length} client(s) trouvé(s)
                    </div>
                </div>

                <Table
                    columns={columns}
                    data={paginatedClients}
                    onRowClick={(row) => navigate(`/clients/${row.id}`)}
                />

                <Pagination
                    total={filteredClients.length}
                    page={page}
                    pageSize={pageSize}
                    onPageChange={setPage}
                />
            </div>
        </>
    );
};

export default ClientsPage;
