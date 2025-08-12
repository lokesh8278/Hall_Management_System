import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { toast } from 'react-toastify';
import { useNavigate } from 'react-router-dom';

const VendorHalls = () => {
  const [halls, setHalls] = useState([]);
  const [formData, setFormData] = useState({ name: '', location: '', category: '', basePrice: '' });
  const [editId, setEditId] = useState(null);
  const navigate = useNavigate();

  const fetchHalls = () => {
    axios.get('http://localhost:8080/api/halls/gethalls', {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
    }).then(res => setHalls(res.data)).catch(console.error);
  };

  useEffect(() => { fetchHalls(); }, []);

  const handleSubmit = () => {
    const url = editId
      ? `http://localhost:8080/api/halls/${editId}`
      : 'http://localhost:8080/api/halls';
    const method = editId ? 'put' : 'post';

    axios[method](url, formData, {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
    }).then(() => {
      toast.success(`Hall ${editId ? 'updated' : 'added'}`);
      setFormData({ name: '', location: '', category: '', basePrice: '' });
      setEditId(null);
      fetchHalls();
    });
  };

  const handleDelete = id => {
    axios.delete(`http://localhost:8080/api/halls/${id}`, {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
    }).then(() => {
      toast.success("Hall deleted");
      fetchHalls();
    });
  };

  return (
    <div className="p-6">
      <h2 className="text-xl font-bold mb-4">Manage Halls</h2>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
        {['name', 'location', 'category', 'basePrice'].map(key => (
          <input
            key={key}
            className="p-2 border rounded"
            placeholder={key}
            value={formData[key]}
            onChange={e => setFormData({ ...formData, [key]: e.target.value })}
          />
        ))}
        <button className="bg-blue-600 text-white p-2 rounded" onClick={handleSubmit}>
          {editId ? 'Update Hall' : 'Add Hall'}
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {halls.map(hall => (
          <div key={hall.id} className="p-4 border rounded shadow">
            <h3 className="font-bold text-lg">{hall.name}</h3>
            <p>{hall.location}</p>
            <p>₹{hall.basePrice}</p>
            <div className="flex gap-2 mt-2">
              <button onClick={() => { setFormData(hall); setEditId(hall.id); }} className="bg-yellow-500 px-2 py-1 rounded">Edit</button>
              <button onClick={() => handleDelete(hall.id)} className="bg-red-600 text-white px-2 py-1 rounded">Delete</button>
              <button onClick={() => navigate(`/vendor/halls/${hall.id}/rooms`)} className="bg-green-600 px-2 py-1 rounded text-white">Rooms</button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default VendorHalls;