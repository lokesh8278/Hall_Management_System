import React, { useEffect, useState } from "react";
import { Outlet, useNavigate, useLocation } from "react-router-dom";
import { LogOut } from "lucide-react";
import AdminSettingsDropdown from "../components/AdminSettingsDropdown";

const AdminLayout = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [adminName, setAdminName] = useState("Admin");

  useEffect(() => {
    const storedName = localStorage.getItem("adminName");
    if (storedName && storedName !== "null") {
      setAdminName(storedName.split(" ")[0]);
    }
  }, []);

  const handleLogout = () => {
    localStorage.clear();
    navigate("/admin-login");
  };

  const navLinks = [
    { label: "Home", path: "/admin/dashboard" },
    { label: "Vendors", path: "/admin/vendors" },
    { label: "Users", path: "/admin/user" },
    { label: "Bookings", path: "/admin/bookings" },
  ];

  return (
    <div className="h-screen flex flex-col bg-white text-gray-900">
      {/* ✅ Fixed Topbar */}
      <header className="sticky top-0 z-50 bg-[#1e293b] shadow-md text-white">
        <div className="flex justify-between items-center px-6 py-4">
          <h1 className="text-xl font-bold">Admin Dashboard</h1>
          <div className="flex items-center gap-4">
            <span className="text-sm">Hi, {adminName}</span>
            <AdminSettingsDropdown />
            <LogOut
              onClick={handleLogout}
              className="cursor-pointer hover:text-red-500"
            />
          </div>
        </div>
      </header>

      {/* ✅ Sticky Nav Buttons */}
      <div className="sticky top-[64px] z-40 bg-white px-6 py-3 border-b shadow-sm">
        <div className="flex justify-center gap-4">
          {navLinks.map((link) => (
            <button
              key={link.path}
              onClick={() => navigate(link.path)}
              className={`px-6 py-2 rounded-full font-semibold text-sm transition-all duration-200
                ${
                  location.pathname === link.path
                    ? "bg-gradient-to-r from-blue-500 to-indigo-600 text-white shadow-lg scale-105"
                    : "bg-gray-700 text-gray-200 hover:bg-gradient-to-r hover:from-blue-600 hover:to-indigo-500 hover:text-white"
                }`}
            >
              {link.label}
            </button>
          ))}
        </div>
      </div>

      {/* ✅ Scrollable Sticky Main Content */}
      <main className="flex-1 overflow-y-auto p-6">
        <Outlet />
      </main>
    </div>
  );
};

export default AdminLayout;
