import React, { useState, useEffect } from "react";
import axios from "axios";
import { X } from "lucide-react";

const RoomModal = ({ hallId, room, onClose, onSuccess }) => {
  const [formData, setFormData] = useState({
    name: "",
    capacity: "",
    price: "",
  });

  useEffect(() => {
    if (room) {
      setFormData({
        name: room.name,
        capacity: room.capacity,
        price: room.price,
      });
    }
  }, [room]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const token = localStorage.getItem("token");

    try {
      if (room) {
        // Update existing room
        await axios.put(`http://localhost:8080/api/halls/rooms/${room.id}`, formData, {
          headers: { Authorization: `Bearer ${token}` },
        });
      } else {
        // Add new room
        await axios.post(`http://localhost:8080/api/halls/${hallId}/rooms`, formData, {
          headers: { Authorization: `Bearer ${token}` },
        });
      }

      onSuccess(); // Refresh parent list
      onClose();   // Close modal
    } catch (error) {
      console.error("❌ Error saving room:", error);
      alert("Failed to save room. Please try again.");
    }
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg p-6 w-full max-w-md shadow-lg relative">
        <button
          onClick={onClose}
          className="absolute top-3 right-3 text-gray-500 hover:text-red-500"
        >
          <X />
        </button>

        <h2 className="text-xl font-semibold mb-4">
          {room ? "Edit Room" : "Add Room"}
        </h2>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block mb-1 text-gray-700">Room Name</label>
            <input
              type="text"
              name="name"
              required
              value={formData.name}
              onChange={handleChange}
              className="w-full border rounded px-3 py-2"
              placeholder="e.g., Conference Hall A"
            />
          </div>

          <div>
            <label className="block mb-1 text-gray-700">Capacity</label>
            <input
              type="number"
              name="capacity"
              required
              value={formData.capacity}
              onChange={handleChange}
              className="w-full border rounded px-3 py-2"
              placeholder="e.g., 100"
            />
          </div>

          <div>
            <label className="block mb-1 text-gray-700">Price (₹)</label>
            <input
              type="number"
              name="price"
              required
              value={formData.price}
              onChange={handleChange}
              className="w-full border rounded px-3 py-2"
              placeholder="e.g., 25000"
            />
          </div>

          <div className="flex justify-end">
            <button
              type="submit"
              className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
            >
              {room ? "Update" : "Add"} Room
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default RoomModal;
