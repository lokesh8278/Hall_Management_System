// src/routes/ProtectedAdminRoute.jsx
import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';

const ProtectedAdminRoute = () => {
  const token = localStorage.getItem('token');
  const role = localStorage.getItem('role');

  if (!token || role !== 'admin') {
    return <Navigate to="/admin-login" replace />;
  }

  return <Outlet />;
};

export default ProtectedAdminRoute;
