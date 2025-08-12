import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import { toast } from 'react-toastify';

const VendorRooms = () => {
  const { hallId } = useParams();
  const [hall, setHall] = useState(null);
  const [rooms, setRooms] = useState([]);
  const [formData, setFormData] = useState({ roomType: '', capacity: '', price: '' });
  const [editId, setEditId] = useState(null);

  const fetchHall = () => {
    axios.get(`http://localhost:8080/api/halls/${hallId}`, {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
    }).then(res => {
      setHall(res.data);
      setRooms(res.data.rooms || []);
    });
  };

  useEffect(() => { fetchHall(); }, [hallId]);

  const handleSubmit = () => {
    const url = editId
      ? `http://localhost:8080/api/halls/rooms/${editId}`
      : `http://localhost:8080/api/halls/${hallId}/rooms`;
    const method = editId ? 'put' : 'post';

    axios[method](url, formData, {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
    }).then(() => {
      toast.success(`Room ${editId ? 'updated' : 'added'}`);
      setFormData({ roomType: '', capacity: '', price: '' });
      setEditId(null);
      fetchHall();
    });
  };

  const handleDelete = id => {
    axios.delete(`http://localhost:8080/api/halls/rooms/${id}`, {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
    }).then(() => {
      toast.success("Room deleted");
      fetchHall();
    });
  };

  return (
    <div className="p-6">
      <h2 className="text-xl font-bold mb-4">Manage Rooms for {hall?.name}</h2>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
        {['roomType', 'capacity', 'price'].map(key => (
          <input
            key={key}
            className="p-2 border rounded"
            placeholder={key}
            value={formData[key]}
            onChange={e => setFormData({ ...formData, [key]: e.target.value })}
          />
        ))}
        <button className="bg-blue-600 text-white p-2 rounded" onClick={handleSubmit}>
          {editId ? 'Update Room' : 'Add Room'}
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {rooms.map(room => (
          <div key={room.id} className="p-4 border rounded shadow">
            <h3 className="font-bold">{room.roomType}</h3>
            <p>Capacity: {room.capacity}</p>
            <p>₹{room.price}</p>
            <div className="flex gap-2 mt-2">
              <button onClick={() => { setFormData(room); setEditId(room.id); }} className="bg-yellow-500 px-2 py-1 rounded">Edit</button>
              <button onClick={() => handleDelete(room.id)} className="bg-red-600 px-2 py-1 rounded text-white">Delete</button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default VendorRooms;