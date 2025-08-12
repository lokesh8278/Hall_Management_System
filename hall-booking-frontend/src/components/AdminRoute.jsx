import React from 'react';
import { Navigate } from 'react-router-dom';

const AdminRoute = ({ children }) => {
  const token = localStorage.getItem('token');
  const role = localStorage.getItem('role');

  if (!token || role !== 'admin') {
    console.error("❌ Access denied: missing token or role is not admin");
    return <div className="p-6 text-red-600 font-bold">❌ Unauthorized: Admin access only</div>;
  }

  return children;
};

export default AdminRoute;
