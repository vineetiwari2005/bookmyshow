import React from 'react';
import { useAuth } from '../../context/AuthContext';
import { FaUser, FaEnvelope, FaPhone, FaWallet, FaUserShield, FaTicketAlt, FaEdit } from 'react-icons/fa';
import { Link } from 'react-router-dom';
import './Profile.scss';

const Profile = () => {
  const { user } = useAuth();

  if (!user) {
    return (
      <div className="profile-page">
        <div className="container">
          <div className="error-message">
            <h3>Please login to view your profile</h3>
            <Link to="/login" className="btn btn-primary">Login</Link>
          </div>
        </div>
      </div>
    );
  }

  const getRoleColor = (role) => {
    switch(role?.toUpperCase()) {
      case 'ADMIN': return '#e74c3c';
      case 'USER': return '#3498db';
      default: return '#95a5a6';
    }
  };

  return (
    <div className="profile-page">
      <div className="profile-banner">
        <div className="banner-overlay"></div>
        <div className="container">
          <div className="profile-header">
            <div className="profile-avatar">
              <div className="avatar-circle">
                <FaUser />
              </div>
              <div className="avatar-badge" style={{ background: getRoleColor(user.role) }}>
                <FaUserShield />
              </div>
            </div>
            <div className="profile-title">
              <h1>{user.name}</h1>
              <p className="user-email">{user.email}</p>
              <span className="role-badge" style={{ background: getRoleColor(user.role) }}>
                {user.role || 'USER'}
              </span>
            </div>
          </div>
        </div>
      </div>

      <div className="container">
        <div className="profile-content">
          <div className="info-section">
            <div className="section-header">
              <h2>Personal Information</h2>
              <button className="btn-edit">
                <FaEdit /> Edit Profile
              </button>
            </div>

            <div className="info-grid">
              <div className="info-card">
                <div className="info-icon" style={{ background: '#3498db20', color: '#3498db' }}>
                  <FaUser />
                </div>
                <div className="info-details">
                  <label>Full Name</label>
                  <p>{user.name || 'Not provided'}</p>
                </div>
              </div>

              <div className="info-card">
                <div className="info-icon" style={{ background: '#e74c3c20', color: '#e74c3c' }}>
                  <FaEnvelope />
                </div>
                <div className="info-details">
                  <label>Email Address</label>
                  <p>{user.email}</p>
                </div>
              </div>

              <div className="info-card">
                <div className="info-icon" style={{ background: '#2ecc7120', color: '#2ecc71' }}>
                  <FaPhone />
                </div>
                <div className="info-details">
                  <label>Mobile Number</label>
                  <p>{user.mobileNumber || 'Not provided'}</p>
                </div>
              </div>

              <div className="info-card">
                <div className="info-icon" style={{ background: '#f39c1220', color: '#f39c12' }}>
                  <FaUserShield />
                </div>
                <div className="info-details">
                  <label>Account Role</label>
                  <p style={{ textTransform: 'uppercase', fontWeight: '600', color: getRoleColor(user.role) }}>
                    {user.role || 'USER'}
                  </p>
                </div>
              </div>
            </div>
          </div>

          <div className="wallet-section">
            <div className="wallet-card">
              <div className="wallet-header">
                <div className="wallet-icon">
                  <FaWallet />
                </div>
                <div>
                  <h3>Wallet Balance</h3>
                  <p className="wallet-subtitle">Available credits</p>
                </div>
              </div>
              <div className="wallet-balance">
                <span className="currency">₹</span>
                <span className="amount">{user.walletBalance || 0}</span>
              </div>
              <div className="wallet-actions">
                <button className="btn btn-primary btn-wallet">Add Money</button>
                <button className="btn btn-outline">Transaction History</button>
              </div>
            </div>
          </div>

          <div className="quick-actions">
            <h2>Quick Actions</h2>
            <div className="actions-grid">
              <Link to="/my-bookings" className="action-card">
                <div className="action-icon" style={{ background: '#9b59b620', color: '#9b59b6' }}>
                  <FaTicketAlt />
                </div>
                <div className="action-details">
                  <h4>My Bookings</h4>
                  <p>View all your bookings</p>
                </div>
              </Link>

              <div className="action-card">
                <div className="action-icon" style={{ background: '#3498db20', color: '#3498db' }}>
                  <FaEdit />
                </div>
                <div className="action-details">
                  <h4>Edit Profile</h4>
                  <p>Update your information</p>
                </div>
              </div>

              <div className="action-card">
                <div className="action-icon" style={{ background: '#f39c1220', color: '#f39c12' }}>
                  <FaWallet />
                </div>
                <div className="action-details">
                  <h4>Wallet</h4>
                  <p>Manage your wallet</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Profile;
