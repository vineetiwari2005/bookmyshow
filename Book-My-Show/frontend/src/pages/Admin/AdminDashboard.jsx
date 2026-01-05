import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { adminStats } from '../../mockData/indianData';

const AdminDashboard = () => {
  return (
    <div className="container" style={{padding: '40px', minHeight: '400px'}}>
      <h2>Admin Dashboard</h2>
      <div style={{display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '20px', marginTop: '30px'}}>
        <div style={{padding: '20px', background: '#fff', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.1)'}}>
          <h3>Total Bookings</h3>
          <p style={{fontSize: '2rem', color: '#f84464'}}>{adminStats.totalBookings}</p>
        </div>
        <div style={{padding: '20px', background: '#fff', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.1)'}}>
          <h3>Total Revenue</h3>
          <p style={{fontSize: '2rem', color: '#27ae60'}}>₹{adminStats.totalRevenue.toLocaleString()}</p>
        </div>
        <div style={{padding: '20px', background: '#fff', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.1)'}}>
          <h3>Total Users</h3>
          <p style={{fontSize: '2rem', color: '#3498db'}}>{adminStats.totalUsers}</p>
        </div>
        <div style={{padding: '20px', background: '#fff', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.1)'}}>
          <h3>Active Movies</h3>
          <p style={{fontSize: '2rem', color: '#e67e22'}}>{adminStats.activeMovies}</p>
        </div>
      </div>
      
      <div style={{marginTop: '40px'}}>
        <h3>Popular Movies</h3>
        <div style={{background: '#fff', padding: '20px', borderRadius: '12px', marginTop: '20px'}}>
          {adminStats.popularMovies.map((movie, index) => (
            <div key={index} style={{padding: '10px', borderBottom: '1px solid #eee'}}>
              <strong>{movie.movieName || movie.name}</strong> - {movie.bookings} bookings
            </div>
          ))}
        </div>
      </div>
      
      <Routes>
        <Route path="/" element={<div></div>} />
        <Route path="/users" element={<div style={{marginTop: '40px'}}><h3>User Management</h3></div>} />
        <Route path="/movies" element={<div style={{marginTop: '40px'}}><h3>Movie Management</h3></div>} />
        <Route path="/theaters" element={<div style={{marginTop: '40px'}}><h3>Theater Management</h3></div>} />
      </Routes>
    </div>
  );
};

export default AdminDashboard;
