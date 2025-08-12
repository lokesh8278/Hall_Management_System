import React, { useEffect, useState } from 'react';

const LiveLocation = () => {
  const [location, setLocation] = useState('Fetching location...');
  const latitude = 17.3850;
  const longitude = 78.4867;

  useEffect(() => {
    const fetchLocation = async () => {
      try {
        const response = await fetch(
          `https://nominatim.openstreetmap.org/reverse?format=json&lat=${latitude}&lon=${longitude}`
        );
        const data = await response.json();
        const city = data.address.city || data.address.town || data.address.village || 'Unknown';
        const country = data.address.country || '';
        setLocation(`${city}, ${country}`);
      } catch (error) {
        setLocation('Unable to fetch location');
      }
    };

    fetchLocation();
  }, []);

  return (
    <div className="bg-white p-4 rounded shadow text-sm text-gray-700 font-medium flex flex-col sm:flex-row items-start sm:items-center gap-1 sm:gap-2">
      <div className="flex items-center gap-2">
        <span className="text-lg">🏢</span>
        <span>Our Office Location:</span>
        <span className="text-blue-700">{location}</span>
      </div>
      <span className="text-xs text-gray-500 ml-1">
        (Lat: {latitude}, Lon: {longitude})
      </span>
    </div>
  );
};

export default LiveLocation;
