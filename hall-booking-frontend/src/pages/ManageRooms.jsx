import React, { useEffect, useState } from "react";
import axios from "axios";
import { useParams } from "react-router-dom";
import { Trash2 } from "lucide-react";

const ManageRooms = () => {
  const { hallId } = useParams();
  const token = localStorage.getItem("token");

  const [rooms, setRooms] = useState([]);
  const [hallName, setHallName] = useState("");
  const [newRoom, setNewRoom] = useState({ name: "", capacity: "", price: "" });
  const [showForm, setShowForm] = useState(false);

  const fetchRooms = async () => {
    try {
      const res = await axios.get(`http://localhost:8080/api/halls/${hallId}/rooms`);
      setRooms(res.data);
    } catch (err) {
      console.error("Error fetching rooms", err);
    }
  };

  const fetchHall = async () => {
    try {
      const res = await axios.get(`http://localhost:8080/api/halls/${hallId}`);
      setHallName(res.data.name);
    } catch (err) {
      console.error("Error fetching hall", err);
    }
  };

  useEffect(() => {
    fetchHall();
    fetchRooms();
  }, [hallId]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setNewRoom((prev) => ({ ...prev, [name]: value }));
  };

  const handleCreateRoom = async () => {
    try {
      await axios.post(
        `http://localhost:8080/api/halls/${hallId}/rooms`,
        {
          name: newRoom.name,
          capacity: parseInt(newRoom.capacity),
          price: parseFloat(newRoom.price),
        },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setNewRoom({ name: "", capacity: "", price: "" });
      setShowForm(false);
      fetchRooms();
    } catch (err) {
      console.error("Error creating room", err);
    }
  };

  const handleDeleteRoom = async (roomId) => {
    try {
      await axios.delete(`http://localhost:8080/api/halls/rooms/${roomId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      fetchRooms();
    } catch (err) {
      console.error("Error deleting room", err);
    }
  };

  return (
 <div className="p-6 max-w-3xl mx-auto">
  <div className="flex justify-between items-center mb-4">
    <h1 className="text-2xl font-bold">🏢 Rooms for {hallName}</h1>
    <button
      onClick={() => setShowForm(!showForm)}
      className={`px-4 py-2 rounded-md text-white ${showForm ? "bg-gray-600" : "bg-blue-600 hover:bg-blue-700"}`}
    >
      {showForm ? "← Back to List" : "➕ Add Room"}
    </button>
  </div>

  {showForm ? (
    <div className="bg-white p-4 rounded shadow">
      <h2 className="font-semibold mb-2">🛠️ New Room Details</h2>
      <input
        name="name"
        placeholder="Room Name"
        value={newRoom.name}
        onChange={handleInputChange}
        className="border p-2 rounded w-full mb-2"
      />
      <input
        name="capacity"
        placeholder="Capacity"
        type="number"
        value={newRoom.capacity}
        onChange={handleInputChange}
        className="border p-2 rounded w-full mb-2"
      />
      <input
        name="price"
        placeholder="Price"
        type="number"
        value={newRoom.price}
        onChange={handleInputChange}
        className="border p-2 rounded w-full mb-2"
      />
      <button
        onClick={handleCreateRoom}
        className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700"
      >
        Add Room
      </button>
    </div>
  ) : (
    <div className="space-y-3">
      {rooms.length === 0 ? (
        <p className="text-gray-600 italic">No rooms added yet.</p>
      ) : (
        rooms.map((room) => (
          <div
            key={room.id}
            className="bg-gray-100 p-3 rounded flex justify-between items-center"
          >
            <div>
              🛏️ <strong>{room.name}</strong> | 👥 {room.capacity} | 💵 ₹{room.price}
            </div>
            <button onClick={() => handleDeleteRoom(room.id)}>
              <Trash2 className="text-red-600 hover:text-red-800" />
            </button>
          </div>
        ))
      )}
    </div>
  )}
</div>

  );
};

export default ManageRooms;
