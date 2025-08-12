import React, { useState } from 'react';
import axios from 'axios';
import { toast } from 'react-toastify';
import { Link } from 'react-router-dom';

const VendorLogin = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const res = await axios.post('http://localhost:8081/vendor/login', { email, password });
         console.log("🔐 Login Response:", res.data);
      
      localStorage.setItem("token", res.data.token);
localStorage.setItem("vendorName", res.data.name);
localStorage.setItem("userId", res.data.userId);  // ✅ Make sure this is set
localStorage.setItem("vendorRole", `ROLE_${res.data.role}`); // ✅ fixed
localStorage.setItem("vendorType", res.data.vendorType || "Vendor"); // ✅ store role (e.g., ROLE_HALL_VENDOR)
   
      toast.success('Vendor login successful!');
      window.location.href = '/vendor-dashboard';
    } catch (error) {
      toast.error('Invalid credentials');
    }
  };

  return (
    <div
      className="min-h-screen flex items-center justify-center bg-cover bg-center"
      style={{ backgroundImage: `url('https://wallpapercave.com/wp/wp5593799.jpg')` }}
    >
      <div className="bg-white p-8 rounded-lg shadow-md max-w-md w-full">
        <h2 className="text-2xl font-bold mb-6 text-center text-gray-800">Vendor Login</h2>
        <form onSubmit={handleLogin} className="space-y-4">
          <input
            type="email"
            placeholder="Email"
            className="w-full p-2 border border-gray-300 rounded"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
          <input
            type="password"
            placeholder="Password"
            className="w-full p-2 border border-gray-300 rounded"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />

          <div className="text-right text-sm mb-2">
            <Link
              to="/forgot-password?role=vendor"
              className="text-blue-600 hover:underline"
            >
              Forgot Password?
            </Link>
          </div>

          <button
            type="submit"
            className="w-full bg-yellow-500 text-white p-2 rounded hover:bg-yellow-400 transition"
          >
            Login
          </button>
        </form>
      </div>
    </div>
  );
};

export default VendorLogin;
