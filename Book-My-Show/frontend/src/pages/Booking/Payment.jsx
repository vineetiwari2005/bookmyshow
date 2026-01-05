import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { loadStripe } from '@stripe/stripe-js';
import { Elements, CardElement, useStripe, useElements } from '@stripe/react-stripe-js';
import { FaCreditCard, FaLock, FaCheckCircle } from 'react-icons/fa';
import './Payment.scss';

const API_URL = 'http://localhost:8080';

// Initialize Stripe (publishable key will be fetched from backend)
let stripePromise = null;

const PaymentForm = ({ bookingDetails }) => {
  const stripe = useStripe();
  const elements = useElements();
  const navigate = useNavigate();
  const [processing, setProcessing] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (!stripe || !elements) {
      return;
    }

    setProcessing(true);
    setError(null);

    try {
      // Create payment intent
      const response = await fetch(`${API_URL}/api/payment/create-stripe-intent`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          amount: Math.round(bookingDetails.totalAmount * 100), // Convert to paise/cents
          currency: 'inr',
          sessionId: bookingDetails.sessionId
        })
      });

      const { clientSecret, paymentIntentId, error: apiError } = await response.json();

      if (apiError) {
        throw new Error(apiError);
      }

      // Confirm payment with Stripe
      const { error: stripeError, paymentIntent } = await stripe.confirmCardPayment(
        clientSecret,
        {
          payment_method: {
            card: elements.getElement(CardElement),
            billing_details: {
              name: bookingDetails.userName || 'Guest',
              email: bookingDetails.userEmail || 'user@example.com'
            }
          }
        }
      );

      if (stripeError) {
        throw new Error(stripeError.message);
      }

      if (paymentIntent.status === 'succeeded') {
        setSuccess(true);
        setTimeout(() => {
          navigate(`/booking/confirmation/${paymentIntent.id}`, {
            state: { 
              ...bookingDetails, 
              paymentIntentId: paymentIntent.id,
              transactionId: paymentIntent.id
            }
          });
        }, 2000);
      }
    } catch (err) {
      setError(err.message);
      setProcessing(false);
    }
  };

  const cardElementOptions = {
    style: {
      base: {
        fontSize: '16px',
        color: '#424770',
        '::placeholder': {
          color: '#aab7c4',
        },
      },
      invalid: {
        color: '#9e2146',
      },
    },
  };

  if (success) {
    return (
      <div className="payment-success">
        <FaCheckCircle size={60} color="#4caf50" />
        <h2>Payment Successful!</h2>
        <p>Redirecting to confirmation...</p>
      </div>
    );
  }

  return (
    <form onSubmit={handleSubmit} className="payment-form">
      <div className="card-element-wrapper">
        <label>
          <FaCreditCard /> Card Details
        </label>
        <CardElement options={cardElementOptions} />
      </div>

      {error && <div className="payment-error">{error}</div>}

      <div className="test-card-info">
        <p><strong>Test Card:</strong> 4242 4242 4242 4242</p>
        <p>Use any future expiry date and any 3-digit CVC</p>
      </div>

      <button 
        type="submit" 
        className="pay-button"
        disabled={!stripe || processing}
      >
        <FaLock /> {processing ? 'Processing...' : `Pay ₹${bookingDetails.totalAmount}`}
      </button>
    </form>
  );
};

const Payment = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [stripeKey, setStripeKey] = useState(null);
  const bookingDetails = location.state;

  useEffect(() => {
    // Fetch Stripe publishable key from backend
    const fetchStripeKey = async () => {
      try {
        const response = await fetch(`${API_URL}/api/payment/stripe-config`);
        const data = await response.json();
        setStripeKey(data.publishableKey);
        stripePromise = loadStripe(data.publishableKey);
      } catch (error) {
        console.error('Failed to load Stripe config:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchStripeKey();
  }, []);

  if (!bookingDetails) {
    return (
      <div className="payment-page">
        <div className="container">
          <div className="error-message">
            <h3>No booking details found</h3>
            <p>Please select seats and try again.</p>
            <button onClick={() => navigate('/')}>Go to Home</button>
          </div>
        </div>
      </div>
    );
  }

  if (loading) {
    return (
      <div className="payment-page">
        <div className="container">
          <div className="loading-container">
            <div className="spinner"></div>
            <p>Loading payment gateway...</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="payment-page">
      <div className="container">
        <div className="payment-container">
          <div className="payment-header">
            <h1>Complete Your Payment</h1>
            <p className="secure-badge">
              <FaLock /> Secured by Stripe
            </p>
          </div>

          <div className="payment-content">
            <div className="payment-summary">
              <h3>Booking Summary</h3>
              <div className="summary-item">
                <span>Movie:</span>
                <strong>{bookingDetails.movieName}</strong>
              </div>
              <div className="summary-item">
                <span>Theater:</span>
                <strong>{bookingDetails.theaterName}</strong>
              </div>
              <div className="summary-item">
                <span>Date & Time:</span>
                <strong>{bookingDetails.showDate} at {bookingDetails.showTime}</strong>
              </div>
              <div className="summary-item">
                <span>Seats:</span>
                <strong>{bookingDetails.selectedSeats?.join(', ')}</strong>
              </div>
              <div className="summary-divider"></div>
              <div className="summary-item">
                <span>Ticket Price:</span>
                <strong>₹{bookingDetails.baseAmount}</strong>
              </div>
              <div className="summary-item">
                <span>Convenience Fee:</span>
                <strong>₹{bookingDetails.convenienceFee || 0}</strong>
              </div>
              <div className="summary-item">
                <span>GST (18%):</span>
                <strong>₹{bookingDetails.tax || 0}</strong>
              </div>
              <div className="summary-divider"></div>
              <div className="summary-item total">
                <span>Total Amount:</span>
                <strong>₹{bookingDetails.totalAmount}</strong>
              </div>
            </div>

            <div className="payment-form-container">
              {stripeKey && stripePromise ? (
                <Elements stripe={stripePromise}>
                  <PaymentForm bookingDetails={bookingDetails} />
                </Elements>
              ) : (
                <div className="error-message">
                  <p>Payment gateway unavailable. Please try again later.</p>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Payment;

