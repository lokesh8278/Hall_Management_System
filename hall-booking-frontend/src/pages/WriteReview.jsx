import React, { useState, useEffect } from 'react';
import AOS from 'aos';
import 'aos/dist/aos.css';

const WriteReview = () => {
  const [formData, setFormData] = useState({
    name: '',
    rating: '',
    review: ''
  });

  const [submitted, setSubmitted] = useState(false);

  useEffect(() => {
    AOS.init({ duration: 800 });
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    console.log('Review submitted:', formData);
    setSubmitted(true);

    setTimeout(() => {
      setSubmitted(false);
      setFormData({ name: '', rating: '', review: '' });
    }, 3000);
  };

  return (
    <div
      className="min-h-screen bg-cover bg-center py-20 px-4"
      style={{
        backgroundImage: `linear-gradient(to right, rgba(0,0,0,0.6), rgba(0,0,0,0.6)), url('https://images.pexels.com/photos/931162/pexels-photo-931162.jpeg')`
      }}
    >
      <div
        className="max-w-2xl mx-auto bg-white bg-opacity-95 p-8 rounded-xl shadow-lg"
        data-aos="fade-up"
      >
        <h2 className="text-3xl font-extrabold mb-6 text-center text-blue-800">Write a Review</h2>

        {submitted ? (
          <div className="text-green-600 text-center font-semibold text-lg">
            ✅ Thank you for your valuable feedback!
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <label className="block mb-1 font-semibold text-gray-700">Your Name</label>
              <input
                type="text"
                name="name"
                required
                value={formData.name}
                onChange={handleChange}
                className="w-full border border-gray-300 rounded-lg p-2 focus:outline-none focus:ring focus:border-blue-400"
              />
            </div>

            <div>
              <label className="block mb-1 font-semibold text-gray-700">Rating (1 to 5)</label>
              <select
                name="rating"
                required
                value={formData.rating}
                onChange={handleChange}
                className="w-full border border-gray-300 rounded-lg p-2"
              >
                <option value="">Select Rating</option>
                {[1, 2, 3, 4, 5].map(num => (
                  <option key={num} value={num}>{num} Star{num > 1 && 's'}</option>
                ))}
              </select>
            </div>

            <div>
              <label className="block mb-1 font-semibold text-gray-700">Your Review</label>
              <textarea
                name="review"
                rows="4"
                required
                value={formData.review}
                onChange={handleChange}
                className="w-full border border-gray-300 rounded-lg p-2"
              ></textarea>
            </div>

            <button
              type="submit"
              className="bg-blue-700 text-white px-6 py-2 rounded-full font-semibold hover:bg-blue-800 transition"
            >
              Submit Review
            </button>
          </form>
        )}
      </div>
    </div>
  );
};

export default WriteReview;
