import React from "react";
import { Outlet, useNavigate, useLocation } from "react-router-dom";
import { motion } from "framer-motion";

const VendorDashboard = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const vendorName = localStorage.getItem("vendorName") || "Vendor";
const role = localStorage.getItem("vendorRole") || "";
console.log("🧾 Vendor Role:", role); // Add this

  const vendorType = localStorage.getItem("vendorType") || "Vendor";

  const handleLogout = () => {
    localStorage.clear();
    navigate("/vendor-login");
  };

  const roleConfig = {
    ROLE_HALL_VENDOR: {
      title: "🏛 Manage Halls",
      description: "Add, edit, or delete your halls and rooms.",
      color: "bg-purple-500",
      route: "/vendor-dashboard/manage-halls",
    },
    ROLE_PHOTOGRAPHER: {
      title: "📷 Manage Photography",
      description: "Upload portfolio and set packages.",
      color: "bg-blue-500",
      route: "/vendor-dashboard/manage-photography",
    },
    ROLE_DECORATOR: {
      title: "🎀 Manage Decoration",
      description: "Add your themes and decor works.",
      color: "bg-yellow-500",
      route: "/vendor-dashboard/manage-decor",
    },
    ROLE_CATERING: {
      title: "🍽️ Manage Catering",
      description: "Add your menus, packages, and photos.",
      color: "bg-green-500",
      route: "/vendor-dashboard/manage-catering",
    },
    ROLE_MEHANDI_ARTIST: {
      title: "🌿 Manage Mehendi",
      description: "Showcase your mehendi designs and pricing.",
      color: "bg-pink-500",
      route: "/vendor-dashboard/manage-mehendi",
    },
  };

  const current = roleConfig[role];

  const isHome = location.pathname === "/vendor-dashboard";

  return (
    <div
      className="min-h-screen bg-gradient-to-br from-purple-100 via-blue-100 to-pink-100 text-gray-800"
      style={{
        backgroundImage: "url('https://www.transparenttextures.com/patterns/white-carbon.png')",
      }}
    >

      <div className="max-w-7xl mx-auto px-6 pt-12 pb-10">
        {isHome ? (
          <>
            <motion.h2
              className="text-3xl font-bold mb-10 text-center"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              transition={{ duration: 0.6 }}
            >
              🎉 Welcome, {vendorName}!
            </motion.h2>

            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-8 justify-items-center">
              <motion.div
                whileHover={{ scale: 1.05 }}
                className="bg-gray-800 text-white p-6 rounded-xl shadow-lg w-72 cursor-pointer"
                onClick={() => navigate("/vendor-dashboard/profile")}
              >
                <h3 className="text-2xl font-bold mb-2">🙋‍♂️ My Profile</h3>
                <p>Update your profile, password, and contact info.</p>
              </motion.div>

              <motion.div
                whileHover={{ scale: 1.05 }}
                className="bg-blue-500 text-white p-6 rounded-xl shadow-lg w-72 cursor-pointer"
                onClick={() => navigate("/vendor-dashboard/bookings")}
              >
                <h3 className="text-2xl font-bold mb-2">📅 My Bookings</h3>
                <p>View users who booked your halls/services.</p>
              </motion.div>

              {current && (
                <motion.div
                  whileHover={{ scale: 1.05 }}
                  className={`${current.color} text-white p-6 rounded-xl shadow-lg w-72 cursor-pointer`}
                  onClick={() => navigate(current.route)}
                >
                  <h3 className="text-2xl font-bold mb-2">{current.title}</h3>
                  <p>{current.description}</p>
                </motion.div>
              )}
            </div>
          </>
        ) : (
          <Outlet />
        )}
      </div>
    </div>
  );
};
export default VendorDashboard;
