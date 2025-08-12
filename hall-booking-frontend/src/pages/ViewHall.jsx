import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import axios from 'axios';
import { MapPin, Phone, Mail, Star } from 'lucide-react';
import { Swiper, SwiperSlide } from 'swiper/react';
import 'swiper/css';

const ViewHall = () => {
  const { id } = useParams();
  const [hall, setHall] = useState(null);
  const [images, setImages] = useState([]);
  const [mapOpen, setMapOpen] = useState(false);

  useEffect(() => {
    axios.get(`http://localhost:8080/api/halls/${id}`)
      .then((res) => setHall(res.data))
      .catch((err) => console.error("Error fetching hall:", err));

    axios.get(`http://localhost:8080/api/halls/documents/${id}/meta`)
      .then((res) => setImages(res.data))
      .catch((err) => console.error("Error fetching images:", err));
  }, [id]);

  if (!hall) return <div className="text-center mt-10">Loading...</div>;

  return (
    <div className="min-h-screen bg-gray-50 py-10 px-6 md:px-20">
      <h1 className="text-3xl font-bold text-center mb-6 mt-10">{hall.name}</h1>

      {/* Carousel */}
      <div className="mb-8">
        {images.length > 0 ? (
          <Swiper spaceBetween={20} slidesPerView={1}>
            {images.map((img) => (
              <SwiperSlide key={img.id}>
                <img
                  src={img.downloadUrl}
                  alt={img.description}
                  className="w-full h-72 object-cover rounded shadow"
                  onError={(e) => {
                    e.target.onerror = null;
                    e.target.src = '/images/default-hall.jpg';
                  }}
                />
              </SwiperSlide>
            ))}
          </Swiper>
        ) : (
          <img
            src="/images/default-hall.jpg"
            alt="Default Hall"
            className="w-full h-72 object-cover rounded shadow"
          />
        )}
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-8 text-lg">
        {/* Left */}
        <div className="space-y-3">
          <p><strong>Base Price:</strong> ₹{hall.basePrice?.toLocaleString()}</p>
          <p><strong>Amenities:</strong> {hall.amenities?.join(', ')}</p>

          <div className="flex items-center gap-2">
            <Star className="text-yellow-500" />
            <span>4.5 / 5 rating (124 reviews)</span>
          </div>

          <div>
            <strong>Food Type:</strong>
            <div className="mt-1 flex gap-4">
              <span className="bg-green-100 text-green-800 px-3 py-1 rounded-full text-sm">Veg</span>
              <span className="bg-red-100 text-red-800 px-3 py-1 rounded-full text-sm">Non-Veg</span>
            </div>
          </div>

          <div className="flex items-center gap-2">
            <Phone className="text-blue-600" />
            <span>+91 9876543210</span>
          </div>

          <div className="flex items-center gap-2">
            <Mail className="text-blue-600" />
            <span>contact@banquethall.com</span>
          </div>

          <div className="flex items-center gap-2">
            <MapPin />
            <button onClick={() => setMapOpen(true)} className="text-blue-600 underline">
              View on Map
            </button>
          </div>
        </div>

        {/* Right */}
        <div>
          {hall.virtualTourUrl && (
            <div>
              <strong>Virtual Tour:</strong>
              <iframe
                src={hall.virtualTourUrl}
                className="w-full h-64 mt-2 border rounded"
                title="Virtual Tour"
                allowFullScreen
              />
            </div>
          )}
        </div>
      </div>

      {/* Buttons */}
      <div className="mt-8 flex gap-4">
        <Link to={`/hall/${hall.id}/book`}>
          <button className="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded shadow">
            Book Hall
          </button>
        </Link>
        <Link to="/">
          <button className="bg-gray-400 hover:bg-gray-500 text-white px-6 py-2 rounded shadow">
            Back
          </button>
        </Link>
      </div>

      {/* Map Modal */}
      {mapOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-60 flex items-center justify-center z-50">
          <div className="bg-white p-4 rounded shadow-lg w-full max-w-2xl relative">
            <button
              onClick={() => setMapOpen(false)}
              className="absolute top-2 right-3 text-gray-600 hover:text-black text-xl"
            >
              ✕
            </button>
            <iframe
              src={`https://www.google.com/maps?q=${hall.latitude},${hall.longitude}&output=embed`}
              className="w-full h-96 rounded"
              title="Google Map"
              allowFullScreen
            ></iframe>
          </div>
        </div>
      )}
    </div>
  );
};

export default ViewHall;