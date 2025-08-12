import React, { useState } from 'react';
import axios from 'axios';
import PhoneInput from 'react-phone-input-2';
import 'react-phone-input-2/lib/style.css';
import { useNavigate } from 'react-router-dom';

const CustomerLogin = () => {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    mobile: '',
    countryCode: '',
    password: '',
  });

  const [errorMessage, setErrorMessage] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [loading, setLoading] = useState(false);

  const handlePhoneChange = (value, data) => {
    const countryCode = '+' + data.dialCode;
    const mobile = value.replace(data.dialCode, '').trim();
    setFormData({ ...formData, countryCode, mobile });
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage('');
    setSuccessMessage('');
    setLoading(true);

    try {
      const response = await axios.post('http://localhost:8080/api/auth/login', formData);
      if (response.data && response.data.token) {
        setSuccessMessage('✅ Login successful');

        localStorage.setItem('token', response.data.token);
        localStorage.setItem('userName', response.data.name);
        localStorage.setItem('userRole', response.data.role);
        // Optional: backend must return userId for this to work
        if (response.data.userId) {
          localStorage.setItem('userId', response.data.userId);
        }

        setTimeout(() => {
          navigate('/');
        }, 1500);
      } else {
        throw new Error('Invalid response from server');
      }
    } catch (error) {
      setErrorMessage(error.response?.data?.message || '❌ Login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-cover bg-center"
      style={{ backgroundImage: `url('https://images6.alphacoders.com/135/1354209.jpeg')` }}>
      <div className="bg-white p-8 rounded-lg shadow-md max-w-md w-full">
        <h2 className="text-2xl font-bold mb-6 text-center text-gray-800">Customer Login</h2>

        {errorMessage && <p className="text-red-600 mb-4 text-center">{errorMessage}</p>}
        {successMessage && <p className="text-green-600 mb-4 text-center">{successMessage}</p>}

        <form className="space-y-4" onSubmit={handleSubmit}>
          <PhoneInput
            country={'in'}
            value={formData.countryCode + formData.mobile}
            onChange={handlePhoneChange}
            inputProps={{
              name: 'mobile',
              required: true,
              className: 'form-control',
            }}
            inputStyle={{
              width: '100%',
              padding: '10px',
              borderRadius: '6px',
              border: '1px solid #ccc',
            }}
          />

          <input
            type="password"
            name="password"
            placeholder="Password"
            className="w-full p-2 border border-gray-300 rounded"
            onChange={handleChange}
            required
          />

          <button
            type="submit"
            disabled={loading}
            className={`w-full bg-yellow-500 text-white p-2 rounded hover:bg-yellow-400 transition ${loading ? 'opacity-50 cursor-not-allowed' : ''}`}
          >
            {loading ? 'Logging in...' : 'Login'}
          </button>
          <p className="text-sm text-center mt-2">
            <span className="text-blue-600 cursor-pointer hover:underline" onClick={() => navigate('/forgot-password')}>
              Forgot Password?
            </span>
          </p>
        </form>
      </div>
    </div>
  );
};

export default CustomerLogin;