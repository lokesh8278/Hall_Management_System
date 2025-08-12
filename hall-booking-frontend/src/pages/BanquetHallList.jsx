import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import { MapPin, Phone, Star, Users, Video } from 'lucide-react';

const BanquetHallList = () => {
  const [halls, setHalls] = useState([]);

  useEffect(() => {
    axios
      .get('http://localhost:8080/api/halls/gethalls')
      .then((res) => {
        setHalls(res.data);
      })
      .catch((err) => {
        console.error('Error fetching halls:', err);
      });
  }, []);

  return (
    <div className="min-h-screen bg-gray-100 py-10 px-4 md:px-10">
      <h1 className="text-3xl font-bold mb-6 mt-8 text-center">Banquet Halls</h1>

      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
        {halls.map((hall) => (
          <div key={hall.id} className="bg-white shadow-md rounded-lg overflow-hidden hover:shadow-xl transition-all duration-300 flex flex-col h-[530px]">
            <img
              src={hall.thumbnailUrl?.startsWith('http') ? hall.thumbnailUrl : '/images/default-hall.jpg'}
              alt={hall.name}
              className="w-full h-48 object-cover"
              onError={(e) => {
                e.target.onerror = null;
                e.target.src = '/images/default-hall.jpg';
              }}
            />

            <div className="flex-1 p-4 flex flex-col justify-between">
              <div>
                <h2 className="text-lg font-semibold mb-2 text-gray-800">{hall.name}</h2>

                {/* 💰 Price */}
                <p className="text-gray-700 text-sm mb-2">💰 ₹{hall.basePrice?.toLocaleString()}</p>

                {/* 👥 Capacity */}
                <p className="text-gray-700 text-sm flex items-center gap-1">
                  <Users size={14} /> Capacity: {hall.guestCapacity || 'N/A'}
                </p>

                {/* ⭐ Rating */}
                <p className="text-gray-700 text-sm flex items-center gap-1">
                  <Star size={14} className="text-yellow-500" /> Rating: {hall.rating?.toFixed(1) || 'N/A'}
                </p>

                {/* 📞 Contact */}
                <p className="text-gray-700 text-sm flex items-center gap-1 truncate">
                  <Phone size={14} /> {hall.contactNumber || 'N/A'}
                </p>

                {/* 📍 Location */}
                <p className="text-gray-700 text-sm flex items-center gap-1">
                  <MapPin size={14} /> {`Lat: ${hall.latitude?.toFixed(2)}, Lng: ${hall.longitude?.toFixed(2)}`}
                </p>
              </div>

              {/* Amenities */}
              <div className="flex flex-wrap gap-1 my-3">
                {hall.amenities?.map((amenity, idx) => (
                  <span
                    key={idx}
                    className="bg-blue-100 text-blue-800 text-xs font-medium px-2 py-0.5 rounded"
                  >
                    {amenity}
                  </span>
                ))}
              </div>

              {/* Buttons */}
              <div className="flex flex-col gap-2 mt-auto">
                <div className="flex gap-2">
                  <Link
                    to={`/hall/${hall.id}`}
                    className="w-1/2 text-center bg-blue-600 text-white py-2 rounded hover:bg-blue-700 transition"
                  >
                    View Hall
                  </Link>
                  <Link
                    to={`/hall/${hall.id}/book`}
                    className="w-1/2 text-center bg-green-600 text-white py-2 rounded hover:bg-green-700 transition"
                  >
                    Book Hall
                  </Link>
                </div>

                {/* 🎥 Virtual Tour & ☎️ Call Now */}
                <div className="flex gap-2">
                  {hall.virtualTourUrl && (
                    <a
                      href={hall.virtualTourUrl}
                      target="_blank"
                      rel="noreferrer"
                      className="w-1/2 text-center bg-purple-600 text-white py-1.5 rounded hover:bg-purple-700 transition text-sm flex items-center justify-center gap-1"
                    >
                      <Video size={14} /> Tour
                    </a>
                  )}
                  {hall.contactNumber && (
                    <a
                      href={`tel:${hall.contactNumber}`}
                      className="w-1/2 text-center bg-gray-700 text-white py-1.5 rounded hover:bg-gray-800 transition text-sm flex items-center justify-center gap-1"
                    >
                      <Phone size={14} /> Call
                    </a>
                  )}
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default BanquetHallList;