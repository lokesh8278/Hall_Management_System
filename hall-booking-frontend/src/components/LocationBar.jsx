import React from 'react';
import { useNavigate } from 'react-router-dom';

const locations = [
  { name: "Delhi NCR", image: "https://www.mistay.in/travel-blog/content/images/size/w2000/2020/06/cover-10.jpg" },
  { name: "Mumbai", image: "https://www.leisurekart.com/blog/wp-content/uploads/2024/04/Places-to-visit-in-Mumbai.jpg" },
  { name: "Bangalore", image: "https://i.pinimg.com/originals/29/26/5c/29265cfe0982977fd53056d1e218ddd5.jpg" },
  { name: "Hyderabad", image: "https://voiceofadventure.com/wp-content/uploads/2021/03/Hyderabad-feature.jpg" },
  { name: "Chennai", image: "https://wallpaperbat.com/img/866731-chennai-central-wallpaper.jpg" },
  { name: "Goa", image: "https://assets.serenity.co.uk/58000-58999/58779/1296x864.jpg" },
  { name: "Jaipur", image: "https://wallpapers.com/images/hd/jal-mahal-water-palace-jaipur-nighttime-uhki0tnbfn2lgovf.jpg" },
  { name: "Pune", image: "https://punetourism.co.in/images/places-to-visit/headers/shaniwar-wada-pune-tourism-entry-fee-timings-holidays-reviews-header.jpg" },
  { name: "Kolkata", image: "https://yometro.com/images/places/howrah-bridge.jpg" },
  { name: "Lucknow", image: "https://wallpaperaccess.com/full/9414101.jpg" },
];

const LocationBar = () => {
  const navigate = useNavigate();

  const handleCitySelect = (city) => {
    navigate(`/vendors/${encodeURIComponent(city)}`);
  };

  return (
    <div
      className="relative bg-cover bg-center bg-no-repeat text-white z-40"
      style={{
        backgroundImage: `url('https://wallpaperaccess.com/full/39608.jpg')`,
      }}
    >
      {/* Gradient + Blur Overlay */}
      <div className="backdrop-blur-sm bg-gradient-to-r from-black/80 via-black/60 to-black/70 px-6 py-6">
        <div className="max-w-7xl mx-auto flex flex-col md:flex-row items-center justify-between gap-4">
          <div>
            <h2 className="text-2xl font-bold">Bridal Wear</h2>
            <p className="text-sm text-gray-200">Showing 3553 results as per your search criteria</p>
          </div>
        </div>

        {/* City scroll bar */}
        <div className="mt-4 overflow-x-auto">
          <div className="flex gap-4 py-2 w-max max-w-full">
            {locations.map((loc, index) => (
              <div
                key={index}
                onClick={() => handleCitySelect(loc.name)}
                className="flex flex-col items-center cursor-pointer transition-transform transform hover:scale-105"
              >
                <img
                  src={loc.image}
                  alt={loc.name}
                  className="w-16 h-16 object-cover rounded-full border shadow-md"
                />
                <span className="text-sm mt-1 text-white">{loc.name}</span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default LocationBar;
