import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import '../custom-datepicker.css';

const BookHall = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [selectedDate, setSelectedDate] = useState('');
  const [available, setAvailable] = useState(null);
  const [rooms, setRooms] = useState([]);
  const [filteredRooms, setFilteredRooms] = useState([]);
  const [selectedRoom, setSelectedRoom] = useState(null);
  const [message, setMessage] = useState('');
  const [bookingStatus, setBookingStatus] = useState('');
  const [loadingRooms, setLoadingRooms] = useState(true);
  const [roomMap, setRoomMap] = useState({});
  const [availableDates, setAvailableDates] = useState([]);
  const [bookedDates, setBookedDates] = useState([]);

  useEffect(() => {
    const fetchRooms = async () => {
      setLoadingRooms(true);
      try {
        const res = await axios.get(`http://localhost:8080/api/halls/${id}/rooms`);
        setRooms(res.data);
        setRoomMap(
          res.data.reduce((map, room) => {
            map[room.id] = room;
            return map;
          }, {})
        );
      } catch (err) {
        console.error('Error fetching rooms:', err);
      } finally {
        setLoadingRooms(false);
      }
    };

    fetchRooms();
  }, [id]);

  useEffect(() => {
    const fetchAvailableDates = async () => {
      try {
        const res = await axios.get(`http://localhost:8080/api/halls/${id}/available-dates?days=30`);
        const avail = res.data.availableDates.map((d) => new Date(d));

        const allDates = Array.from({ length: 30 }, (_, i) => {
          const dt = new Date();
          dt.setDate(dt.getDate() + i);
          return dt;
        });

        const booked = allDates.filter(
          (d) => !avail.some((a) => a.toDateString() === d.toDateString())
        );

        setAvailableDates(avail);
        setBookedDates(booked);
      } catch (err) {
        console.error('Error loading available dates:', err);
      }
    };

    fetchAvailableDates();
  }, [id]);

  const highlightDates = [
    {
      'react-datepicker__day--highlighted-available': availableDates,
    },
    {
      'react-datepicker__day--highlighted-booked': bookedDates,
    },
  ];

  const checkAvailability = async () => {
    setMessage('');
    setBookingStatus('');
    try {
      const res = await axios.post(`http://localhost:8080/api/halls/${id}/availability`, {
        date: selectedDate,
      });

      setAvailable(res.data.available);
      setMessage(res.data.message);

      if (res.data.availableRooms?.length > 0) {
        const filtered = rooms.filter((room) =>
          res.data.availableRooms.includes(room.id)
        );
        setFilteredRooms(filtered);
      } else {
        setFilteredRooms(rooms);
      }
    } catch (err) {
      console.error(err);
      setMessage('Error checking availability');
    }
  };

  const confirmBooking = async () => {
    const token = localStorage.getItem('token');
    const userId = localStorage.getItem('userId');

    if (!token || !userId) {
      alert('Please login to continue.');
      navigate('/customer-login');
      return;
    }

    if (!selectedRoom) {
      alert('Please select a room');
      return;
    }

    try {
      const res = await axios.post(
        `http://localhost:8080/booking/addbooking`,
        {
          checkin: `${selectedDate}T09:00`,
          checkout: `${selectedDate}T23:00`,
          userId: parseInt(userId),
          hallId: parseInt(id),
          roomId: parseInt(selectedRoom),
        },
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setBookingStatus(res.data);
      navigate('/payment');
    } catch (err) {
      console.error(err);
      setBookingStatus('Booking failed: ' + (err.response?.data || 'Unexpected error.'));
    }
  };

  const calculateTotalPrice = () => {
    const room = roomMap[selectedRoom];
    if (!room || !selectedDate) return 0;
    return room.price;
  };

  return (
    <div className="max-w-3xl mx-auto p-4">
      <h1 className="text-2xl font-bold mb-4 mt-10 text-center">Book Hall</h1>

      <DatePicker
        selected={selectedDate ? new Date(selectedDate) : null}
        onChange={(date) => {
          const formatted = date.toISOString().split('T')[0];
          setSelectedDate(formatted);
          setAvailable(null);
          setBookingStatus('');
          setMessage('');
          setSelectedRoom(null);
        }}
        minDate={new Date()}
        highlightDates={highlightDates}
        placeholderText="Select a date"
        className="border p-2 mb-4 w-full rounded"
        dateFormat="yyyy-MM-dd"
      />

      <button
        onClick={checkAvailability}
        className="mb-4 px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
      >
        Check Availability
      </button>

      {message && <p className="mb-4 text-lg">{message}</p>}

      {available && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
          {filteredRooms.map((room) => (
            <div
              key={room.id}
              onClick={() => setSelectedRoom(room.id)}
              className={`border rounded p-4 cursor-pointer shadow hover:shadow-lg transition duration-300 ${
                selectedRoom === room.id ? 'border-green-500 bg-green-50' : ''
              }`}
            >
              <h2 className="text-lg font-semibold">{room.name}</h2>
              <p>Capacity: {room.capacity}</p>
              <p>Price: ₹{room.price}</p>
            </div>
          ))}
        </div>
      )}

      {selectedRoom && (
        <div className="mb-4 text-green-700 font-medium">
          Total Price: ₹{calculateTotalPrice()}
        </div>
      )}

      {available && (
        <button
          onClick={confirmBooking}
          className="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700"
        >
          Confirm Booking
        </button>
      )}

      {bookingStatus && (
        <div className="mt-4 p-3 bg-gray-100 border rounded">{bookingStatus}</div>
      )}
    </div>
  );
};

export default BookHall;