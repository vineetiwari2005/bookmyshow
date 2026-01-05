import React, { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { FaTicketAlt, FaCalendarAlt, FaClock, FaMapMarkerAlt, FaTrash, FaCheckCircle } from 'react-icons/fa';
import { QRCodeSVG } from 'qrcode.react';
import './MyBookings.scss';

const MyBookings = () => {
  const { user } = useAuth();
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [cancellingId, setCancellingId] = useState(null);

  useEffect(() => {
    if (user) {
      fetchBookings();
    }
  }, [user]);

  const fetchBookings = async () => {
    try {
      setLoading(true);
      const response = await fetch(`http://localhost:8080/ticket/user/${user.id}`);
      if (!response.ok) throw new Error('Failed to fetch bookings');
      const data = await response.json();
      // Sort by booking date (newest first)
      const sortedData = data.sort((a, b) => new Date(b.bookedAt) - new Date(a.bookedAt));
      setBookings(sortedData);
    } catch (err) {
      console.error('Error fetching bookings:', err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleCancelBooking = async (ticketId) => {
    if (!window.confirm('Are you sure you want to cancel this booking? This action cannot be undone.')) {
      return;
    }

    try {
      setCancellingId(ticketId);
      const response = await fetch(`http://localhost:8080/ticket/cancel/${ticketId}`, {
        method: 'PUT',
      });
      
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.error || 'Failed to cancel booking');
      }

      // Remove the cancelled booking from the list
      setBookings(bookings.filter(booking => booking.id !== ticketId));
      alert('Booking cancelled successfully! Refund will be processed within 5-7 business days.');
    } catch (err) {
      console.error('Error cancelling booking:', err);
      alert(`Failed to cancel booking: ${err.message}`);
    } finally {
      setCancellingId(null);
    }
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-IN', { 
      day: '2-digit', 
      month: 'short', 
      year: 'numeric' 
    });
  };

  const formatTime = (timeString) => {
    if (!timeString) return '';
    // Handle different time formats
    if (timeString.includes(':')) {
      const [hours, minutes] = timeString.split(':');
      const hour = parseInt(hours);
      const ampm = hour >= 12 ? 'PM' : 'AM';
      const displayHour = hour % 12 || 12;
      return `${displayHour}:${minutes} ${ampm}`;
    }
    return timeString;
  };

  const isPastBooking = (showDate, showTime) => {
    const now = new Date();
    const bookingDateTime = new Date(showDate);
    
    if (showTime && showTime.includes(':')) {
      const [hours, minutes] = showTime.split(':');
      bookingDateTime.setHours(parseInt(hours), parseInt(minutes));
    }
    
    return bookingDateTime < now;
  };

  if (loading) {
    return (
      <div className="my-bookings-page">
        <div className="container">
          <div className="loading-container">
            <div className="spinner"></div>
            <p>Loading your bookings...</p>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="my-bookings-page">
        <div className="container">
          <div className="error-message">
            <h3>Error Loading Bookings</h3>
            <p>{error}</p>
            <button onClick={fetchBookings} className="btn btn-primary">Try Again</button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="my-bookings-page">
      <div className="container">
        <div className="page-header">
          <h1><FaTicketAlt /> My Bookings</h1>
          <p className="subtitle">View and manage your movie bookings</p>
        </div>

        {bookings.length === 0 ? (
          <div className="no-bookings">
            <FaTicketAlt className="empty-icon" />
            <h3>No Bookings Yet</h3>
            <p>You haven't made any bookings. Start exploring movies and book your tickets!</p>
            <a href="/" className="btn btn-primary">Browse Movies</a>
          </div>
        ) : (
          <div className="bookings-grid">
            {bookings.map((booking) => {
              const isPast = isPastBooking(booking.show?.date, booking.show?.time);
              const qrData = JSON.stringify({
                bookingId: `BMS${booking.id}`,
                movieName: booking.show?.movie?.movieName,
                theaterName: booking.show?.theater?.name,
                date: booking.show?.date,
                time: booking.show?.time,
                seats: booking.bookedSeats,
                amount: booking.totalTicketsPrice,
              });

              return (
                <div key={booking.id} className={`booking-card ${isPast ? 'past-booking' : ''}`}>
                  <div className="booking-header">
                    <div className="booking-status">
                      {isPast ? (
                        <span className="status-badge completed">Completed</span>
                      ) : (
                        <span className="status-badge active">
                          <FaCheckCircle /> Active
                        </span>
                      )}
                    </div>
                    <div className="booking-id">
                      Booking ID: <strong>BMS{booking.id}</strong>
                    </div>
                  </div>

                  <div className="booking-content">
                    <div className="booking-details">
                      <h3 className="movie-title">{booking.show?.movie?.movieName}</h3>
                      
                      <div className="detail-row">
                        <FaMapMarkerAlt className="icon" />
                        <div>
                          <label>Theater</label>
                          <p>{booking.show?.theater?.name}</p>
                          <small>{booking.show?.theater?.address}</small>
                        </div>
                      </div>

                      <div className="detail-row">
                        <FaCalendarAlt className="icon" />
                        <div>
                          <label>Show Date & Time</label>
                          <p>{formatDate(booking.show?.date)} • {formatTime(booking.show?.time)}</p>
                        </div>
                      </div>

                      <div className="detail-row">
                        <FaTicketAlt className="icon" />
                        <div>
                          <label>Seats</label>
                          <div className="seat-badges">
                            {booking.bookedSeats?.split(',').filter(s => s.trim()).map((seat, idx) => (
                              <span key={idx} className="seat-badge">{seat.trim()}</span>
                            ))}
                          </div>
                        </div>
                      </div>

                      <div className="booking-price">
                        <span>Total Amount</span>
                        <strong>₹{booking.totalTicketsPrice}</strong>
                      </div>

                      <div className="booking-footer">
                        <small>Booked on: {formatDate(booking.bookedAt)}</small>
                        {!isPast && (
                          <button 
                            className="btn-cancel"
                            onClick={() => handleCancelBooking(booking.id)}
                            disabled={cancellingId === booking.id}
                          >
                            <FaTrash /> {cancellingId === booking.id ? 'Cancelling...' : 'Cancel Booking'}
                          </button>
                        )}
                      </div>
                    </div>

                    <div className="booking-qr">
                      <div className="qr-code">
                        <QRCodeSVG 
                          value={qrData}
                          size={150}
                          level="H"
                          includeMargin={true}
                        />
                      </div>
                      <p className="qr-label">Show at Theater</p>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
};

export default MyBookings;
