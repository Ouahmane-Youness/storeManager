import React from 'react';
import { ChevronLeft, ChevronRight } from 'lucide-react';

const Pagination = ({ total, page, pageSize, onPageChange }) => {
    const totalPages = Math.ceil(total / pageSize);
    if (totalPages <= 1) return null;

    const pages = Array.from({ length: totalPages }, (_, i) => i + 1);

    return (
        <div className="pagination">
            <span className="pagination-info">
                Affichage de {(page - 1) * pageSize + 1} à {Math.min(page * pageSize, total)} sur {total}
            </span>
            <div className="pagination-controls">
                <button
                    className="btn-icon"
                    onClick={() => onPageChange(page - 1)}
                    disabled={page === 1}
                >
                    <ChevronLeft size={18} />
                </button>

                {pages.map(p => (
                    <button
                        key={p}
                        className={`btn-page ${page === p ? 'active' : ''}`}
                        onClick={() => onPageChange(p)}
                    >
                        {p}
                    </button>
                ))}

                <button
                    className="btn-icon"
                    onClick={() => onPageChange(page + 1)}
                    disabled={page === totalPages}
                >
                    <ChevronRight size={18} />
                </button>
            </div>
        </div>
    );
};

export default Pagination;
