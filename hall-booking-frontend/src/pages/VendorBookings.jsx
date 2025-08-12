import React, { useEffect, useState } from 'react';
import axios from 'axios';

const VendorBookings = () => {
  const token = localStorage.getItem('token');
  const vendorId = localStorage.getItem('userId'); // Must be set at vendor login

  const [bookings, setBookings] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!token || !vendorId) {
      console.warn("Missing token or vendorId", { token, vendorId });
      setError('⚠️ Please log in as vendor to view bookings.');
      return;
    }

    fetchVendorBookings();
  }, []);

  const fetchVendorBookings = async () => {
    try {
      const res = await axios.get(`http://localhost:8080/booking/vendor/${vendorId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setBookings(res.data);
    } catch (err) {
      console.error("Fetch error:", err);
      setError('Failed to fetch vendor bookings');
    }
  };

  if (error) return <div className="text-red-600 text-center mt-6">{error}</div>;

  return (
    <div className="max-w-6xl mx-auto px-4 pt-6 pb-10">
      <h2 className="text-3xl font-bold text-center mb-8">👥 Users Who Booked You</h2>

      {bookings.length === 0 ? (
        <p className="text-center text-gray-600">No bookings yet.</p>
      ) : (
        bookings.map((b) => {
          const totalPrice = (b.hall?.baseprice || 0) + (b.room?.price || 0);

          return (
            <div key={b.id} className="bg-white shadow border p-6 mb-5 rounded-lg">
              <div className="flex justify-between mb-3">
                <h3 className="font-semibold text-lg text-blue-800">
                  Booking #{b.id}
                </h3>
                <span
                  className={`px-3 py-1 rounded-full text-sm font-medium ${
                    b.status === 'CONFIRMED'
                      ? 'bg-green-100 text-green-800'
                      : b.status === 'CANCELLED'
                      ? 'bg-red-100 text-red-800'
                      : 'bg-yellow-100 text-yellow-800'
                  }`}
                >
                  {b.status}
                </span>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm text-gray-700">
                <div>
                  <p><strong>User:</strong> {b.user?.name} ({b.user?.email})</p>
                  <p><strong>Phone:</strong> {b.user?.phone}</p>
                  <p><strong>Check-in:</strong> {new Date(b.checkin).toLocaleDateString()}</p>
                  <p><strong>Check-out:</strong> {new Date(b.checkout).toLocaleDateString()}</p>
                  <p><strong>Booked At:</strong> {new Date(b.createdat).toLocaleString()}</p>
                </div>
                <div>
                  <p><strong>Service:</strong> {b.hall?.name || b.service?.name}</p>
                  <p><strong>Room:</strong> {b.room?.name || '-'}</p>
                  <p><strong>Total Price:</strong> ₹{totalPrice}</p>

                  {b.qrCodePath && (
                    <div className="mt-3">
                      <img
                        src={`http://localhost:8080/${b.qrCodePath}`}
                        alt="QR Code"
                        className="w-32 h-32 border rounded mb-2"
                      />
                      <a
                        href={`http://localhost:8080/${b.qrCodePath}`}
                        download
                        className="text-blue-600 underline"
                      >
                        ⬇ Download QR
                      </a>
                    </div>
                  )}
                </div>
              </div>
            </div>
          );
        })
      )}
    </div>
  );
};

export default VendorBookings;
