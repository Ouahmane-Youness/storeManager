import React, { useState, useEffect, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import productsService from '../../services/productsService';
import Table from '../../components/Table';
import Pagination from '../../components/Pagination';
import SearchBar from '../../components/SearchBar';
import ErrorMessage from '../../components/ErrorMessage';
import { formatAmount } from '../../utils/format';
import { Plus, Edit, Trash2 } from 'lucide-react';

const ProductsPage = () => {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const [searchTerm, setSearchTerm] = useState('');
    const [page, setPage] = useState(1);
    const pageSize = 10;

    const navigate = useNavigate();

    useEffect(() => {
        fetchProducts();
    }, []);

    const fetchProducts = async () => {
        try {
            setLoading(true);
            const data = await productsService.getActive();
            setProducts(data);
            setError(null);
        } catch (err) {
            setError(err.message || 'Erreur lors du chargement des produits');
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (e, id) => {
        e.stopPropagation();
        if (window.confirm('Voulez-vous vraiment supprimer ce produit ?')) {
            try {
                await productsService.remove(id);
                fetchProducts();
            } catch (err) {
                setError(err.message || 'Erreur lors de la suppression');
            }
        }
    };

    const filteredProducts = useMemo(() => {
        if (!searchTerm) return products;
        const lowerSearch = searchTerm.toLowerCase();
        return products.filter(p =>
            p.name.toLowerCase().includes(lowerSearch)
        );
    }, [products, searchTerm]);

    const paginatedProducts = useMemo(() => {
        const start = (page - 1) * pageSize;
        return filteredProducts.slice(start, start + pageSize);
    }, [filteredProducts, page]);

    const handleSearch = (term) => {
        setSearchTerm(term);
        setPage(1);
    };

    const columns = [
        { header: 'ID', accessor: 'id', width: '80px' },
        {
            header: 'Produit',
            accessor: 'name',
            render: (row) => <span style={{ fontWeight: 600 }}>{row.name}</span>
        },
        {
            header: 'Prix HT',
            accessor: 'price',
            render: (row) => formatAmount(row.price)
        },
        {
            header: 'Stock',
            accessor: 'stock',
            render: (row) => (
                <span className={`badge ${row.stock > 10 ? 'badge-success' : row.stock > 0 ? 'badge-warning' : 'badge-danger'}`}>
                    {row.stock}
                </span>
            )
        },
        {
            header: 'Actions',
            accessor: 'actions',
            width: '120px',
            render: (row) => (
                <div className="flex-gap" style={{ justifyContent: 'flex-end' }}>
                    <button
                        className="btn-icon"
                        onClick={(e) => { e.stopPropagation(); navigate(`/products/${row.id}/edit`); }}
                        title="Modifier"
                    >
                        <Edit size={16} />
                    </button>
                    <button
                        className="btn-icon"
                        style={{ color: 'var(--danger-color)' }}
                        onClick={(e) => handleDelete(e, row.id)}
                        title="Supprimer"
                    >
                        <Trash2 size={16} />
                    </button>
                </div>
            )
        }
    ];

    if (loading && products.length === 0) return <div className="loading-screen">Chargement...</div>;

    return (
        <>
            <div className="page-header">
                <h1 className="page-title">Gestion des Produits</h1>
                <button className="btn btn-primary" onClick={() => navigate('/products/new')}>
                    <Plus size={18} /> Nouveau Produit
                </button>
            </div>

            <ErrorMessage message={error} />

            <div className="glass-card" style={{ padding: '2rem' }}>
                <div className="flex-between mb-4">
                    <SearchBar onSearch={handleSearch} placeholder="Rechercher par nom..." />
                    <div className="text-secondary" style={{ fontSize: '0.875rem' }}>
                        {filteredProducts.length} produit(s) trouvé(s)
                    </div>
                </div>

                <Table
                    columns={columns}
                    data={paginatedProducts}
                    onRowClick={(row) => navigate(`/products/${row.id}/edit`)}
                />

                <Pagination
                    total={filteredProducts.length}
                    page={page}
                    pageSize={pageSize}
                    onPageChange={setPage}
                />
            </div>
        </>
    );
};

export default ProductsPage;
