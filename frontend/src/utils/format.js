export const formatAmount = (amount) => {
    if (amount == null) return '0.00 DH';
    const num = typeof amount === 'number' ? amount : parseFloat(amount);
    if (isNaN(num)) return '0.00 DH';

    return new Intl.NumberFormat('fr-FR', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
    }).format(num) + ' DH';
};

export const formatDate = (dateString, withTime = false) => {
    if (!dateString) return '-';
    const date = new Date(dateString);

    if (withTime) {
        return new Intl.DateTimeFormat('fr-FR', {
            year: 'numeric', month: '2-digit', day: '2-digit',
            hour: '2-digit', minute: '2-digit'
        }).format(date);
    }

    return new Intl.DateTimeFormat('fr-FR', {
        year: 'numeric', month: '2-digit', day: '2-digit'
    }).format(date);
};

export const formatStatus = (status) => {
    const map = {
        PENDING: 'En attente',
        CONFIRMED: 'Confirmée',
        CANCELED: 'Annulée',
        REJECTED: 'Rejetée',
        EN_ATTENTE: 'En attente',
        ENCAISSE: 'Encaissé',
        REJETE: 'Rejeté'
    };
    return map[status] || status;
};

export const getTierColor = (tier) => {
    switch (tier) {
        case 'BASIC': return '#9e9e9e'; // Grey
        case 'SILVER': return '#e0e0e0'; // Light grey/silver
        case 'GOLD': return '#ffb300'; // Gold/yellow
        case 'PLATINUM': return '#90caf9'; // Light blue/platinum
        default: return '#e0e0e0';
    }
};
