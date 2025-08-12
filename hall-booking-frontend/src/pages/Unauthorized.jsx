import React from 'react';
import { Link } from 'react-router-dom';

const Unauthorized = () => {
  return (
    <div className="text-center mt-20">
      <h2 className="text-3xl font-bold text-red-600">🚫 Unauthorized</h2>
      <p className="mt-4 text-gray-700">You are not authorized to view this page.</p>
      <Link to="/" className="mt-4 inline-block text-blue-500 underline">
        Go back to Home
      </Link>
    </div>
  );
};

export default Unauthorized;
