import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import axios from 'axios';

const AdminLogin = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();

    try {
      const response = await axios.post(
        'http://localhost:8081/admin/login',
        { email, password },
        { headers: { 'Content-Type': 'application/json' } }
      );

      const { token, name, adminId } = response.data;

      if (token) {
        localStorage.setItem('token', token);
        localStorage.setItem('adminId', adminId);
        localStorage.setItem('role', 'admin');

        // Set name or fallback to 'Admin'
        if (name && name !== 'null' && name !== null) {
          localStorage.setItem('adminName', name);
        } else {
          localStorage.setItem('adminName', 'Admin');
        }

        console.log('✅ Token stored:', token);

        // ✅ Directly navigate to dashboard (no alert)
        navigate('/admin/dashboard');
      } else {
        alert('Login failed: Token missing');
      }
    } catch (error) {
      console.error('Login error:', error);
      alert('Invalid credentials or access denied.');
    }
  };

  return (
    <div
      className="min-h-screen flex items-center justify-center bg-cover bg-center"
      style={{
        backgroundImage:
          "url('https://img.freepik.com/premium-vector/geometric-gradient-technology-background_23-2149110132.jpg')",
      }}
    >
      <div className="bg-black bg-opacity-90 p-8 rounded-lg shadow-lg w-full max-w-md backdrop-blur">
        <h2 className="text-2xl font-bold text-center text-white mb-6">ADMIN LOGIN</h2>
        <form onSubmit={handleLogin} className="space-y-4">
          <input
            type="email"
            placeholder="Email"
            className="w-full p-3 border rounded"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
          <input
            type="password"
            placeholder="Password"
            className="w-full p-3 border rounded"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <div className="text-right text-sm">
            <Link to="/admin/forgot-password" className="text-blue-400 hover:underline">
              Forgot Password?
            </Link>
          </div>
          <button
            type="submit"
            className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 transition"
          >
            Login
          </button>
        </form>
      </div>
    </div>
  );
};

export default AdminLogin;
