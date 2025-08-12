import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { motion } from 'framer-motion';

const AdminDashboard = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const storedName = localStorage.getItem('adminName');
  const adminName = storedName ? storedName.split(' ')[0] : 'Admin';

  const routes = [
    { label: 'Home', path: '/admin/dashboard' },
    { label: 'Vendors', path: '/admin/vendors' },
    { label: 'Users', path: '/admin/user' },
    { label: 'Bookings', path: '/admin/bookings' },
  ];

  return (
    <div className="min-h-screen bg-white text-gray-900">
      {/* ✅ Dashboard Content */}
      <div className="p-6">
        <motion.p
          className="text-lg text-gray-700 mb-8"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.2 }}
        >
          Welcome, {adminName}! Use the dashboard to manage vendors, users, bookings, and more.
        </motion.p>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <motion.div
            whileHover={{ scale: 1.05 }}
            className="bg-blue-500 text-white p-6 rounded-lg shadow hover:bg-blue-600 cursor-pointer"
            onClick={() => navigate('/admin/vendors')}
          >
            <h2 className="text-xl font-bold mb-2">Vendors</h2>
            <p>View and manage all registered vendors.</p>
          </motion.div>

          <motion.div
            whileHover={{ scale: 1.05 }}
            className="bg-green-500 text-white p-6 rounded-lg shadow hover:bg-green-600 cursor-pointer"
            onClick={() => navigate('/admin/user')}
          >
            <h2 className="text-xl font-bold mb-2">Users</h2>
            <p>View user profiles and bookings.</p>
          </motion.div>

          <motion.div
            whileHover={{ scale: 1.05 }}
            className="bg-yellow-400 text-gray-900 p-6 rounded-lg shadow hover:bg-yellow-500 cursor-pointer"
            onClick={() => navigate('/admin/bookings')}
          >
            <h2 className="text-xl font-bold mb-2">Bookings</h2>
            <p>Check and manage hall bookings.</p>
          </motion.div>
        </div>
      </div>
    </div>
  );
};

export default AdminDashboard;
