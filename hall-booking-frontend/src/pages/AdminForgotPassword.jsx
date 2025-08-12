import React, { useState } from 'react';
import axios from 'axios';
import { toast } from 'react-toastify';

const AdminForgotPassword = () => {
  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSendOtp = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const res = await axios.post('http://localhost:8081/admin/forgot-password', { email });
      toast.success(res.data.message || 'OTP sent to your email');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to send OTP');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-6 max-w-md mx-auto mt-10 bg-white rounded shadow">
      <h2 className="text-2xl font-bold mb-4">Admin Forgot Password</h2>
      <form onSubmit={handleSendOtp}>
        <input
          type="email"
          placeholder="Enter your registered email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          className="w-full border p-2 rounded mb-4"
          required
        />
        <button type="submit" className="w-full bg-black-600 text-white py-2 rounded" disabled={loading}>
          {loading ? 'Sending OTP...' : 'Send OTP'}
        </button>
      </form>
    </div>
  );
};

export default AdminForgotPassword;
