import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const CustomerProfile = () => {
  const token = localStorage.getItem('token');
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [editMode, setEditMode] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    if (!token) {
      setError('User not logged in');
      setLoading(false);
      return;
    }

    axios
      .get('http://localhost:8080/api/user/getprofile', {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => {
        setProfile(res.data);
        setLoading(false);
      })
      .catch((err) => {
        console.error(err);
        setError('Error fetching profile');
        setLoading(false);
      });
  }, [token]);

  const handleChange = (e) => {
    setProfile({ ...profile, [e.target.name]: e.target.value });
  };

  const handleSave = () => {
    setSuccess('');
    setError('');

    const updatedProfile = {
      name: profile.name,
      email: profile.email,
      mobileNumber: profile.mobileNumber,
      gender: profile.gender,
      dob: profile.dob,
      countryCode: profile.countryCode,
    };

    axios
      .put('http://localhost:8080/api/user/updateprofile', updatedProfile, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then(() => {
        setSuccess('Profile updated successfully!');
        setEditMode(false);
      })
      .catch((err) => {
        console.error(err);
        setError('Failed to update profile');
      });
  };

  if (loading) return <p className="text-center mt-10">Loading...</p>;
  if (error) return <p className="text-center mt-10 text-red-600">{error}</p>;

  return (
    <div className="max-w-2xl mx-auto mt-10 bg-white shadow-md rounded-lg p-6">
      <h2 className="text-3xl font-bold mb-6 text-center text-gray-800">My Profile</h2>

      {success && <p className="text-green-600 text-center mb-4">{success}</p>}
      {error && <p className="text-red-600 text-center mb-4">{error}</p>}

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 text-gray-800">
        {/* Editable fields */}
        <div>
          <label className="font-semibold text-gray-600">Name:</label>
          {editMode ? (
            <input
              type="text"
              name="name"
              value={profile.name}
              onChange={handleChange}
              className="w-full p-2 border rounded mt-1"
            />
          ) : (
            <p>{profile.name}</p>
          )}
        </div>

        <div>
          <label className="font-semibold text-gray-600">Email:</label>
          {editMode ? (
            <input
              type="email"
              name="email"
              value={profile.email}
              onChange={handleChange}
              className="w-full p-2 border rounded mt-1"
            />
          ) : (
            <p>{profile.email}</p>
          )}
        </div>

        <div>
          <label className="font-semibold text-gray-600">Mobile:</label>
          {editMode ? (
            <input
              type="text"
              name="mobileNumber"
              value={profile.mobileNumber}
              onChange={handleChange}
              className="w-full p-2 border rounded mt-1"
            />
          ) : (
            <p>{`${profile.countryCode} ${profile.mobileNumber}`}</p>
          )}
        </div>

        <div>
          <label className="font-semibold text-gray-600">Country Code:</label>
          {editMode ? (
            <input
              type="text"
              name="countryCode"
              value={profile.countryCode}
              onChange={handleChange}
              className="w-full p-2 border rounded mt-1"
            />
          ) : (
            <p>{profile.countryCode}</p>
          )}
        </div>

        <div>
          <label className="font-semibold text-gray-600">Gender:</label>
          {editMode ? (
            <select
              name="gender"
              value={profile.gender}
              onChange={handleChange}
              className="w-full p-2 border rounded mt-1"
            >
              <option value="">Select</option>
              <option value="MALE">Male</option>
              <option value="FEMALE">Female</option>
              <option value="OTHER">Other</option>
            </select>
          ) : (
            <p>{profile.gender || 'N/A'}</p>
          )}
        </div>

        <div>
          <label className="font-semibold text-gray-600">Date of Birth:</label>
          {editMode ? (
            <input
              type="date"
              name="dob"
              value={profile.dob}
              onChange={handleChange}
              className="w-full p-2 border rounded mt-1"
            />
          ) : (
            <p>{profile.dob || 'N/A'}</p>
          )}
        </div>

        {/* Read-only fields */}
   

        <div>
          <label className="font-semibold text-gray-600">Status:</label>
          <p className={profile.active ? 'text-green-600' : 'text-red-600'}>
            {profile.active ? 'Active' : 'Inactive'}
          </p>
        </div>

    
      </div>

      <div className="flex flex-col sm:flex-row justify-center gap-4 mt-8">
        {!editMode && (
          <button
            className="bg-blue-600 text-white px-6 py-2 rounded hover:bg-blue-700 transition"
            onClick={() => setEditMode(true)}
          >
            Edit Profile
          </button>
        )}

        {editMode && (
          <>
            <button
              className="bg-green-600 text-white px-6 py-2 rounded hover:bg-green-700 transition"
              onClick={handleSave}
            >
              Save Changes
            </button>
            <button
              className="bg-gray-500 text-white px-6 py-2 rounded hover:bg-gray-600 transition"
              onClick={() => setEditMode(false)}
            >
              Cancel
            </button>
          </>
        )}

        <button
          className="bg-yellow-600 text-white px-6 py-2 rounded hover:bg-yellow-700 transition"
          onClick={() => navigate('/changepassword')}
        >
          Change Password
        </button>
      </div>
    </div>
  );
};

export default CustomerProfile;
