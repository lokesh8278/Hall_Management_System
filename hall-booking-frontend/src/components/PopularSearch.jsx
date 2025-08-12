import React, { useState } from 'react';
import {
  bridalWearData,
  photographyData,
  invitationsData,
  groomWearData,
  cateringData,
  mehandiData
} from '../data/vendorDataByCity';

// Map each category to its respective data array
const categoryMap = {
  'Bridal Wear': bridalWearData,
  'Photography': photographyData,
  'Invitations': invitationsData,
  'Groom Wear': groomWearData,
  'Catering Services': cateringData,
  'Mehandi Artist': mehandiData,
};

// Prepare categories for grid view
const popularItems = Object.keys(categoryMap).map(key => ({
  title: key,
  image: categoryMap[key][0]?.image || '',
}));

const PopularSearch = () => {
  const [selectedCategory, setSelectedCategory] = useState(null);

  const handleClick = (category) => {
    setSelectedCategory(category);
  };

  const vendors = categoryMap[selectedCategory] || [];

  return (
    <section
      className="bg-cover bg-center bg-no-repeat text-white py-16 px-4"
      style={{
        backgroundImage:
          "url('https://tse1.mm.bing.net/th/id/OIP.2z70OzjBnL-aI8d1RJ6hlwHaEK?pid=Api&P=0&h=220')",
      }}
    >
      <div className="bg-black bg-opacity-70 p-6 max-w-7xl mx-auto rounded-xl shadow-lg">
        <h2 className="text-3xl font-bold text-center mb-10">Popular Searches</h2>

        {/* Category Grid */}
        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-6 gap-4 mb-10">
          {popularItems.map((item, index) => (
            <div
              key={index}
              onClick={() => handleClick(item.title)}
              className={`cursor-pointer bg-white bg-opacity-80 backdrop-blur-md rounded-lg overflow-hidden shadow-md transition transform hover:scale-105 ${selectedCategory === item.title ? 'ring-2 ring-yellow-400' : ''}`}
            >
              <img src={item.image} alt={item.title} className="w-full h-24 object-cover" />
              <div className="p-2 text-center">
                <h3 className="text-sm font-semibold text-black">{item.title}</h3>
              </div>
            </div>
          ))}
        </div>

        {/* Vendor Cards */}
        {selectedCategory && (
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
            {vendors.map((vendor, idx) => (
              <div
                key={idx}
                className="bg-white bg-opacity-90 rounded-lg overflow-hidden shadow-md"
              >
                <img
                  src={vendor.image}
                  alt={vendor.name}
                  className="w-full h-48 object-cover"
                />
                <div className="p-4">
                  <h4 className="text-lg font-bold text-black">{vendor.name}</h4>
                  <p className="text-sm text-gray-700">{vendor.type}</p>
                  <div className="text-yellow-500 font-medium text-sm">
                    ⭐ {vendor.rating}
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </section>
  );
};

export default PopularSearch;
