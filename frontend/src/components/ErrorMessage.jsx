import React from 'react';
import { AlertCircle } from 'lucide-react';

const ErrorMessage = ({ message }) => {
    if (!message) return null;

    return (
        <div className="error-message fade-in">
            <AlertCircle size={20} className="error-icon" />
            <span>{message}</span>
        </div>
    );
};

export default ErrorMessage;
