// Updated ManageHalls.jsx with redirect to /rooms/:hallId page
import React, { useEffect, useState } from "react";
import axios from "axios";
import { PlusCircle, Pencil, Trash2 } from "lucide-react";
import { useNavigate } from "react-router-dom";

const ManageHalls = () => {
  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  const [halls, setHalls] = useState([]);

  const fetchHalls = async () => {
    try {
      const response = await axios.get("http://localhost:8080/api/halls/vendor", {
        headers: { Authorization: `Bearer ${token}` },
      });
      setHalls(response.data);
    } catch (error) {
      console.error("Error fetching vendor halls:", error);
    }
  };

  useEffect(() => {
    fetchHalls();
  }, []);

  const handleDelete = async (id) => {
    if (!window.confirm("Delete this hall?")) return;
    try {
      await axios.delete(`http://localhost:8080/api/halls/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      fetchHalls();
    } catch (error) {
      console.error("Error deleting hall:", error);
    }
  };

  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-2xl font-bold">🏢 Manage Halls</h1>
        <button
          className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
          onClick={() => navigate("/vendor-dashboard/addHall")}
        >
          <PlusCircle className="inline mr-1" size={18} /> Add Hall
        </button>
      </div>

      {halls.length === 0 ? (
        <p className="text-gray-500">No halls found.</p>
      ) : (
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
          {halls.map((hall) => (
            <div key={hall.id} className="bg-white shadow rounded-lg p-4 relative">
              <h3 className="text-lg font-bold mb-2">{hall.name}</h3>
              <p className="text-sm text-gray-600 mb-1">
                📍 Lat: {hall.latitude}, Lng: {hall.longitude}
              </p>
              <p className="text-sm text-gray-600 mb-1">💰 Price: ₹{hall.basePrice}</p>
              <div className="absolute top-2 right-2 flex gap-2">
                <Pencil className="text-blue-600 cursor-pointer" />
                <Trash2
                  className="text-red-600 cursor-pointer"
                  onClick={() => handleDelete(hall.id)}
                />
              </div>
              <button
                onClick={() => navigate(`/vendor-dashboard/manage-halls/rooms/${hall.id}`)}
                className="text-sm text-indigo-600 mt-3 underline"
              >
                Manage Rooms
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default ManageHalls;
