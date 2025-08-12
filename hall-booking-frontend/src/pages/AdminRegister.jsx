import React, { useState } from 'react';
import axios from 'axios';

const AdminRegister = () => {
  const [form, setForm] = useState({ name: '', email: '', password: '' });

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await axios.post('http://localhost:8081/admin/register', form);
      alert('Registration successful. Please login.');
    } catch (err) {
      alert('Error during registration');
    }
  };

  return (
    <div className="min-h-screen flex justify-center items-center bg-gradient-to-br from-slate-100 to-blue-200">
      <form onSubmit={handleSubmit} className="bg-white p-8 rounded shadow w-full max-w-md">
        <h2 className="text-2xl font-bold mb-4 text-blue-900">Admin Registration</h2>
        <input
          type="text"
          placeholder="Name"
          className="w-full mb-4 p-2 border rounded"
          value={form.name}
          onChange={(e) => setForm({ ...form, name: e.target.value })}
        />
        <input
          type="email"
          placeholder="Email"
          className="w-full mb-4 p-2 border rounded"
          value={form.email}
          onChange={(e) => setForm({ ...form, email: e.target.value })}
        />
        <input
          type="password"
          placeholder="Password"
          className="w-full mb-4 p-2 border rounded"
          value={form.password}
          onChange={(e) => setForm({ ...form, password: e.target.value })}
        />
        <button
          type="submit"
          className="w-full bg-green-600 text-white py-2 rounded hover:bg-green-700"
        >
          Register
        </button>
      </form>
    </div>
  );
};

export default AdminRegister;
