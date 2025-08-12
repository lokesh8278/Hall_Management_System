import React, { useState } from 'react';
import axios from 'axios';
import { useLocation, useNavigate } from 'react-router-dom';

const VerifyOtp = () => {
  const [otp, setOtp] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const query = new URLSearchParams(useLocation().search);
  const rawCountryCode = query.get('countryCode') || '';
  const phoneNumber = query.get('mobile') || '';
  const role = query.get('role') || 'vendor';

  const getApiEndpoint = () => {
    switch (role) {
      case 'vendor':
        return 'http://localhost:8081/vendor/verify-otp';
      case 'admin':
        return 'http://localhost:8081/admin/verify-otp';
      default:
        return 'http://localhost:8081/api/auth/verify-otp';
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!otp.trim()) {
      alert('Please enter the OTP');
      return;
    }

    setLoading(true);

    const formattedCountryCode = rawCountryCode.startsWith('+')
      ? rawCountryCode
      : '+' + rawCountryCode;

    const payload = {
      countryCode: formattedCountryCode,
      phoneNumber: phoneNumber.trim(),
      otp: otp.trim(),
    };

    console.log('🔍 Verifying OTP with:', payload);

    try {
      const response = await axios.post(getApiEndpoint(), payload);
      console.log('✅ Verified:', response.data);
      navigate(
        `/reset-password?countryCode=${formattedCountryCode}&mobile=${phoneNumber}&role=${role}`
      );
    } catch (err) {
      const msg = err?.response?.data || 'OTP verification failed';
      alert(typeof msg === 'string' ? msg : JSON.stringify(msg));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex justify-center items-center bg-gray-100">
      <form
        onSubmit={handleSubmit}
        className="bg-white p-6 rounded shadow-md w-full max-w-md"
      >
        <h2 className="text-xl font-bold mb-4 text-center text-black">
          🔐 Verify OTP
        </h2>

        <label className="block text-sm text-gray-700 mb-1">Enter OTP</label>
        <input
          type="text"
          value={otp}
          onChange={(e) => setOtp(e.target.value)}
          required
          className="w-full p-2 border rounded"
          placeholder="Enter the OTP received"
        />

        <button
          type="submit"
          disabled={loading}
          className="mt-4 w-full bg-green-600 text-white p-2 rounded hover:bg-green-500"
        >
          {loading ? 'Verifying...' : 'Verify OTP'}
        </button>
      </form>
    </div>
  );
};

export default VerifyOtp;
