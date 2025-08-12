import React, { useState } from 'react';
import PhoneInput from 'react-phone-input-2';
import 'react-phone-input-2/lib/style.css';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const ForgotPassword = () => {
  const [phone, setPhone] = useState('');
  const [countryCode, setCountryCode] = useState('');
  const [mobile, setMobile] = useState('');
  const [role, setRole] = useState('vendor'); // default as vendor
  const navigate = useNavigate();

  const handlePhoneChange = (value, data) => {
    const dialCode = data.dialCode;
    setCountryCode('+' + dialCode);
    setMobile(value.replace(dialCode, '').trim());
    setPhone(value);
  };

  const getApiEndpoint = () => {
    switch (role) {
      case 'vendor':
        return 'http://localhost:8081/vendor/forgot-password';
      case 'admin':
        return 'http://localhost:8081/admin/forgot-password';
      default:
        return 'http://localhost:8081/api/auth/forgot-password';
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post(getApiEndpoint(), {
        countryCode,
        phoneNumber: mobile,
      });

      alert(response.data || 'OTP sent successfully');
      navigate(`/verify-otp?countryCode=${countryCode}&mobile=${mobile}&role=${role}`);
    } catch (err) {
      alert(err.response?.data || 'Failed to send OTP');
    }
  };

  return (
    <div className="min-h-screen flex justify-center items-center bg-cover bg-center"
         style={{ backgroundImage: `url('https://png.pngtree.com/thumb_back/fw800/background/20230620/pngtree-secure-your-personal-data-with-3d-lock-and-password-field-a-image_3650162.jpg')` }}>
      <form className="bg-white p-6 rounded shadow-md w-full max-w-md" onSubmit={handleSubmit}>
        <h2 className="text-xl font-bold mb-4 text-center text-gray-800">Forgot Password</h2>

        <select
          value={role}
          onChange={(e) => setRole(e.target.value)}
          className="w-full border mb-4 p-2 rounded"
        >
          <option value="customer">Customer</option>
          <option value="vendor">Vendor</option>
          <option value="admin">Admin</option>
        </select>

        <PhoneInput
          country={'in'}
          value={phone}
          onChange={handlePhoneChange}
          inputStyle={{ width: '100%' }}
        />

        <button
          type="submit"
          className="mt-4 w-full bg-yellow-500 text-white p-2 rounded hover:bg-yellow-400 transition"
        >
          Send OTP
        </button>
      </form>
    </div>
  );
};

export default ForgotPassword;
