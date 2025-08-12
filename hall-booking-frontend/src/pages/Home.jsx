import React, { useEffect } from 'react';
import PopularSearch from '../components/PopularSearch';
import RegisterForm from './RegisterCustomer';
import LocationBar from '../components/LocationBar';
import SearchBar from '../components/SearchBar'; // ✅ Add SearchBar import
import AOS from 'aos';
import 'aos/dist/aos.css';

const Home = () => {
  useEffect(() => {
    AOS.init({ duration: 1000, once: true });
  }, []);

  return (
    <div className="min-h-screen bg-gray-200 scroll-smooth">
      <LocationBar />

      {/* Hero Section */}
      <div
        className="h-[80vh] bg-cover bg-center flex flex-col items-center justify-center text-white"
        style={{
          backgroundImage: `url('https://img.freepik.com/premium-photo/fundraising-event_78899-10475.jpg')`,
        }}
      >
        <div className="bg-black bg-opacity-50 p-8 rounded-lg text-center animate-fade-in-up transition-all duration-700 ease-out">
          <h2 className="text-4xl font-bold mb-4">A ROYAL OASIS OF LUXURY FOR YOUR CELEBRATIONS</h2>
          <p className="text-lg mb-6">Explore venues, vendors, and more — all in one place.</p>

          {/* ✅ Search bar placed here instead of "Book Now" */}
          <div className="w-full max-w-xl mx-auto">
            <SearchBar />
          </div>
        </div>
      </div>

      {/* Popular Searches */}
      <div id="popular-search" className="p-10 bg-gray-100">
        <h1 className="text-3xl font-bold text-center mb-10" data-aos="fade-up">POPULAR SEARCH</h1>
        <PopularSearch />
      </div>

      {/* Register Section */}
      <div
        id="register"
        className="p-10 bg-cover bg-center text-white"
        style={{
          backgroundImage: `url('https://img.freepik.com/premium-photo/wedding-ceremony-event-decorated-with-flowers-white-archway_743855-3384.jpg')`,
        }}
      >
        <h2 className="text-3xl font-bold text-center mb-6">REGISTER WITH US</h2>
        <div className="bg-white bg-opacity-80 rounded-lg p-6 max-w-3xl mx-auto">
          <RegisterForm />
        </div>
      </div>

      {/* Testimonials */}
      <section className="p-10 bg-gray-100 text-center">
        <h2 className="text-3xl font-bold mb-6" data-aos="fade-up">Happy Couples</h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
          {[
            { text: "The best venue experience! Easy booking and perfect service.", name: "– Priya Sharma" },
            { text: "Beautiful locations and great service. Highly recommended!", name: "– Rohit & Anjali" },
            { text: "A seamless experience with great support throughout.", name: "– Arjun Patel" },
          ].map((testimonial, i) => (
            <div className="bg-white p-6 rounded-lg shadow" data-aos="fade-up" data-aos-delay={i * 100} key={i}>
              <p>"{testimonial.text}"</p>
              <p className="text-sm mt-2 text-gray-600">{testimonial.name}</p>
            </div>
          ))}
        </div>
      </section>

      {/* Venue Section */}
      <section id="venues" className="p-10 bg-white text-center" data-aos="fade-right">
        <h3 className="text-3xl font-bold text-gray-800 mb-4">Top Wedding Venues</h3>
        <p className="text-gray-600">Browse and book the best venues in your city at affordable prices.</p>
      </section>

      {/* FAQ */}
      <section className="p-10 bg-gray-50" data-aos="fade-up">
        <h2 className="text-2xl font-bold mb-6 text-center">FAQ</h2>
        <div className="max-w-3xl mx-auto space-y-4">
          <details className="bg-white p-4 rounded shadow">
            <summary className="cursor-pointer font-medium">Can I cancel a booking?</summary>
            <p className="mt-2 text-gray-600">Yes, but cancellation policies vary per venue/vendor.</p>
          </details>
          <details className="bg-white p-4 rounded shadow">
            <summary className="cursor-pointer font-medium">Are there any hidden charges?</summary>
            <p className="mt-2 text-gray-600">No, all prices are transparent before confirmation.</p>
          </details>
        </div>
      </section>

      {/* Newsletter */}
      <section className="p-10 bg-yellow-100 text-center" data-aos="fade-up">
        <h3 className="text-2xl font-bold mb-4">Stay Updated!</h3>
        <p className="mb-4 text-gray-700">Subscribe for wedding planning tips & venue deals</p>
        <input type="email" placeholder="Enter your email" className="p-2 rounded-l border border-gray-300" />
        <button className="p-2 bg-yellow-600 text-white rounded-r">Subscribe</button>
      </section>

      {/* Google Map */}
      <div className="px-4 py-10 bg-white" data-aos="fade-up">
        <h3 className="text-2xl font-semibold text-center mb-4">Our Office Location</h3>
        <iframe
          className="w-full h-64 mt-4 rounded-lg"
          src="https://www.google.com/maps/embed?pb=YOUR_MAP_EMBED_LINK_HERE"
          loading="lazy"
          allowFullScreen
          title="Google Map"
        ></iframe>
      </div>

      {/* WhatsApp Button */}
      <a
        href="https://wa.me/91-9618127997"
        className="fixed bottom-5 right-5 z-50 bg-green-500 p-3 rounded-full shadow-lg hover:bg-green-600"
        target="_blank"
        rel="noopener noreferrer"
      >
        <img
          src="https://img.icons8.com/ios-filled/50/ffffff/whatsapp.png"
          alt="Chat"
          className="w-6 h-6"
        />
      </a>

      {/* Terms and Conditions */}
      <div id="terms" className="p-10 bg-gray-100 text-gray-800 text-justify" data-aos="fade-up">
        <div className="max-w-4xl mx-auto">
          <h3 className="text-3xl font-bold mb-4 text-center">Terms and Conditions</h3>
          <ul className="list-disc pl-6 space-y-2">
            <li>All bookings are subject to availability and confirmation.</li>
            <li>Payments made are non-refundable unless specified otherwise.</li>
            <li>We are not liable for any third-party vendor services.</li>
            <li>Users must provide accurate booking details during registration.</li>
            <li>Any misuse of the platform will result in account termination.</li>
          </ul>
        </div>
      </div>
    </div>
  );
};

export default Home;
