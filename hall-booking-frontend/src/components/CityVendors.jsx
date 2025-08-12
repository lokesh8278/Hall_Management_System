import React from 'react';
import { useParams } from 'react-router-dom';
import vendorDataByCity from '../data/vendorDataByCity'; // ✅ Import the combined vendor data

const CityVendors = () => {
  const { city } = useParams();
  const vendors = vendorDataByCity[city] || [];

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">Vendors in {city}</h1>

      {vendors.length === 0 ? (
        <p className="text-gray-600">
          No vendors found in <strong>{city}</strong>.
        </p>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
          {vendors.map((vendor, index) => (
            <div key={index} className="bg-white rounded-lg shadow-md overflow-hidden">
              <img
                src={vendor.image}
                alt={vendor.name}
                className="h-48 w-full object-cover"
              />
              <div className="p-4">
                <h2 className="text-lg font-semibold text-gray-800">{vendor.name}</h2>
                <p className="text-sm text-gray-500">{vendor.type}</p>
                <p className="text-yellow-500 font-medium mt-1">⭐ {vendor.rating}</p>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default CityVendors;
