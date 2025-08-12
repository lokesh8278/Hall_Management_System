import React, { useEffect, useState } from 'react';
import axios from 'axios';

const CustomerDashboard = () => {
  const [profile, setProfile] = useState(null);

  useEffect(() => {
    const fetchProfile = async () => {
      const token = localStorage.getItem('token');
      const res = await axios.get('http://localhost:8080/customer/profile', {
        headers: { Authorization: `Bearer ${token}` }
      });
      setProfile(res.data);
    };

    fetchProfile();
  }, []);

  return profile ? (
    <div className="p-4">
      <h1 className="text-2xl font-bold">Welcome, {profile.name}</h1>
      <p><strong>Email:</strong> {profile.email}</p>
      <p><strong>Phone:</strong> {profile.phoneNumber}</p>
      {/* add more customer data here */}
    </div>
  ) : (
    <p>Loading...</p>
  );
};

export default CustomerDashboard;
