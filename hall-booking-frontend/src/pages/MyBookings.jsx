import React, { useEffect, useState } from 'react';
import axios from 'axios';

const API = 'http://localhost:8080';

const MyBookings = () => {
  const token = localStorage.getItem('token');
  const userId = localStorage.getItem('userId');

  const [bookings, setBookings] = useState([]);
  const [filteredStatus, setFilteredStatus] = useState('ALL');
  const [error, setError] = useState('');
  const [qrSrcMap, setQrSrcMap] = useState({}); // bookingId -> object URL

  useEffect(() => {
    if (!token || !userId) {
      setError('⚠️ Please log in to view your bookings.');
      return;
    }
    fetchBookings();
  }, [token, userId]);

  const fetchBookings = async () => {
    try {
      const res = await axios.get(`${API}/booking/user/${userId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setBookings(res.data);

      // Optional: prefetch QR codes as blobs if your QR endpoint is secured
      // comment this out if your QR endpoint is public and you plan to use <img src={`${API}/booking/qr/${id}`} />
      const entries = await Promise.all(
        res.data.map(async (b) => {
          try {
            const qr = await axios.get(`${API}/booking/qr/${b.id}`, {
              responseType: 'blob',
              headers: { Authorization: `Bearer ${token}` }, // remove if public
            });
            return [b.id, URL.createObjectURL(qr.data)];
          } catch {
            return [b.id, null];
          }
        })
      );
      setQrSrcMap(Object.fromEntries(entries));
    } catch (err) {
      console.error(err);
      setError('Failed to fetch bookings');
    }
  };

  const handleCancel = async (bookingId) => {
    try {
      if (!window.confirm('Are you sure you want to cancel this booking?')) return;
      await axios.delete(`${API}/booking/${bookingId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      alert('❌ Booking cancelled successfully');
      fetchBookings();
    } catch (err) {
      console.error(err);
      alert('❌ Error cancelling booking');
    }
  };

  // ✅ Proper PDF download with auth + blob
const downloadPDF = async (bookingId) => {
  const res = await axios.get(`${API}/booking/ticket/${bookingId}`, {
    responseType: 'blob',
    headers: { Authorization: `Bearer ${token}` },
  });
  const blob = new Blob([res.data], { type: 'application/pdf' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `booking-${bookingId}.pdf`;
  document.body.appendChild(a);
  a.click();
  a.remove();
  URL.revokeObjectURL(url);
};


  const filteredBookings =
    filteredStatus === 'ALL'
      ? bookings
      : bookings.filter((b) => b.status === filteredStatus);

  if (error) return <div className="text-red-600 text-center mt-6">{error}</div>;

  return (
    <div className="max-w-5xl mx-auto px-4 py-6">
      <h2 className="text-3xl font-bold text-center mb-6 mt-10">My Bookings</h2>

      <div className="flex justify-center gap-4 mb-6">
        {['ALL', 'CONFIRMED', 'CANCELLED', 'PENDING'].map((status) => (
          <button
            key={status}
            onClick={() => setFilteredStatus(status)}
            className={`px-4 py-1 rounded-full border ${
              filteredStatus === status
                ? 'bg-blue-600 text-white'
                : 'bg-white text-gray-700 border-gray-300'
            }`}
          >
            {status}
          </button>
        ))}
      </div>

      {filteredBookings.length === 0 ? (
        <p className="text-center text-gray-500">No bookings found.</p>
      ) : (
        filteredBookings.map((booking) => {
          const totalPrice =
            (booking.hall?.baseprice || 0) + (booking.room?.price || 0);

          return (
            <div key={booking.id} className="bg-white shadow-md border p-6 mb-6 rounded-lg">
              <div className="flex justify-between items-center mb-4">
                <h3 className="text-xl font-semibold text-blue-800">
                  Booking ID #{booking.id}
                </h3>
                <span
                  className={`px-3 py-1 rounded-full text-sm font-medium ${
                    booking.status === 'CONFIRMED'
                      ? 'bg-green-100 text-green-800'
                      : booking.status === 'CANCELLED'
                      ? 'bg-red-100 text-red-800'
                      : 'bg-yellow-100 text-yellow-800'
                  }`}
                >
                  {booking.status}
                </span>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <p><strong>Check-In:</strong> {new Date(booking.checkin).toLocaleDateString()}</p>
                  <p><strong>Check-Out:</strong> {new Date(booking.checkout).toLocaleDateString()}</p>
                  <p><strong>Hall:</strong> {booking.hall?.name}</p>
                  <p><strong>Room:</strong> {booking.room?.name}</p>
                  <p><strong>Total Price:</strong> ₹{totalPrice}</p>
                  <p><strong>Created:</strong> {new Date(booking.createdat).toLocaleString()}</p>

                  <div className="mt-3 flex gap-3 flex-wrap">
                    <button
                      onClick={() => downloadPDF(booking.id)}
                      className="bg-blue-600 text-white px-4 py-1 rounded hover:bg-blue-700"
                    >
                      📄 Download PDF
                    </button>
                    {booking.status === 'CONFIRMED' && (
                      <button
                        onClick={() => handleCancel(booking.id)}
                        className="bg-red-600 text-white px-4 py-1 rounded hover:bg-red-700"
                      >
                        ❌ Cancel Booking
                      </button>
                    )}
                  </div>
                </div>

                <div className="flex flex-col items-center">
                  {/* If your QR endpoint is public, you can skip qrSrcMap and just:
                      <img src={`${API}/booking/qr/${booking.id}`} ... /> */}
                  {qrSrcMap[booking.id] && (
                    <>
                      <img
                        src={qrSrcMap[booking.id]}
                        alt="QR Code"
                        className="w-40 h-40 object-contain border rounded mb-2"
                      />
                      <a
                        href={qrSrcMap[booking.id]}
                        download={`booking-${booking.id}-qr.png`}
                        className="text-blue-600 underline"
                      >
                        ⬇ Download QR
                      </a>
                    </>
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

export default MyBookings;
