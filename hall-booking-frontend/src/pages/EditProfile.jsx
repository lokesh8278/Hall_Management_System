import React, { useEffect, useState } from 'react';
import axios from 'axios';

const EditProfile = () => {
  const [profile, setProfile] = useState({
    name: '',
    email: '',
    phoneNumber: '',
  });

  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState('');

  const token = localStorage.getItem('token');

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const res = await axios.get('http://localhost:8081/admin/profile', {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setProfile(res.data);
      } catch (error) {
        console.error('Failed to fetch profile:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchProfile();
  }, [token]);

  const handleChange = (e) => {
    setProfile({ ...profile, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await axios.put('http://localhost:8081/admin/profile', profile, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setMessage('✅ Profile updated successfully!');
    } catch (error) {
      console.error('Profile update failed:', error);
      setMessage('❌ Failed to update profile');
    }
  };

  if (loading) return <p className="p-6 text-center">Loading...</p>;

  return (
    <div
      className="min-h-screen flex items-center justify-center relative overflow-hidden"
      style={{
        backgroundImage: `url('https://images.unsplash.com/photo-1522071820081-009f0129c71c?auto=format&fit=crop&w=1740&q=80')`,
        backgroundSize: 'cover',
        backgroundRepeat: 'repeat',
        animation: 'scrollBg 40s linear infinite',
      }}
    >
      {/* ✨ Custom animation keyframes */}
      <style>
        {`
          @keyframes scrollBg {
            0% { background-position: 0 0; }
            100% { background-position: 0 1000px; }
          }
        `}
      </style>

      <div className="max-w-xl w-full bg-white bg-opacity-90 backdrop-blur p-8 rounded shadow-lg text-black z-10">
        <h2 className="text-3xl font-bold mb-6 text-center">✏️ Edit Profile</h2>

        {message && (
          <p className={`mb-4 text-center font-medium ${message.startsWith('✅') ? 'text-green-600' : 'text-red-600'}`}>
            {message}
          </p>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block mb-1 font-semibold">Name</label>
            <input
              type="text"
              name="name"
              value={profile.name}
              onChange={handleChange}
              className="w-full border px-4 py-2 rounded"
              required
            />
          </div>
          <div>
            <label className="block mb-1 font-semibold">Email</label>
            <input
              type="email"
              name="email"
              value={profile.email}
              onChange={handleChange}
              className="w-full border px-4 py-2 rounded"
              required
            />
          </div>
          <div>
            <label className="block mb-1 font-semibold">Phone Number</label>
            <input
              type="text"
              name="phoneNumber"
              value={profile.phoneNumber}
              onChange={handleChange}
              className="w-full border px-4 py-2 rounded"
            />
          </div>
          <button
            type="submit"
            className="bg-blue-600 w-full text-white px-6 py-2 rounded hover:bg-blue-700 transition"
          >
            Save Changes
          </button>
        </form>
      </div>
    </div>
  );
};

export default EditProfile;
