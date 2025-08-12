import React, { useState } from 'react';

const RegisterVendor = () => {
  const [form, setForm] = useState({
    businessName: '',
    vendorType: '',
    email: '',
    password: '',
    phoneNumber: '',
    countryCode: '+91',
  });
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage('');
    setError('');

    const requestBody = {
      name: form.businessName,
      email: form.email,
      password: form.password,
      countryCode: form.countryCode,
      phoneNumber: form.phoneNumber,
      vendorType: form.vendorType, // sent as vendorType to backend
      features: [],
    };

    try {
      const response = await fetch('http://localhost:8081/vendor/register-request', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(requestBody),
      });
      const data = await response.text();
      if (response.ok) {
        setMessage(data);
        setForm({
          businessName: '',
          email: '',
          password: '',
          countryCode: '+91',
          phoneNumber: '',
          vendorType: '',
        });
      } else {
        setError(data || 'Registration failed!');
      }
    } catch (err) {
      setError('Registration failed! Try again.');
    }
  };

  return (
    <div
      className="min-h-screen flex items-center justify-center bg-cover bg-center"
      style={{ backgroundImage: "url('https://images.unsplash.com/photo-1521334884684-d80222895322')" }}
    >
      <div className="bg-white bg-opacity-90 p-8 rounded shadow-md w-full max-w-md">
        <h2 className="text-2xl font-bold mb-4 text-center">Vendor Registration</h2>
        <form className="space-y-4" onSubmit={handleSubmit}>
          <input name="businessName" type="text" placeholder="Business Name" className="w-full p-2 border rounded"
            value={form.businessName} onChange={handleChange} required />

          {/* ✅ Dropdown for Category instead of free text */}
       <select name="vendorType" className="w-full p-2 border rounded"
  value={form.vendorType} onChange={handleChange} required>
  <option value="">Select Category</option>
  <option value="HALL_VENDOR">Halls / Venues</option>
  <option value="PHOTOGRAPHER">Photography</option>
  <option value="CATERING">Catering</option>
  <option value="MEHANDI_ARTIST">Mehandi Artists</option>
  <option value="DECORATOR">Decoration</option>
</select>


          <input name="email" type="email" placeholder="Email" className="w-full p-2 border rounded"
            value={form.email} onChange={handleChange} required />
          <input name="password" type="password" placeholder="Password" className="w-full p-2 border rounded"
            value={form.password} onChange={handleChange} required />
          <input name="phoneNumber" type="text" placeholder="Phone Number" className="w-full p-2 border rounded"
            value={form.phoneNumber} onChange={handleChange} required />

          <select name="countryCode" className="w-full p-2 border rounded"
            value={form.countryCode} onChange={handleChange}>
            <option value="+91">+91 (India)</option>
            <option value="+1">+1 (USA)</option>
            {/* Add more countries as needed */}
          </select>

          <button type="submit" className="w-full bg-yellow-500 text-white p-2 rounded hover:bg-yellow-400">
            Register
          </button>
        </form>

        {message && <div className="mt-4 text-green-600 text-center">{message}</div>}
        {error && <div className="mt-4 text-red-600 text-center">{error}</div>}
      </div>
    </div>
  );
};

export default RegisterVendor;
