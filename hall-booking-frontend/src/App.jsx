// Updated App.jsx with ManageRooms route
import React from 'react';
import { BrowserRouter as Router, Routes, Route, useLocation } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

import Navbar from './components/Navbar';
import ChatWidget from './components/ChatWidget';

import Home from './pages/Home';
import RegisterCustomer from './pages/RegisterCustomer';
import CustomerLogin from './pages/CustomerLogin';
import CustomerDashboard from './pages/CustomerDashboard';
import RegisterVendor from './pages/RegisterVendor';
import VendorLogin from './pages/VendorLogin';
import PopularSearch from './components/PopularSearch';
import CategoryListing from './components/CategoryListing';
import WriteReview from './pages/WriteReview';
import CityVendors from './components/CityVendors';
import SearchResults from './pages/SearchResults';
import ProtectedAdminRoute from './components/ProtectedAdminRoute';

import ForgotPassword from './pages/ForgotPassword';
import VerifyOtp from './pages/VerifyOtp';
import ResetPassword from './pages/ResetPassword';

import AdminDashboard from './pages/AdminDashboard';
import AdminVendors from './pages/AdminVendors';
import AdminLogin from './pages/AdminLogin';
import AdminRegister from './pages/AdminRegister';
import AdminForgotPassword from './pages/AdminForgotPassword';
import AdminResetPassword from './pages/AdminResetPassword';

import BanquetHallList from './pages/BanquetHallList';
import ViewHall from './pages/ViewHall';
import BookHall from './pages/BookHall';
import MyBookings from './pages/MyBookings';
import VendorDashboard from './pages/VendorDashboard';
import CustomerProfile from './pages/CustomerProfile';

import AdminLayout from './layouts/AdminLayout';
import EditProfile from './pages/EditProfile';
import AdminUserList from './pages/AdminUserList';
import AdminRoute from './components/AdminRoute';
import AdminAllBookings from './pages/AdminAllBookings';

import VendorLayout from './layouts/VendorLayout';
import VendorProfile from './pages/VendorProfile';
import VendorBookings from './pages/VendorBookings';
import ManageHalls from './pages/ManageHalls';
import AddHall from './pages/AddHall';
import ManageRooms from './pages/ManageRooms';

import {
  bridalWearData,
  photographyData,
  invitationsData,
  groomWearData,
  cateringData,
  mehandiData,
} from './data/categoryData';

const AppRoutes = () => {
  const location = useLocation();
  const hiddenNavbarRoutes = [
    '/admin',
    '/vendor-dashboard',
    '/customer-dashboard',
  ];

  const shouldHideNavbar = hiddenNavbarRoutes.some((path) =>
    location.pathname.startsWith(path)
  );

  return (
    <>
      {!shouldHideNavbar && <Navbar />}

      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/search/:query" element={<SearchResults />} />
        <Route path="/popular-search" element={<PopularSearch />} />
        <Route path="/category/bridal-wear" element={<CategoryListing data={bridalWearData} title="Bridal Wear" />} />
        <Route path="/category/photography" element={<CategoryListing data={photographyData} title="Photography" />} />
        <Route path="/category/invitations" element={<CategoryListing data={invitationsData} title="Invitations" />} />
        <Route path="/category/groom-wear" element={<CategoryListing data={groomWearData} title="Groom Wear" />} />
        <Route path="/category/catering-services" element={<CategoryListing data={cateringData} title="Catering Services" />} />
        <Route path="/category/mehandi-artist" element={<CategoryListing data={mehandiData} title="Mehandi Artist" />} />
        <Route path="/category/banquet-halls" element={<BanquetHallList />} />
        <Route path="/vendors/:city" element={<CityVendors />} />

        <Route path="/register-customer" element={<RegisterCustomer />} />
        <Route path="/customer-login" element={<CustomerLogin />} />
        <Route path="/customer-dashboard" element={<CustomerDashboard />} />
        <Route path="/my-bookings" element={<MyBookings />} />
        <Route path="/register-vendor" element={<RegisterVendor />} />
        <Route path="/vendor-login" element={<VendorLogin />} />
        <Route path="/vendor-dashboard" element={<VendorDashboard />} />

        <Route path="/forgot-password" element={<ForgotPassword />} />
        <Route path="/verify-otp" element={<VerifyOtp />} />
        <Route path="/reset-password" element={<ResetPassword />} />

        <Route path="/admin-login" element={<AdminLogin />} />
        <Route path="/admin-register" element={<AdminRegister />} />
        <Route path="/admin/forgot-password" element={<AdminForgotPassword />} />
        <Route path="/admin/reset-password" element={<AdminResetPassword />} />

        <Route path="/admin" element={<ProtectedAdminRoute />}>
          <Route element={<AdminLayout />}>
            <Route path="dashboard" element={<AdminDashboard />} />
            <Route path="vendors" element={<AdminVendors />} />
            <Route path="user" element={<AdminRoute><AdminUserList /></AdminRoute>} />
            <Route path="bookings" element={<AdminRoute><AdminAllBookings /></AdminRoute>} />
          </Route>
        </Route>

        <Route path="/admin/edit-profile" element={<EditProfile />} />
        <Route path="/hall/:id" element={<ViewHall />} />
        <Route path="/hall/:id/book" element={<BookHall />} />
        <Route path="/profile" element={<CustomerProfile />} />
        <Route path="/write-review" element={<WriteReview />} />
        <Route path="*" element={<h2 className="p-6 text-center">404 - Page Not Found</h2>} />

        <Route path="/vendor-dashboard" element={<VendorLayout />}>
          <Route index element={<VendorDashboard />} />
          <Route path="profile" element={<VendorProfile />} />
          <Route path="bookings" element={<VendorBookings />} />
          <Route path="manage-halls" element={<ManageHalls />} />
          <Route path="addHall" element={<AddHall />} />
          <Route path="manage-halls/rooms/:hallId" element={<ManageRooms />} />
        </Route>
      </Routes>

      {!shouldHideNavbar && <ChatWidget />}
      <ToastContainer />
    </>
  );
};

function App() {
  return (
    <Router>
      <AppRoutes />
    </Router>
  );
}

export default App;
