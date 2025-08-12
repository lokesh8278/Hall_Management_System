import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { motion } from "framer-motion";


const AdminAllBookings = () => {
  const [bookings, setBookings] = useState([]);
  const [filtered, setFiltered] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [statusFilter, setStatusFilter] = useState("ALL");
  const [fromDate, setFromDate] = useState("");
  const [toDate, setToDate] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    const fetchAllBookings = async () => {
      try {
        const token = localStorage.getItem("token");
        const response = await axios.get("http://localhost:8080/booking/all", {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setBookings(response.data);
        setFiltered(response.data);
      } catch (err) {
        console.error("Error fetching bookings:", err);
        setError("Failed to load bookings");
      }
    };

    fetchAllBookings();
  }, []);

  useEffect(() => {
    let results = bookings;

    if (statusFilter !== "ALL") {
      results = results.filter(b => b.status === statusFilter);
    }

    if (searchTerm.trim() !== "") {
      const term = searchTerm.toLowerCase();
      results = results.filter(
        b =>
          b.user?.name?.toLowerCase().includes(term) ||
          b.user?.email?.toLowerCase().includes(term) ||
          b.hall?.name?.toLowerCase().includes(term) ||
          b.room?.name?.toLowerCase().includes(term)
      );
    }

    if (fromDate) {
      results = results.filter(b => new Date(b.checkin) >= new Date(fromDate));
    }
    if (toDate) {
      results = results.filter(b => new Date(b.checkin) <= new Date(toDate));
    }

    setFiltered(results);
  }, [searchTerm, statusFilter, fromDate, toDate, bookings]);

  const getStatusBadge = (status) => {
    const base = "px-2 py-1 text-xs font-semibold rounded";
    if (status === "CONFIRMED") return `${base} bg-green-100 text-green-700`;
    if (status === "PENDING") return `${base} bg-yellow-100 text-yellow-800`;
    if (status === "CANCELLED") return `${base} bg-red-100 text-red-700`;
    return `${base} bg-gray-100 text-gray-800`;
  };

  const exportToCSV = () => {
    const headers = [
      "Booking ID", "User ID", "User Name", "Phone", "Email", "Hall", "Room",
      "Check-In", "Check-Out", "Status"
    ];

    const rows = filtered.map((b) => [
      b.id, b.user?.id, b.user?.name,
      `+91-${b.user?.phoneNumber}`,
      b.user?.email,
      b.hall?.name,
      b.room?.name,
      new Date(b.checkin).toLocaleString(),
      new Date(b.checkout).toLocaleString(),
      b.status
    ]);

    const csvContent =
      "data:text/csv;charset=utf-8," +
      [headers, ...rows].map(e => e.join(",")).join("\n");

    const blob = new Blob([decodeURIComponent(encodeURI(csvContent))], { type: "text/csv;charset=utf-8;" });
    const link = document.createElement("a");
    link.href = URL.createObjectURL(blob);
    link.download = "all_bookings.csv";
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  const total = bookings.length;
  const confirmed = bookings.filter(b => b.status === "CONFIRMED").length;
  const pending = bookings.filter(b => b.status === "PENDING").length;
  const cancelled = bookings.filter(b => b.status === "CANCELLED").length;

  const summaryCard = (label, count, color, statusKey) => (
    <motion.div
      whileHover={{ scale: 1.05 }}
      whileTap={{ scale: 0.98 }}
      onClick={() => setStatusFilter(statusKey)}
      className={`cursor-pointer p-4 rounded shadow text-center transition ${
        statusFilter === statusKey ? 'ring-2 ring-offset-2 ring-' + color : ''
      } bg-${color}-100`}
    >
      <h2 className={`text-xl font-bold text-${color}-800`}>{count}</h2>
      <p className={`text-sm text-${color}-600`}>{label}</p>
    </motion.div>
  );

  return (
    <div className="p-6 min-h-screen bg-gray-100">
    
    

      <h1 className="text-3xl font-bold text-gray-800 mb-4">📊 Booking Dashboard</h1>

      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-4 mb-6">
        {summaryCard("Total Bookings", total, "blue", "ALL")}
        {summaryCard("Confirmed", confirmed, "green", "CONFIRMED")}
        {summaryCard("Pending", pending, "yellow", "PENDING")}
        {summaryCard("Cancelled", cancelled, "red", "CANCELLED")}
      </div>

      <div className="flex flex-col md:flex-row md:items-center gap-4 mb-6">
        <input
          type="text"
          placeholder="Search name, email, hall..."
          className="px-4 py-2 border border-gray-300 rounded-md text-black w-full md:w-64"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />

        <div className="flex items-center gap-2">
          <label className="text-sm font-medium text-gray-700">From</label>
          <input
            type="date"
            className="px-4 py-2 border border-gray-300 rounded-md text-black"
            value={fromDate}
            onChange={(e) => setFromDate(e.target.value)}
          />
        </div>

        <div className="flex items-center gap-2">
          <label className="text-sm font-medium text-gray-700">To</label>
          <input
            type="date"
            className="px-4 py-2 border border-gray-300 rounded-md text-black"
            value={toDate}
            onChange={(e) => setToDate(e.target.value)}
          />
        </div>

        <button
          className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
          onClick={exportToCSV}
        >
          ⬇️ Export CSV
        </button>
      </div>

      <div className="bg-white rounded-lg shadow overflow-x-auto">
        <table className="min-w-full text-sm text-gray-800">
          <thead className="bg-blue-800 text-white">
            <tr>
              <th className="px-4 py-3">Booking ID</th>
              <th className="px-4 py-3">User</th>
              <th className="px-4 py-3">Phone</th>
              <th className="px-4 py-3">Email</th>
              <th className="px-4 py-3">Hall</th>
              <th className="px-4 py-3">Room</th>
              <th className="px-4 py-3">Check-In</th>
              <th className="px-4 py-3">Check-Out</th>
              <th className="px-4 py-3">Status</th>
            </tr>
          </thead>
          <tbody>
            {filtered.map((booking) => (
              <motion.tr
                key={booking.id}
                className="border-b hover:bg-gray-50"
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                transition={{ duration: 0.2 }}
              >
                <td className="px-4 py-2">{booking.id}</td>
                <td className="px-4 py-2">{booking.user?.name}</td>
                <td className="px-4 py-2">+91-{booking.user?.phoneNumber}</td>
                <td className="px-4 py-2">{booking.user?.email}</td>
                <td className="px-4 py-2">{booking.hall?.name}</td>
                <td className="px-4 py-2">{booking.room?.name}</td>
                <td className="px-4 py-2">{new Date(booking.checkin).toLocaleString()}</td>
                <td className="px-4 py-2">{new Date(booking.checkout).toLocaleString()}</td>
                <td className="px-4 py-2">
                  <span className={getStatusBadge(booking.status)}>{booking.status}</span>
                </td>
              </motion.tr>
            ))}
          </tbody>
        </table>

        {filtered.length === 0 && !error && (
          <div className="p-4 text-center text-gray-500">No bookings found.</div>
        )}
      </div>
    </div>
  );
};

export default AdminAllBookings;