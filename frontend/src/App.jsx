import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './state/AuthContext';
import Layout from './components/Layout';
import LoginPage from './pages/LoginPage';

// Placeholders for now
import ProductsPage from './pages/products/ProductsPage';
import ProductFormPage from './pages/products/ProductFormPage';
import ClientsPage from './pages/clients/ClientsPage';
import ClientFormPage from './pages/clients/ClientFormPage';
import ClientDetailPage from './pages/clients/ClientDetailPage';
import OrdersPage from './pages/orders/OrdersPage';
import NewOrderPage from './pages/orders/NewOrderPage';
import OrderDetailPage from './pages/orders/OrderDetailPage';

const App = () => {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />

          <Route element={<Layout />}>
            <Route path="/products" element={<ProductsPage />} />
            <Route path="/products/new" element={<ProductFormPage />} />
            <Route path="/products/:id/edit" element={<ProductFormPage />} />

            <Route path="/clients" element={<ClientsPage />} />
            <Route path="/clients/new" element={<ClientFormPage />} />
            <Route path="/clients/:id" element={<ClientDetailPage />} />
            <Route path="/clients/:id/edit" element={<ClientFormPage />} />

            <Route path="/orders" element={<OrdersPage />} />
            <Route path="/orders/new" element={<NewOrderPage />} />
            <Route path="/orders/:id" element={<OrderDetailPage />} />

            <Route path="/" element={<Navigate to="/products" replace />} />
            <Route path="*" element={<Navigate to="/products" replace />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
};

export default App;
