import React, { useEffect, useState } from 'react';
import axios from 'axios';



const AdminUserList = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchProfiles = async () => {
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          console.warn("⚠️ No token found in localStorage");
          setError("Authentication token not found.");
          setLoading(false);
          return;
        }

        console.log("📦 Sending token:", token);

        const response = await axios.get('http://localhost:8080/api/user/all', {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        console.log("✅ Users Fetched:", response.data);
        setUsers(response.data);
      } catch (err) {
        if (err.response) {
          console.error("❌ API Error:", {
            status: err.response.status,
            data: err.response.data,
            message: err.message,
          });
          setError(`Error ${err.response.status}: ${err.response.data.message || 'Unable to fetch users.'}`);
        } else if (err.request) {
          console.error("❌ No response from backend", err.request);
          setError("No response from server.");
        } else {
          console.error("❌ Request setup error:", err.message);
          setError("Unexpected error occurred.");
        }
      } finally {
        setLoading(false);
      }
    };

    fetchProfiles();
  }, []);

  return (
    
    <div className="min-h-screen bg-gray-50 p-6">
   
      <h2 className="text-3xl font-bold text-gray-800 mb-6">👥 All Registered Users</h2>

      {loading ? (
        <p className="text-blue-600 font-medium">Loading users...</p>
      ) : error ? (
        <p className="text-red-600 font-semibold">{error}</p>
      ) : users.length === 0 ? (
        <p className="text-gray-600">No users found.</p>
      ) : (
        <div className="bg-white rounded-lg shadow-lg p-4 overflow-auto border border-gray-200">
          <table className="w-full text-left border-collapse">
            <thead className="bg-blue-800 text-white text-sm uppercase tracking-wide">
              <tr>
                <th className="p-3 border">ID</th>
                <th className="p-3 border">Name</th>
                <th className="p-3 border">Email</th>
                <th className="p-3 border">Phone</th>
                <th className="p-3 border">Gender</th>
                <th className="p-3 border">DOB</th>
              </tr>
            </thead>
            <tbody className="text-gray-900 text-sm">
              {users.map((user, idx) => (
                <tr
                  key={user.id || idx}
                  className={idx % 2 === 0 ? 'bg-white hover:bg-gray-100' : 'bg-gray-50 hover:bg-gray-100'}
                >
                  <td className="p-3 border">{user.id}</td>
                  <td className="p-3 border">{user.name || user.fullName || 'N/A'}</td>
                  <td className="p-3 border">{user.email || 'N/A'}</td>
                  <td className="p-3 border">
                    {user.countryCode
                      ? `${user.countryCode}-${user.mobileNumber || user.phone}`
                      : user.mobileNumber || user.phone || 'N/A'}
                  </td>
                  <td className="p-3 border">{user.gender || 'N/A'}</td>
                  <td className="p-3 border">
                    {user.dob ? new Date(user.dob).toLocaleDateString() : 'N/A'}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default AdminUserList;
