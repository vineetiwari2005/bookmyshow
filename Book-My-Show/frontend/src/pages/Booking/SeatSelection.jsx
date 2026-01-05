import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { FaCouch, FaArrowLeft, FaCheckCircle, FaClock, FaRupeeSign } from 'react-icons/fa';
import { MdEventSeat } from 'react-icons/md';
import { seatLockService, authService } from '../../services';
import { seatLayout } from '../../mockData/indianData';
import './SeatSelection.scss';

const SeatSelection = () => {
  const { showId } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  const showDetails = location.state;

  const [selectedSeats, setSelectedSeats] = useState([]);
  const [lockedSeats, setLockedSeats] = useState([]);
  const [bookedSeats, setBookedSeats] = useState([]);
  const [sessionId] = useState(`SESSION_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`);
  const [timeRemaining, setTimeRemaining] = useState(600); // 10 minutes in seconds
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const user = authService.getCurrentUser();

  // Generate seat map based on seatLayout configuration
  const generateSeats = () => {
    const seats = [];
    seatLayout.rows.forEach(row => {
      const rowSeats = [];
      for (let num = 1; num <= seatLayout.seatsPerRow; num++) {
        const seatNumber = `${row}${num}`;
        rowSeats.push({
          number: seatNumber,
          row: row,
          type: seatLayout.seatTypes[row],
          price: seatLayout.pricing[seatLayout.seatTypes[row]],
          status: 'available' // available, selected, locked, booked
        });
      }
      seats.push(rowSeats);
    });
    return seats;
  };

  const [seats, setSeats] = useState(generateSeats());

  // Timer countdown
  useEffect(() => {
    if (selectedSeats.length === 0) return;

    const timer = setInterval(() => {
      setTimeRemaining(prev => {
        if (prev <= 1) {
          clearInterval(timer);
          handleTimeout();
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(timer);
  }, [selectedSeats.length]);

  // Simulate fetching booked seats (in real app, fetch from backend)
  useEffect(() => {
    // Mock some booked seats for demonstration
    const mockBookedSeats = ['A5', 'A6', 'B3', 'C7', 'D2'];
    setBookedSeats(mockBookedSeats);
  }, [showId]);

  const handleTimeout = () => {
    alert('Time expired! Please select seats again.');
    setSelectedSeats([]);
    setTimeRemaining(600);
  };

  const handleSeatClick = (seatNumber, seatType, price) => {
    // Check if seat is booked or locked by others
    if (bookedSeats.includes(seatNumber) || lockedSeats.includes(seatNumber)) {
      return;
    }

    setSelectedSeats(prev => {
      if (prev.find(s => s.number === seatNumber)) {
        // Deselect
        return prev.filter(s => s.number !== seatNumber);
      } else {
        // Select (max 10 seats)
        if (prev.length >= 10) {
          alert('You can select maximum 10 seats at a time.');
          return prev;
        }
        return [...prev, { number: seatNumber, type: seatType, price: price }];
      }
    });
  };

  const getSeatStatus = (seatNumber) => {
    if (bookedSeats.includes(seatNumber)) return 'booked';
    if (lockedSeats.includes(seatNumber)) return 'locked';
    if (selectedSeats.find(s => s.number === seatNumber)) return 'selected';
    return 'available';
  };

  const calculateTotal = () => {
    const baseAmount = selectedSeats.reduce((sum, seat) => sum + seat.price, 0);
    const convenienceFee = Math.max(baseAmount * 0.025, 20); // 2.5% or min ₹20
    const tax = (baseAmount + convenienceFee) * 0.18; // 18% GST
    const total = baseAmount + convenienceFee + tax;

    return {
      baseAmount: baseAmount.toFixed(2),
      convenienceFee: convenienceFee.toFixed(2),
      tax: tax.toFixed(2),
      total: total.toFixed(2)
    };
  };

  const formatTime = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  const handleProceedToPayment = async () => {
    if (selectedSeats.length === 0) {
      alert('Please select at least one seat.');
      return;
    }

    if (!user) {
      alert('Please login to continue.');
      navigate('/login', { state: { from: location.pathname } });
      return;
    }

    setLoading(true);
    setError(null);

    try {
      // Try to lock seats via backend
      try {
        const lockData = {
          showId: parseInt(showId),
          userId: user.id,
          sessionId: sessionId,
          seatNumbers: selectedSeats.map(s => s.number)
        };

        await seatLockService.lockSeats(lockData);
      } catch (lockError) {
        console.log('Seat lock API not available, proceeding with mock data');
      }

      // Proceed to payment page
      const pricing = calculateTotal();
      const bookingData = {
        showId: showId,
        sessionId: sessionId,
        movieName: showDetails?.movieName || 'Selected Movie',
        theaterName: showDetails?.theaterName || 'Selected Theater',
        showDate: showDetails?.showDate || new Date().toISOString().split('T')[0],
        showTime: showDetails?.showTime || '19:00',
        selectedSeats: selectedSeats.map(s => s.number),
        seatDetails: selectedSeats,
        baseAmount: parseFloat(pricing.baseAmount),
        convenienceFee: parseFloat(pricing.convenienceFee),
        tax: parseFloat(pricing.tax),
        totalAmount: parseFloat(pricing.total),
        userName: user.name,
        userEmail: user.email || user.emailId
      };

      navigate('/booking/payment', { state: bookingData });
    } catch (err) {
      setError(err.message || 'Failed to proceed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  if (!showDetails) {
    return (
      <div className="seat-selection-page">
        <div className="container">
          <div className="error-message">
            <h3>No show details found</h3>
            <p>Please select a show to continue.</p>
            <button onClick={() => navigate('/')}>Go to Home</button>
          </div>
        </div>
      </div>
    );
  }

  const pricing = calculateTotal();

  return (
    <div className="seat-selection-page">
      <div className="container">
        {/* Header */}
        <div className="page-header">
          <button className="back-btn" onClick={() => navigate(-1)}>
            <FaArrowLeft /> Back
          </button>
          <div className="show-info">
            <h1>{showDetails.movieName}</h1>
            <p>{showDetails.theaterName} | {showDetails.showDate} | {showDetails.showTime}</p>
          </div>
          {selectedSeats.length > 0 && (
            <div className="timer">
              <FaClock /> {formatTime(timeRemaining)}
            </div>
          )}
        </div>

        <div className="seat-selection-content">
          {/* Seat Map */}
          <div className="seat-map-container">
            <div className="screen">
              <div className="screen-label">SCREEN THIS WAY</div>
            </div>

            <div className="seat-map">
              {seats.map((row, rowIndex) => (
                <div key={rowIndex} className="seat-row">
                  <div className="row-label">{seatLayout.rows[rowIndex]}</div>
                  <div className="seats">
                    {row.map((seat, seatIndex) => {
                      const status = getSeatStatus(seat.number);
                      return (
                        <button
                          key={seatIndex}
                          className={`seat ${status} ${seat.type.toLowerCase()}`}
                          onClick={() => handleSeatClick(seat.number, seat.type, seat.price)}
                          disabled={status === 'booked' || status === 'locked'}
                          title={`${seat.number} - ${seat.type} - ₹${seat.price}`}
                        >
                          <MdEventSeat />
                          <span className="seat-number">{seat.number}</span>
                        </button>
                      );
                    })}
                  </div>
                  <div className="row-label">{seatLayout.rows[rowIndex]}</div>
                </div>
              ))}
            </div>

            {/* Legend */}
            <div className="seat-legend">
              <div className="legend-item">
                <div className="legend-seat available"><MdEventSeat /></div>
                <span>Available</span>
              </div>
              <div className="legend-item">
                <div className="legend-seat selected"><MdEventSeat /></div>
                <span>Selected</span>
              </div>
              <div className="legend-item">
                <div className="legend-seat booked"><MdEventSeat /></div>
                <span>Booked</span>
              </div>
              <div className="legend-item">
                <div className="legend-seat locked"><MdEventSeat /></div>
                <span>Locked</span>
              </div>
            </div>

            {/* Seat Types & Pricing */}
            <div className="seat-types">
              <div className="type-item premium">
                <span className="type-name">Premium (A-B)</span>
                <span className="type-price">₹{seatLayout.pricing.PREMIUM}</span>
              </div>
              <div className="type-item gold">
                <span className="type-name">Gold (C-D)</span>
                <span className="type-price">₹{seatLayout.pricing.GOLD}</span>
              </div>
              <div className="type-item silver">
                <span className="type-name">Silver (E)</span>
                <span className="type-price">₹{seatLayout.pricing.SILVER}</span>
              </div>
            </div>
          </div>

          {/* Booking Summary */}
          <div className="booking-summary">
            <h3>Booking Summary</h3>
            
            {selectedSeats.length > 0 ? (
              <>
                <div className="selected-seats-list">
                  <h4>Selected Seats ({selectedSeats.length})</h4>
                  <div className="seats-grid">
                    {selectedSeats.map(seat => (
                      <div key={seat.number} className="seat-tag">
                        {seat.number}
                        <button 
                          className="remove-seat"
                          onClick={() => handleSeatClick(seat.number, seat.type, seat.price)}
                        >
                          ×
                        </button>
                      </div>
                    ))}
                  </div>
                </div>

                <div className="price-breakdown">
                  <div className="price-item">
                    <span>Ticket Price</span>
                    <span>₹{pricing.baseAmount}</span>
                  </div>
                  <div className="price-item">
                    <span>Convenience Fee</span>
                    <span>₹{pricing.convenienceFee}</span>
                  </div>
                  <div className="price-item">
                    <span>GST (18%)</span>
                    <span>₹{pricing.tax}</span>
                  </div>
                  <div className="price-divider"></div>
                  <div className="price-item total">
                    <span>Total Amount</span>
                    <span>₹{pricing.total}</span>
                  </div>
                </div>

                {error && <div className="error-alert">{error}</div>}

                <button 
                  className="proceed-btn"
                  onClick={handleProceedToPayment}
                  disabled={loading}
                >
                  {loading ? (
                    'Processing...'
                  ) : (
                    <>
                      <FaCheckCircle /> Proceed to Payment
                    </>
                  )}
                </button>

                <p className="note">
                  <FaClock /> Selected seats will be held for {formatTime(timeRemaining)}
                </p>
              </>
            ) : (
              <div className="no-selection">
                <FaCouch size={50} />
                <p>Select seats to continue</p>
                <small>Click on available seats to select</small>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default SeatSelection;
