import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import PhoneInput from 'react-phone-input-2';
import 'react-phone-input-2/lib/style.css';

const RegisterCustomer = () => {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    confirmPassword: '',
    gender: '',
    dob: '',
    mobile: '',
    countryCode: '',
  });

  const [errors, setErrors] = useState({});
  const [passwordMatchError, setPasswordMatchError] = useState('');
  const [passwordPolicyError, setPasswordPolicyError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    const updatedFormData = { ...formData, [name]: value };
    setFormData(updatedFormData);

    if (name === 'password') {
      const passwordValid = /^(?=.*[A-Z])(?=.*\d).{8,}$/.test(value);
      setPasswordPolicyError(passwordValid ? '' : 'Password must be at least 8 characters, include 1 uppercase and 1 number');
    }

    if (name === 'confirmPassword' || name === 'password') {
      setPasswordMatchError(
        updatedFormData.password !== updatedFormData.confirmPassword
          ? 'Passwords do not match'
          : ''
      );
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrors({});

    if (passwordPolicyError || passwordMatchError) {
      alert("Fix password errors before submitting.");
      return;
    }

    try {
      const response = await axios.post('http://localhost:8080/api/auth/register', formData);
      setSuccessMessage(response.data.message || "Registration successful!");

      setFormData({
        name: '',
        email: '',
        password: '',
        confirmPassword: '',
        gender: '',
        dob: '',
        mobile: '',
        countryCode: '',
      });

      setTimeout(() => {
        navigate('/customer-login');
      }, 2000);
    } catch (error) {
      if (error.response?.data) {
        if (typeof error.response.data === 'string') {
          alert(error.response.data);
        } else {
          setErrors(error.response.data);
        }
      } else {
        alert("Registration failed. " + error.message);
      }
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-cover bg-center"
      style={{ backgroundImage: "url('https://images.unsplash.com/photo-1521791136064-7986c2920216')" }}>
      <div className="bg-black bg-opacity-90 p-8 rounded shadow-md w-full max-w-md">
        <h2 className="text-2xl font-bold mb-4 text-center text-white">Customer Registration</h2>

        {successMessage && (
          <div className="bg-green-500 text-white text-center p-2 rounded mb-4">
            {successMessage}
          </div>
        )}

        <form className="space-y-4" onSubmit={handleSubmit}>
          <label className="block text-white">
            Full Name
            <input name="name" type="text" value={formData.name} onChange={handleChange}
              className={`w-full text-black p-2 border rounded mt-1 ${errors.name ? 'border-red-500' : ''}`} />
            {errors.name && <p className="text-red-500 text-sm">{errors.name}</p>}
          </label>

          <label className="block text-white">
            Email
            <input name="email" type="email" value={formData.email} onChange={handleChange}
              className={`w-full text-black p-2 border rounded mt-1 ${errors.email ? 'border-red-500' : ''}`} />
            {errors.email && <p className="text-red-500 text-sm">{errors.email}</p>}
          </label>

          <label className="block text-white">
            Password
            <input name="password" type="password" value={formData.password} onChange={handleChange}
              className="w-full text-black p-2 border rounded mt-1" />
            {passwordPolicyError && <p className="text-red-500 text-sm">{passwordPolicyError}</p>}
          </label>

          <label className="block text-white">
            Confirm Password
            <input name="confirmPassword" type="password" value={formData.confirmPassword} onChange={handleChange}
              className="w-full text-black p-2 border rounded mt-1" />
            {passwordMatchError && <p className="text-red-500 text-sm">{passwordMatchError}</p>}
          </label>

          <label className="block text-white">
            Gender
            <select name="gender" value={formData.gender} onChange={handleChange}
              className="w-full text-black p-2 border rounded mt-1">
              <option value="">Select Gender</option>
              <option value="Male">Male</option>
              <option value="Female">Female</option>
              <option value="Other">Other</option>
            </select>
            {errors.gender && <p className="text-red-500 text-sm">{errors.gender}</p>}
          </label>

          <label className="block text-white">
            Date of Birth
            <input name="dob" type="date" value={formData.dob} onChange={handleChange}
              className="w-full text-black p-2 border rounded mt-1" />
            {errors.dob && <p className="text-red-500 text-sm">{errors.dob}</p>}
          </label>

          <label className="block text-white w-100">
            Phone Number
            <PhoneInput
              country={'in'}
              value={formData.countryCode + formData.mobile}
              onChange={(value, data) => {
                const countryCode = '+' + data.dialCode;
                const mobile = value.replace(data.dialCode, '').trim();
                setFormData({ ...formData, countryCode, mobile });
              }}
              inputProps={{
                name: 'mobile',
                required: true,
                className: 'form-control',
              }}
              inputStyle={{
                width: '100%',
                padding: '10px',
                borderRadius: '6px',
                border: errors.mobile ? '1px solid red' : '1px solid #ccc',
              }}
            />
            {errors.mobile && <p className="text-red-500 text-sm">{errors.mobile}</p>}
            {errors.countryCode && <p className="text-red-500 text-sm">{errors.countryCode}</p>}
          </label>

          <button type="submit"
            className="w-full bg-yellow-500 text-blue-900 p-2 rounded hover:bg-yellow-400">
            Register
          </button>
        </form>
      </div>
    </div>
  );
};

export default RegisterCustomer;