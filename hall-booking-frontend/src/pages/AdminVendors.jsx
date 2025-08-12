import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
const AdminVendors = () => {
  const [approvedVendors, setApprovedVendors] = useState([]);
  const [pendingVendors, setPendingVendors] = useState([]);
  const [searchId, setSearchId] = useState('');
  const [searchResult, setSearchResult] = useState(null);
  const [showApproved, setShowApproved] = useState(false);
  const [showPending, setShowPending] = useState(false);

  const navigate = useNavigate();

  const getAuthHeaders = () => {
    const token = localStorage.getItem('token');
    if (!token) {
      console.warn('❗ No token found, redirecting to login...');
      navigate('/admin-login');
      return null;
    }
    return {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    };
  };

  const fetchVendors = async () => {
    try {
      const headers = getAuthHeaders();
      if (!headers) return;

   const [approved, pending] = await Promise.all([
  axios.get('http://localhost:8081/admin/vendors', headers),
  axios.get('http://localhost:8081/admin/pending-vendors', headers),
]);
      setApprovedVendors(approved.data);
      setPendingVendors(pending.data);
    } catch (error) {
      console.error('❌ Error fetching vendors:', error);
      if (error.response?.status === 403) {
        alert('⚠️ Unauthorized access. Please login again.');
        localStorage.removeItem('token');
        navigate('/admin-login');
      }
    }
  };

  const approveVendor = async (vendorId) => {
    try {
      await axios.post(`http://localhost:8081/admin/approve/${vendorId}`, {}, getAuthHeaders());
      fetchVendors();
    } catch (error) {
      console.error('❌ Error approving vendor:', error);
      alert('Failed to approve vendor');
    }
  };

  const deleteVendor = async (vendorId) => {
    try {
      await axios.delete(`http://localhost:8081/admin/reject/${vendorId}`, getAuthHeaders());
      fetchVendors();
    } catch (error) {
      console.error('❌ Error deleting vendor:', error);
      alert('Failed to delete vendor');
    }
  };

  const handleSearch = async () => {
    if (!searchId.trim()) return;
    try {
      const res = await axios.get(`http://localhost:8081/admin/vendor/${searchId}`, getAuthHeaders());
      setSearchResult(res.data);
    } catch (error) {
      console.error('🔍 Vendor not found:', error);
      setSearchResult(null);
      alert('Vendor not found');
    }
  };

  useEffect(() => {
    fetchVendors();
  }, []);

  return (
    <div className="min-h-screen bg-white p-8">
      <div className="max-w-6xl mx-auto bg-white shadow-xl rounded-lg p-6 border">

        {/* Header */}
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-3xl font-bold text-blue-900">🎯 ADMIN MANAGEMENT</h1>
        </div>

        {/* Search */}
        <div className="mb-6 flex flex-col sm:flex-row items-center gap-4">
          <input
            type="text"
            placeholder="Search by Vendor ID"
            className="border p-2 rounded w-full sm:w-64 shadow-sm"
            value={searchId}
            onChange={(e) => setSearchId(e.target.value)}
          />
          <button
            onClick={handleSearch}
            className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 transition"
          >
            Search
          </button>
        </div>

        {/* Search Result */}
        {searchResult && (
          <div className="mb-6 bg-blue-50 border-l-4 border-blue-400 p-4 rounded shadow">
            <h2 className="text-lg font-semibold">🔍 Search Result:</h2>
            <p><strong>ID:</strong> {searchResult.id}</p>
            <p><strong>Name:</strong> {searchResult.name}</p>
            <p><strong>Email:</strong> {searchResult.email}</p>
            <p><strong>Status:</strong> {searchResult.status}</p>
          </div>
        )}

        {/* Approved Vendors */}
        <div className="mb-8">
          <button
            onClick={() => setShowApproved(!showApproved)}
            className="text-blue-700 font-semibold underline mb-2"
          >
            {showApproved ? 'Hide' : 'Show'} Approved Vendors
          </button>

          {showApproved && (
            <div className="overflow-x-auto mt-2 bg-blue text-black p-4 rounded shadow border">
              <table className="w-full table-auto">
                <thead>
                  <tr className="bg-blue-100">
                    <th className="border px-4 py-2">ID</th>
                    <th className="border px-4 py-2">Name</th>
                    <th className="border px-4 py-2">Email</th>
                    <th className="border px-4 py-2">Mobile</th>
                  </tr>
                </thead>
                <tbody>
                  {approvedVendors.length === 0 ? (
                    <tr>
                      <td colSpan="4" className="text-center py-4 text-blue-500">
                        No approved vendors found.
                      </td>
                    </tr>
                  ) : (
                    approvedVendors.map((vendor) => (
                      <tr key={vendor.id}>
                        <td className="border px-4 py-2">{vendor.id}</td>
                        <td className="border px-4 py-2">{vendor.name}</td>
                        <td className="border px-4 py-2">{vendor.email}</td>
                        <td className="border px-4 py-2">
                          {vendor.mobile || `${vendor.countryCode || ''} ${vendor.phoneNumber || ''}`}
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          )}
        </div>

        {/* Pending Vendors */}
        <div>
          <button
            onClick={() => setShowPending(!showPending)}
            className="text-yellow-800 font-semibold underline mb-2"
          >
            {showPending ? 'Hide' : 'Show'} Pending Vendor Requests
          </button>

          {showPending && (
            <div className="overflow-x-auto mt-2 bg-white text-black p-4 rounded shadow border">
              <table className="w-full table-auto">
                <thead>
                  <tr className="bg-yellow-100">
                    <th className="border px-4 py-2">ID</th>
                    <th className="border px-4 py-2">Name</th>
                    <th className="border px-4 py-2">Email</th>
                    <th className="border px-4 py-2">Mobile</th>
                    <th className="border px-4 py-2">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {pendingVendors.length === 0 ? (
                    <tr>
                      <td colSpan="5" className="text-center py-4 text-gray-500">
                        No pending vendor requests.
                      </td>
                    </tr>
                  ) : (
                    pendingVendors.map((vendor) => (
                      <tr key={vendor.id}>
                        <td className="border px-4 py-2">{vendor.id}</td>
                        <td className="border px-4 py-2">{vendor.name}</td>
                        <td className="border px-4 py-2">{vendor.email}</td>
                        <td className="border px-4 py-2">
                          {(vendor.countryCode || '') + ' ' + (vendor.phoneNumber || '')}
                        </td>
                        <td className="border px-4 py-2 space-x-2">
                          <button
                            onClick={() => approveVendor(vendor.id)}
                            className="bg-green-500 text-white px-3 py-1 rounded hover:bg-green-600"
                          >
                            Approve
                          </button>
                          <button
                            onClick={() => deleteVendor(vendor.id)}
                            className="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600"
                          >
                            Delete
                          </button>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default AdminVendors;
