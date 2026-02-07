import React from 'react';

const Table = ({ columns, data, onRowClick }) => {
    return (
        <div className="table-container fade-in">
            <table className="data-table">
                <thead>
                    <tr>
                        {columns.map((col, index) => (
                            <th key={index} style={{ width: col.width }}>
                                {col.header}
                            </th>
                        ))}
                    </tr>
                </thead>
                <tbody>
                    {data.length === 0 ? (
                        <tr>
                            <td colSpan={columns.length} className="empty-state">
                                Aucune donnée à afficher.
                            </td>
                        </tr>
                    ) : (
                        data.map((row, rowIndex) => (
                            <tr
                                key={rowIndex}
                                className={onRowClick ? 'clickable-row' : ''}
                                onClick={() => onRowClick && onRowClick(row)}
                            >
                                {columns.map((col, colIndex) => (
                                    <td key={colIndex}>
                                        {col.render ? col.render(row) : row[col.accessor]}
                                    </td>
                                ))}
                            </tr>
                        ))
                    )}
                </tbody>
            </table>
        </div>
    );
};

export default Table;
