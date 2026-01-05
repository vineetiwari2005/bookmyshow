import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { AppProvider } from './context/AppContext';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

// Layout Components
import Navbar from './components/Layout/Navbar';
import Footer from './components/Layout/Footer';

// Pages
import Home from './pages/Home/Home';
import Login from './pages/Auth/Login';
import Signup from './pages/Auth/Signup';
import MovieDetails from './pages/Movie/MovieDetails';
import ShowSelection from './pages/Booking/ShowSelection';
import SeatSelection from './pages/Booking/SeatSelection';
import Payment from './pages/Booking/Payment';
import BookingConfirmation from './pages/Booking/BookingConfirmation';
import MyBookings from './pages/User/MyBookings';
import Profile from './pages/User/Profile';
import AdminDashboard from './pages/Admin/AdminDashboard';
import TheaterOwnerDashboard from './pages/TheaterOwner/TheaterOwnerDashboard';
import NotFound from './pages/NotFound';
import TestPage from './pages/TestPage';

// Protected Route Component
import ProtectedRoute from './components/ProtectedRoute';

import './styles/global.scss';

function App() {
  console.log('📱 App component rendering...');
  
  return (
    <Router>
      <AuthProvider>
        <AppProvider>
          <div className="app">
            <Navbar />
            <main className="main-content">
              <Routes>
                {/* Test Route */}
                <Route path="/test" element={<TestPage />} />
                
                {/* Public Routes */}
                <Route path="/" element={<Home />} />
                <Route path="/login" element={<Login />} />
                <Route path="/signup" element={<Signup />} />
                <Route path="/movie/:movieName" element={<MovieDetails />} />
                
                {/* Protected User Routes */}
                <Route
                  path="/movie/:movieName/shows"
                  element={
                    <ProtectedRoute>
                      <ShowSelection />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/booking/:showId/seats"
                  element={
                    <ProtectedRoute>
                      <SeatSelection />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/booking/payment"
                  element={
                    <ProtectedRoute>
                      <Payment />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/booking/confirmation/:ticketId"
                  element={
                    <ProtectedRoute>
                      <BookingConfirmation />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/my-bookings"
                  element={
                    <ProtectedRoute>
                      <MyBookings />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/profile"
                  element={
                    <ProtectedRoute>
                      <Profile />
                    </ProtectedRoute>
                  }
                />
                
                {/* Admin Routes */}
                <Route
                  path="/admin/*"
                  element={
                    <ProtectedRoute requireRole="ADMIN">
                      <AdminDashboard />
                    </ProtectedRoute>
                  }
                />
                
                {/* Theater Owner Routes */}
                <Route
                  path="/theater-owner/*"
                  element={
                    <ProtectedRoute requireRole="THEATER_OWNER">
                      <TheaterOwnerDashboard />
                    </ProtectedRoute>
                  }
                />
                
                {/* 404 */}
                <Route path="*" element={<NotFound />} />
              </Routes>
            </main>
            <Footer />
            <ToastContainer 
              position="top-right"
              autoClose={3000}
              hideProgressBar={false}
              newestOnTop={true}
              closeOnClick
              rtl={false}
              pauseOnFocusLoss
              draggable
              pauseOnHover
              theme="colored"
            />
          </div>
        </AppProvider>
      </AuthProvider>
    </Router>
  );
}

export default App;
