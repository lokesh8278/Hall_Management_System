import React, { useState } from 'react';
import axios from 'axios';
import { toast } from 'react-toastify';

const AdminResetPassword = () => {
  const [otp, setOtp] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [email, setEmail] = useState('');

  const handleReset = async (e) => {
    e.preventDefault();
    if (newPassword !== confirmPassword) {
      toast.error("Passwords do not match");
      return;
    }

    try {
      const res = await axios.post('http://localhost:8081/admin/reset-password', {
        email,
        otp,
        newPassword,
      });
      toast.success(res.data.message || 'Password reset successfully');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Reset failed');
    }
  };

  return (
    <div className="p-6 max-w-md mx-auto mt-10 bg-white rounded shadow">
      <h2 className="text-2xl font-bold mb-4">Reset Admin Password</h2>
      <form onSubmit={handleReset}>
        <input
          type="email"
          placeholder="Enter Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          className="w-full border p-2 rounded mb-4"
          required
        />
        <input
          type="text"
          placeholder="Enter OTP"
          value={otp}
          onChange={(e) => setOtp(e.target.value)}
          className="w-full border p-2 rounded mb-4"
          required
        />
        <input
          type="password"
          placeholder="New Password"
          value={newPassword}
          onChange={(e) => setNewPassword(e.target.value)}
          className="w-full border p-2 rounded mb-4"
          required
        />
        <input
          type="password"
          placeholder="Confirm Password"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
          className="w-full border p-2 rounded mb-4"
          required
        />
        <button type="submit" className="w-full bg-green-600 text-white py-2 rounded">
          Reset Password
        </button>
      </form>
    </div>
  );
};

export default AdminResetPassword;
