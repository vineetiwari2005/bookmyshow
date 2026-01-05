import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { QRCodeSVG } from 'qrcode.react';
import { FaCheckCircle, FaDownload, FaHome, FaCalendar, FaClock, FaMapMarkerAlt, FaTicketAlt, FaRupeeSign, FaEnvelope } from 'react-icons/fa';
import { MdEventSeat } from 'react-icons/md';
import { authService } from '../../services';
import './BookingConfirmation.scss';

const BookingConfirmation = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { paymentIntentId } = useParams();
  const bookingData = location.state;
  const user = authService.getCurrentUser();

  const [bookingId] = useState(`BMS${Date.now()}${Math.floor(Math.random() * 1000)}`);
  const [currentTime] = useState(new Date().toLocaleString());

  useEffect(() => {
    // Scroll to top on mount
    window.scrollTo(0, 0);

    // If no booking data, redirect to home
    if (!bookingData) {
      setTimeout(() => navigate('/'), 3000);
    }
  }, [bookingData, navigate]);

  const handleDownload = () => {
    // Create a printable version
    window.print();
  };

  const handleEmail = () => {
    alert('Confirmation email sent to ' + (bookingData?.userEmail || user?.email || 'your email'));
  };

  if (!bookingData) {
    return (
      <div className="booking-confirmation-page">
        <div className="container">
          <div className="error-state">
            <h3>No booking details found</h3>
            <p>Redirecting to home...</p>
          </div>
        </div>
      </div>
    );
  }

  const qrData = JSON.stringify({
    bookingId: bookingId,
    movieName: bookingData.movieName,
    theaterName: bookingData.theaterName,
    showDate: bookingData.showDate,
    showTime: bookingData.showTime,
    seats: bookingData.selectedSeats,
    totalAmount: bookingData.totalAmount,
    transactionId: bookingData.transactionId || paymentIntentId
  });

  return (
    <div className="booking-confirmation-page">
      <div className="container">
        {/* Success Header */}
        <div className="success-header">
          <div className="success-icon">
            <FaCheckCircle />
          </div>
          <h1>Booking Confirmed!</h1>
          <p>Your tickets have been booked successfully</p>
          <div className="booking-id">
            <strong>Booking ID:</strong> {bookingId}
          </div>
        </div>

        {/* Ticket Details Card */}
        <div className="ticket-card">
          <div className="ticket-content">
            {/* Left Side - Details */}
            <div className="ticket-details">
              <div className="movie-header">
                <h2>{bookingData.movieName}</h2>
                <span className="rating">U/A</span>
              </div>

              <div className="detail-row">
                <FaMapMarkerAlt className="icon" />
                <div>
                  <label>Theater</label>
                  <p>{bookingData.theaterName}</p>
                </div>
              </div>

              <div className="detail-row">
                <FaCalendar className="icon" />
                <div>
                  <label>Date</label>
                  <p>{new Date(bookingData.showDate).toLocaleDateString('en-US', { 
                    weekday: 'long', 
                    year: 'numeric', 
                    month: 'long', 
                    day: 'numeric' 
                  })}</p>
                </div>
              </div>

              <div className="detail-row">
                <FaClock className="icon" />
                <div>
                  <label>Show Time</label>
                  <p>{bookingData.showTime}</p>
                </div>
              </div>

              <div className="detail-row">
                <MdEventSeat className="icon" />
                <div>
                  <label>Seats ({bookingData.selectedSeats?.length || 0})</label>
                  <div className="seat-numbers">
                    {bookingData.selectedSeats?.map((seat, index) => (
                      <span key={index} className="seat-badge">{seat}</span>
                    ))}
                  </div>
                </div>
              </div>

              <div className="detail-row">
                <FaTicketAlt className="icon" />
                <div>
                  <label>Ticket Type</label>
                  <p>M-Ticket (Mobile Ticket)</p>
                </div>
              </div>

              <div className="price-summary">
                <div className="price-row">
                  <span>Ticket Price</span>
                  <span>₹{bookingData.baseAmount?.toFixed(2)}</span>
                </div>
                <div className="price-row">
                  <span>Convenience Fee</span>
                  <span>₹{bookingData.convenienceFee?.toFixed(2)}</span>
                </div>
                <div className="price-row">
                  <span>GST (18%)</span>
                  <span>₹{bookingData.tax?.toFixed(2)}</span>
                </div>
                <div className="price-divider"></div>
                <div className="price-row total">
                  <span>Total Paid</span>
                  <span>₹{bookingData.totalAmount?.toFixed(2)}</span>
                </div>
              </div>

              <div className="transaction-info">
                <p><strong>Transaction ID:</strong> {bookingData.transactionId || paymentIntentId}</p>
                <p><strong>Booked At:</strong> {currentTime}</p>
                <p><strong>Booked By:</strong> {bookingData.userName || user?.name}</p>
              </div>
            </div>

            {/* Right Side - QR Code */}
            <div className="ticket-qr">
              <div className="qr-section">
                <h3>Show this QR at the theater</h3>
                <div className="qr-code">
                  <QRCodeSVG 
                    value={qrData}
                    size={200}
                    level="H"
                    includeMargin={true}
                  />
                </div>
                <p className="qr-note">Scan to verify booking</p>
                <div className="booking-ref">
                  {bookingId}
                </div>
              </div>
            </div>
          </div>

          {/* Ticket Footer */}
          <div className="ticket-footer">
            <div className="footer-note">
              <p>⚠️ Please arrive 15 minutes before showtime</p>
              <p>📱 Carry a valid ID proof along with this ticket</p>
              <p>🎬 Outside food and beverages are not allowed</p>
            </div>
          </div>
        </div>

        {/* Action Buttons */}
        <div className="action-buttons">
          <button className="btn-download" onClick={handleDownload}>
            <FaDownload /> Download Ticket
          </button>
          <button className="btn-email" onClick={handleEmail}>
            <FaEnvelope /> Email Ticket
          </button>
          <button className="btn-home" onClick={() => navigate('/')}>
            <FaHome /> Back to Home
          </button>
        </div>

        {/* Cancellation Policy */}
        <div className="policy-section">
          <h3>Cancellation Policy</h3>
          <ul>
            <li>Tickets can be cancelled up to 20 minutes before the show start time</li>
            <li>Cancellation charges: ₹{(bookingData.convenienceFee || 20).toFixed(2)} per ticket</li>
            <li>Refund will be processed within 5-7 working days</li>
            <li>No cancellation allowed for shows that have already started</li>
          </ul>
        </div>
      </div>
    </div>
  );
};

export default BookingConfirmation;
