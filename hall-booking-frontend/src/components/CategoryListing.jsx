import React from 'react';

const CategoryListing = ({ data, title }) => {
  return (
    <section className="py-10 px-4">
      <h2 className="text-3xl font-bold text-center mt-16 mb-8">{title}</h2>

      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6 max-w-7xl mx-auto">
        {data.map((item, index) => (
          <div key={index} className="border rounded-lg shadow-lg overflow-hidden bg-white">
            <img src={item.image} alt={item.name} className="w-full h-64 object-cover" />
            <div className="p-4">
              <h3 className="text-xl font-semibold">{item.name}</h3>
              <p className="text-sm text-gray-500">{item.location}</p>
              <p className="text-pink-600 font-bold mt-2">{item.price}</p>
              <p className="text-yellow-600 mt-1">⭐ {item.rating} ({item.reviews} reviews)</p>
              <div className="mt-3 flex flex-wrap gap-2">
                {item.tags.map((tag, idx) => (
                  <span key={idx} className="bg-gray-200 text-sm px-2 py-1 rounded-full">{tag}</span>
                ))}
              </div>
            </div>
          </div>
        ))}
      </div>
    </section>
  );
};

export default CategoryListing;
