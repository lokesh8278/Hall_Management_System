// src/components/SearchBar.jsx
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const SearchBar = () => {
  const [query, setQuery] = useState('');
  const navigate = useNavigate();

  const handleSearch = (e) => {
    e.preventDefault();
    if (query.trim() !== '') {
      navigate(`/search/${encodeURIComponent(query.trim().toLowerCase())}`);
    }
  };

  return (
    <form
      onSubmit={handleSearch}
      className="flex justify-center mt-6 bg-white-100 p-2 rounded-md max-w-2xl mx-auto"
    >
      <input
        type="text"
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        placeholder="Search for venues, services, locations..."
        className="flex-grow px-4 py-2 border border-gray-300 rounded-l-lg focus:outline-none focus:ring-2 focus:ring-yellow-500 text-black "
      />
      <button
        type="submit"
        className="px-5 py-2 bg-yellow-500 text-black rounded-r-lg hover:bg-yellow-600 transition"
      >
        Search
      </button>
    </form>
  );
};

export default SearchBar;


