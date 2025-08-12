// ✅ FULL UPDATED Navbar.jsx
// ✅ Ensures only one dropdown (Customer/Vendor/Admin) shows at a time
// ✅ Includes 3-second delay on mouse leave

import React, { useState, useRef, useEffect } from 'react';
import { ChevronDown, MapPin } from 'lucide-react';
import { useNavigate, Link } from 'react-router-dom';

const Navbar = () => {
  const [dropdown, setDropdown] = useState({ venues: false, vendors: false, invites: false });
  const [showCustomerMenu, setShowCustomerMenu] = useState(false);
  const [showVendorMenu, setShowVendorMenu] = useState(false);
  const [showAdminMenu, setShowAdminMenu] = useState(false);
  const [isOpen, setIsOpen] = useState(false);
  const [location, setLocation] = useState("Fetching location...");
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const menuRef = useRef(null);
  const navigate = useNavigate();

  const customerTimer = useRef(null);
  const vendorTimer = useRef(null);
  const adminTimer = useRef(null);

  const rawName = localStorage.getItem('user_name');
  const userName = rawName ? rawName.split(' ')[0] : 'My Account';

  const toggleDropdown = (key) => {
    setDropdown((prev) => ({ venues: false, vendors: false, invites: false, [key]: !prev[key] }));
  };

  const scrollToSection = (id) => {
    const section = document.getElementById(id);
    if (section) {
      section.scrollIntoView({ behavior: 'smooth' });
      setIsOpen(false);
    } else {
      navigate('/');
      setTimeout(() => {
        const newSection = document.getElementById(id);
        if (newSection) newSection.scrollIntoView({ behavior: 'smooth' });
      }, 100);
    }
  };

  const getLocation = () => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        async ({ coords }) => {
          try {
            const res = await fetch(`https://nominatim.openstreetmap.org/reverse?lat=${coords.latitude}&lon=${coords.longitude}&format=json`);
            const data = await res.json();
            setLocation(data.address?.city || data.address?.town || data.address?.state || "Your Location");
          } catch {
            setLocation("Unable to fetch location");
          }
        },
        () => setLocation("Permission denied")
      );
    } else {
      setLocation("Geolocation not supported");
    }
  };

  const handleLogout = async () => {
    try {
      await fetch('http://localhost:8080/api/auth/logout', {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
      });
    } catch (err) {
      console.error('Logout failed:', err);
    }
    localStorage.clear();
    setIsLoggedIn(false);
    navigate('/customer-login');
    window.location.reload();
  };

  useEffect(() => {
    getLocation();
    const interval = setInterval(getLocation, 60000);
    return () => clearInterval(interval);
  }, []);

  useEffect(() => {
    const checkAuth = () => setIsLoggedIn(!!localStorage.getItem('token'));
    checkAuth();
    const interval = setInterval(checkAuth, 500);
    window.addEventListener('storage', checkAuth);
    return () => {
      clearInterval(interval);
      window.removeEventListener('storage', checkAuth);
    };
  }, []);

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (menuRef.current && !menuRef.current.contains(e.target)) {
        setDropdown({ venues: false, vendors: false, invites: false });
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const menuItems = {
    venues: [
      { label: 'Banquet Halls', path: '/category/banquet-halls' },
      { label: 'Lawn Venues', path: '/category/lawn-venues' },
      { label: 'Destination Weddings', path: '/category/destination-weddings' },
    ],
    vendors: [
      { label: 'Photographers', path: '/category/photographers' },
      { label: 'Decorators', path: '/category/decorators' },
      { label: 'Caterers', path: '/category/caterers' },
      { label: 'Music, DJ', path: '/category/music-dj' },
    ],
    invites: [
      { label: 'Save the Date', path: '/category/save-the-date' },
      { label: 'Reception Invite', path: '/category/reception-invite' },
      { label: 'Haldi / Mehendi', path: '/category/haldi-mehendi' },
    ],
  };

  const handleMenuEnter = (type) => {
    clearTimeout(customerTimer.current);
    clearTimeout(vendorTimer.current);
    clearTimeout(adminTimer.current);
    setShowCustomerMenu(type === 'customer');
    setShowVendorMenu(type === 'vendor');
    setShowAdminMenu(type === 'admin');
  };

  const handleMenuLeave = (type) => {
    const ref = type === 'customer' ? customerTimer : type === 'vendor' ? vendorTimer : adminTimer;
    ref.current = setTimeout(() => {
      if (type === 'customer') setShowCustomerMenu(false);
      if (type === 'vendor') setShowVendorMenu(false);
      if (type === 'admin') setShowAdminMenu(false);
    }, 3000);
  };

  return (
    <header className="sticky top-0 z-50">
      {/* top location bar */}
      <div className="bg-slate-800 text-white text-sm px-4 py-1 flex justify-between items-center">
        <div className="flex items-center gap-1">
          <MapPin className="w-4 h-4 text-white" />
          <span>{location}</span>
        </div>
        <div className="flex gap-4">
          <a href="/app/hall-booking.apk" download className="hover:underline">Download App</a>
          <Link to="/write-review" className="hover:underline">Write a Review</Link>
        </div>
      </div>

      <nav className="bg-blue-900 w-full shadow-md">
        <div className="max-w-7xl mx-auto px-4 py-2">
          <div className="flex justify-between items-center">
            <div className="flex items-center text-white font-bold text-xl">
              <span className="text-3xl mr-1">🏛️</span>
              HALL<span className="text-white">BOOKING</span>
            </div>

            <ul className="hidden md:flex gap-6 text-white font-medium items-center" ref={menuRef}>
              {Object.keys(menuItems).map((key) => (
                <li key={key} className="relative">
                  <button onClick={() => toggleDropdown(key)} className="hover:underline capitalize">
                    {key} <ChevronDown className="inline w-4 h-4" />
                  </button>
                  {dropdown[key] && (
                    <ul className="absolute top-full left-0 mt-2 w-52 bg-white text-black shadow-lg rounded-md z-50">
                      {menuItems[key].map(({ label, path }) => (
                        <li key={label}>
                          <Link to={path} className="block px-4 py-2 hover:bg-blue-100" onClick={() => setDropdown({ venues: false, vendors: false, invites: false })}>
                            {label}
                          </Link>
                        </li>
                      ))}
                    </ul>
                  )}
                </li>
              ))}

              <li onClick={() => scrollToSection('home')} className="cursor-pointer hover:underline">Home</li>
              <li onClick={() => scrollToSection('faq')} className="cursor-pointer hover:underline">FAQ</li>
              <li onClick={() => scrollToSection('popular-search')} className="cursor-pointer hover:underline">Popular</li>

              {isLoggedIn ? (
                <>
                  <li><Link to="/my-bookings" className="hover:text-blue-400">My Bookings</Link></li>
                  <li className="relative group">
                    <button className="hover:underline">{userName}</button>
                    <div className="absolute hidden group-hover:block bg-white text-black right-0 rounded shadow-md py-2 w-44 z-50">
                      <Link to="/profile" className="block px-4 py-2 hover:bg-gray-100">Profile</Link>
                      <button onClick={handleLogout} className="block w-full text-left px-4 py-2 hover:bg-gray-100 text-red-600">Logout</button>
                    </div>
                  </li>
                </>
              ) : (
                <>
                  <li className="relative" onMouseEnter={() => handleMenuEnter('customer')} onMouseLeave={() => handleMenuLeave('customer')}>
                    <span className="cursor-pointer hover:underline">Customer</span>
                    {showCustomerMenu && (
                      <ul className="absolute top-full left-0 mt-2 w-44 bg-white text-black rounded shadow z-50">
                        <Link to="/customer-login" className="block px-4 py-2 hover:bg-gray-100 rounded">Login</Link>
                        <Link to="/register-customer" className="block px-4 py-2 hover:bg-gray-100 rounded">Register</Link>
                      </ul>
                    )}
                  </li>
                  <li className="relative" onMouseEnter={() => handleMenuEnter('vendor')} onMouseLeave={() => handleMenuLeave('vendor')}>
                    <span className="cursor-pointer hover:underline">Vendor</span>
                    {showVendorMenu && (
                      <ul className="absolute top-full left-0 mt-2 w-44 bg-white text-black rounded shadow z-50">
                        <Link to="/vendor-login" className="block px-4 py-2 hover:bg-gray-100 rounded">Login</Link>
                        <Link to="/register-vendor" className="block px-4 py-2 hover:bg-gray-100 rounded">Register</Link>
                      </ul>
                    )}
                  </li>
                  <li className="relative" onMouseEnter={() => handleMenuEnter('admin')} onMouseLeave={() => handleMenuLeave('admin')}>
                    <span className="cursor-pointer hover:underline">Admin</span>
                    {showAdminMenu && (
                      <ul className="absolute top-full left-0 mt-2 w-44 bg-white text-black rounded shadow z-50">
                        <Link to="/admin-login" className="block px-4 py-2 hover:bg-gray-100 rounded">Login</Link>
                        <Link to="/admin-register" className="block px-4 py-2 hover:bg-gray-100 rounded">Register</Link>
                      </ul>
                    )}
                  </li>
                </>
              )}
            </ul>

            <div className="md:hidden">
              <button onClick={() => setIsOpen(!isOpen)} className="text-white focus:outline-none">
                <svg className="w-6 h-6" fill="none" stroke="currentColor">
                  {isOpen ? (
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
                  ) : (
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4 6h16M4 12h16M4 18h16" />
                  )}
                </svg>
              </button>
            </div>
          </div>
        </div>

        {isOpen && (
          <div className="md:hidden px-4 pb-2 bg-blue-900 text-white space-y-2">
            <Link to="/customer-login" className="block py-2">Customer Login</Link>
            <Link to="/vendor-login" className="block py-2">Vendor Login</Link>
            <Link to="/admin-login" className="block py-2">Admin Login</Link>
            <Link to="/admin-register" className="block py-2">Admin Register</Link>
            <button onClick={() => scrollToSection('home')} className="block py-2 w-full text-left">Home</button>
            <button onClick={() => scrollToSection('popular-search')} className="block py-2 w-full text-left">Popular Search</button>
            <button onClick={() => scrollToSection('register-form')} className="block py-2 w-full text-left">Register</button>
            {isLoggedIn && (
              <button onClick={handleLogout} className="block py-2 w-full text-left text-red-300">Logout</button>
            )}
          </div>
        )}
      </nav>
    </header>
  );
};

export default Navbar;