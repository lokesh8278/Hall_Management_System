import React, { useState } from 'react';
import axios from 'axios';
import { useLocation, useNavigate } from 'react-router-dom';
import { Eye, EyeOff } from 'lucide-react';

const ResetPassword = () => {
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [showNewPassword, setShowNewPassword] = useState(false);

  const navigate = useNavigate();
  const query = new URLSearchParams(useLocation().search);
  const countryCode = query.get('countryCode');
  const phoneNumber = query.get('mobile');
  const role = query.get('role') || 'vendor';

  const getApiEndpoint = () => {
    switch (role) {
      case 'vendor':
        return 'http://localhost:8081/vendor/reset-password';
      case 'admin':
        return 'http://localhost:8081/admin/reset-password';
      default:
        return 'http://localhost:8081/api/auth/reset-password';
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (newPassword !== confirmPassword) {
      alert('Passwords do not match');
      return;
    }

    try {
      const response = await axios.post(getApiEndpoint(), {
        countryCode,
        phoneNumber,
        newPassword,
      });

      alert(response.data || 'Password reset successful!');
      navigate('/login');
    } catch (err) {
      alert(err.response?.data || 'Something went wrong');
    }
  };

  return (
    <div className="min-h-screen flex justify-center items-center bg-gray-100">
      <form onSubmit={handleSubmit} className="bg-white p-6 rounded shadow-md w-full max-w-md">
        <h2 className="text-xl font-bold mb-4 text-center text-black">🔐 Reset Password</h2>

        <label className="block text-sm text-gray-700 mb-1">New Password</label>
        <div className="relative">
          <input
            type={showNewPassword ? 'text' : 'password'}
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
            required
            className="w-full p-2 border rounded pr-10"
            placeholder="Enter new password"
          />
          <span
            className="absolute right-3 top-2.5 cursor-pointer"
            onClick={() => setShowNewPassword(!showNewPassword)}
          >
            {showNewPassword ? <EyeOff size={18} /> : <Eye size={18} />}
          </span>
        </div>

        <label className="block mt-4 text-sm text-gray-700 mb-1">Confirm Password</label>
        <input
          type="password"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
          required
          className="w-full p-2 border rounded"
          placeholder="Re-enter new password"
        />

        <button
          type="submit"
          className="mt-6 w-full bg-blue-600 text-white p-2 rounded hover:bg-blue-500"
        >
          Reset Password
        </button>
      </form>
    </div>
  );
};

export default ResetPassword;
