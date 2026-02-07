import React, { useState, useEffect } from 'react';
import { Search } from 'lucide-react';

const SearchBar = ({ onSearch, placeholder = 'Rechercher...' }) => {
    const [value, setValue] = useState('');

    useEffect(() => {
        const timer = setTimeout(() => {
            onSearch(value);
        }, 300);

        return () => clearTimeout(timer);
    }, [value, onSearch]);

    return (
        <div className="search-bar">
            <Search className="search-icon" size={18} />
            <input
                type="text"
                placeholder={placeholder}
                value={value}
                onChange={(e) => setValue(e.target.value)}
                className="search-input"
            />
        </div>
    );
};

export default SearchBar;
