import React, { useEffect, useState } from 'react';
import axios from 'axios';

const VendorProfile = () => {
  const [vendor, setVendor] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const token = localStorage.getItem("token"); // ✅ Correct token key
        const response = await axios.get("http://localhost:8081/vendor/verify", {
          headers: {
            Authorization: `Bearer ${token}`, // ✅ Send token in Authorization header
          },
        });
        setVendor(response.data);
      } catch (err) {
        console.error("❌ Error fetching profile:", err);
        setError("Failed to load profile. Please login again.");
      }
    };

    fetchProfile();
  }, []);

  if (error) return <div className="text-red-500 p-4">{error}</div>;

  if (!vendor) return <div className="text-gray-600 p-4">Loading profile...</div>;

  return (
    <div className="max-w-xl mx-auto mt-10 p-6 bg-white shadow rounded">
      <h2 className="text-2xl font-bold mb-4 text-center">👤 Vendor Profile</h2>
      <p><strong>Name:</strong> {vendor.name}</p>
      <p><strong>Email:</strong> {vendor.email}</p>
      <p><strong>Phone:</strong> {vendor.phoneNumber}</p>
      <p><strong>Country Code:</strong> {vendor.countryCode}</p>
      <p><strong>Role:</strong> {vendor.role}</p>
    </div>
  );
};

export default VendorProfile;
