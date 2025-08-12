// src/pages/SearchResults.jsx
import React from 'react';
import { useParams } from 'react-router-dom';

const SearchResults = () => {
  const { query } = useParams();

  return (
    <div className="p-6">
      <h2 className="text-2xl font-semibold mb-4">Search Results for "{query}"</h2>
      {/* You can now filter or search through vendors/halls/services here */}
      {/* Example: filter through vendorDataByCity to find matches */}
    </div>
  );
};

export default SearchResults;
