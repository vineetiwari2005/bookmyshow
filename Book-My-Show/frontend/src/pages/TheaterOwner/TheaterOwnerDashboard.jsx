import React from 'react';
import { Routes, Route } from 'react-router-dom';

const TheaterOwnerDashboard = () => {
  return (
    <div className="container" style={{padding: '40px', minHeight: '400px'}}>
      <h2>Theater Owner Dashboard</h2>
      <p>Manage your theaters, screens, shows, and view revenue reports</p>
      
      <div style={{display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '20px', marginTop: '30px'}}>
        <div style={{padding: '20px', background: '#fff', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.1)'}}>
          <h3>My Theaters</h3>
          <p style={{fontSize: '2rem', color: '#f84464'}}>5</p>
        </div>
        <div style={{padding: '20px', background: '#fff', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.1)'}}>
          <h3>Total Screens</h3>
          <p style={{fontSize: '2rem', color: '#3498db'}}>28</p>
        </div>
        <div style={{padding: '20px', background: '#fff', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.1)'}}>
          <h3>Today's Shows</h3>
          <p style={{fontSize: '2rem', color: '#e67e22'}}>84</p>
        </div>
        <div style={{padding: '20px', background: '#fff', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.1)'}}>
          <h3>Monthly Revenue</h3>
          <p style={{fontSize: '2rem', color: '#27ae60'}}>₹2,45,000</p>
        </div>
      </div>
      
      <Routes>
        <Route path="/" element={<div></div>} />
        <Route path="/theaters" element={<div style={{marginTop: '40px'}}><h3>Theater Management</h3></div>} />
        <Route path="/shows" element={<div style={{marginTop: '40px'}}><h3>Show Management</h3></div>} />
        <Route path="/reports" element={<div style={{marginTop: '40px'}}><h3>Revenue Reports</h3></div>} />
      </Routes>
    </div>
  );
};

export default TheaterOwnerDashboard;
