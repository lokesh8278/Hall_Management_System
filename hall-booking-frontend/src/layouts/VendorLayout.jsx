import React, { useEffect, useState } from "react";
import { Outlet, useLocation, useNavigate } from "react-router-dom";
import { LogOut } from "lucide-react";

const VendorLayout = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const [vendorName, setVendorName] = useState("Vendor");
  const [vendorRole, setVendorRole] = useState("");

  useEffect(() => {
    const name = localStorage.getItem("vendorName");
    const role = localStorage.getItem("vendorRole");
    if (name) setVendorName(name.split(" ")[0]);
    if (role) setVendorRole(role);
  }, []);

  const handleLogout = () => {
    localStorage.clear();
    navigate("/vendor-login");
  };

  const roleConfig = {
    ROLE_HALL_VENDOR: [
      { label: "Home", path: "/vendor-dashboard" },
      { label: "Profile", path: "/vendor-dashboard/profile" },
      { label: "Bookings", path: "/vendor-dashboard/bookings" },
      { label: "Manage Halls", path: "/vendor-dashboard/manage-halls" },
    ],
    ROLE_PHOTOGRAPHER: [
      { label: "Home", path: "/vendor-dashboard" },
      { label: "Profile", path: "/vendor-dashboard/profile" },
      { label: "Bookings", path: "/vendor-dashboard/bookings" },
      { label: "Manage Photography", path: "/vendor-dashboard/manage-photography" },
    ],
    ROLE_CATERING: [
      { label: "Home", path: "/vendor-dashboard" },
      { label: "Profile", path: "/vendor-dashboard/profile" },
      { label: "Bookings", path: "/vendor-dashboard/bookings" },
      { label: "Manage Catering", path: "/vendor-dashboard/manage-catering" },
    ],
    ROLE_DECORATOR: [
      { label: "Home", path: "/vendor-dashboard" },
      { label: "Profile", path: "/vendor-dashboard/profile" },
      { label: "Bookings", path: "/vendor-dashboard/bookings" },
      { label: "Manage Decoration", path: "/vendor-dashboard/manage-decor" },
    ],
    ROLE_MEHANDI_ARTIST: [
      { label: "Home", path: "/vendor-dashboard" },
      { label: "Profile", path: "/vendor-dashboard/profile" },
      { label: "Bookings", path: "/vendor-dashboard/bookings" },
      { label: "Manage Mehendi", path: "/vendor-dashboard/manage-mehendi" },
    ],
  };

  const navLinks = roleConfig[vendorRole] || [];

  return (
    <div className="h-screen flex flex-col bg-white text-gray-900">
      {/* ✅ Topbar */}
      <header className="sticky top-0 z-50 bg-[#1e293b] shadow-md text-white">
        <div className="flex justify-between items-center px-6 py-4">
          <h1 className="text-xl font-bold">Vendor Dashboard</h1>
          <div className="flex items-center gap-4">
            <span className="text-sm">Hi, {vendorName}</span>
            <LogOut
              onClick={handleLogout}
              className="cursor-pointer hover:text-red-500"
            />
          </div>
        </div>
      </header>

      {/* ✅ Nav Buttons */}
      <div className="sticky top-[64px] z-40 bg-white px-6 py-3 border-b shadow-sm">
        <div className="flex justify-center gap-4">
          {navLinks.map((link) => (
            <button
              key={link.path}
              onClick={() => navigate(link.path)}
              className={`px-6 py-2 rounded-full font-semibold text-sm transition-all duration-200
                ${
                  location.pathname === link.path
                    ? "bg-gradient-to-r from-purple-500 to-pink-500 text-white shadow-lg scale-105"
                    : "bg-gray-700 text-gray-200 hover:bg-gradient-to-r hover:from-purple-600 hover:to-pink-500 hover:text-white"
                }`}
            >
              {link.label}
            </button>
          ))}
        </div>
      </div>

      {/* ✅ Main content */}
      <main className="flex-1 overflow-y-auto p-6">
        <Outlet />
      </main>
    </div>
  );
};

export default VendorLayout;
