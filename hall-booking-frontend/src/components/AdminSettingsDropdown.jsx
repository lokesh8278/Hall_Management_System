import React, { useState, useRef, useEffect } from "react";
import { Settings } from "lucide-react";
import { useNavigate } from "react-router-dom";

const AdminSettingsDropdown = () => {
  const [open, setOpen] = useState(false);
  const dropdownRef = useRef();
  const navigate = useNavigate();

  const toggleDropdown = () => setOpen(!open);

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const logout = () => {
    localStorage.removeItem("token");
    navigate("/admin-login");
  };

  return (
    <div className="relative inline-block" ref={dropdownRef}>
      <Settings
        className="cursor-pointer hover:text-blue-400"
        onClick={toggleDropdown}
      />
      {open && (
        <div className="absolute right-0 mt-2 w-52 bg-white text-black shadow-lg rounded z-50">
          <ul className="py-1">
            <li
              className="px-4 py-2 hover:bg-gray-100 cursor-pointer"
              onClick={() => navigate("/admin/edit-profile")}
            >
              ✏️ Edit Profile
            </li>
           
            <li
              className="px-4 py-2 hover:bg-gray-100 cursor-pointer"
              onClick={() => alert("🔔 Notification settings coming soon")}
            >
              🔔 Notification Settings
            </li>
            <li
              className="px-4 py-2 hover:bg-gray-100 cursor-pointer"
              onClick={() => alert("🧑‍💼 Role management coming soon")}
            >
              🧑‍💼 Manage Roles
            </li>
            <li
              className="px-4 py-2 hover:bg-gray-100 cursor-pointer"
              onClick={() => alert("📜 Audit log view coming soon")}
            >
              📜 Audit Logs
            </li>
            <li
              className="px-4 py-2 hover:bg-red-100 text-red-600 cursor-pointer"
              onClick={logout}
            >
              🚪 Logout
            </li>
          </ul>
        </div>
      )}
    </div>
  );
};

export default AdminSettingsDropdown;
