import api from './api';

// Authentication Services
export const authService = {
  signup: async (userData) => {
    // Map frontend field names to backend expected names
    const backendData = {
      name: userData.name,
      email: userData.email,
      password: userData.password,
      mobileNo: userData.mobileNumber, // Frontend uses mobileNumber, backend expects mobileNo
      age: parseInt(userData.age),
      gender: userData.gender,
      address: userData.address || '',
      role: userData.role || 'USER'
    };
    const response = await api.post('/auth/signup', backendData);
    return response.data;
  },

  login: async (credentials) => {
    const response = await api.post('/auth/login', credentials);
    return response.data;
  },

  logout: () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
  },

  getCurrentUser: () => {
    const userStr = localStorage.getItem('user');
    if (!userStr || userStr === 'undefined' || userStr === 'null') {
      return null;
    }
    try {
      return JSON.parse(userStr);
    } catch (error) {
      console.error('Error parsing user data from localStorage:', error);
      localStorage.removeItem('user');
      return null;
    }
  },

  isAuthenticated: () => {
    return !!localStorage.getItem('accessToken');
  },

  getUserRole: () => {
    const user = authService.getCurrentUser();
    return user?.role || 'USER';
  }
};

// Movie Services
export const movieService = {
  getNowShowing: async () => {
    const response = await api.get('/api/movies/now-showing');
    return response.data;
  },

  searchMovies: async (keyword) => {
    const response = await api.get(`/api/movies/search?keyword=${keyword}`);
    return response.data;
  },

  filterMovies: async (filters) => {
    const params = new URLSearchParams();
    if (filters.genre) params.append('genre', filters.genre);
    if (filters.language) params.append('language', filters.language);
    if (filters.minRating) params.append('minRating', filters.minRating);
    
    const response = await api.get(`/api/movies/filter/advanced?${params.toString()}`);
    return response.data;
  },

  getMoviesByCity: async (city) => {
    const response = await api.get(`/api/movies/city/${city}`);
    return response.data;
  },

  addMovie: async (movieData) => {
    const response = await api.post('/movie/addNew', movieData);
    return response.data;
  }
};

// Theater Services
export const theaterService = {
  getAllTheaters: async () => {
    const response = await api.get('/theater/get-all-theaters');
    return response.data;
  },

  getTheatersByCity: async (city) => {
    const response = await api.get(`/theater/get-theaters-by-city/${city}`);
    return response.data;
  },

  addTheater: async (theaterData) => {
    const response = await api.post('/theater/add-theater', theaterData);
    return response.data;
  },

  addTheaterSeat: async (seatData) => {
    const response = await api.post('/theater/add-theater-seat', seatData);
    return response.data;
  }
};

// Show Services
export const showService = {
  addShow: async (showData) => {
    const response = await api.post('/show/add-show', showData);
    return response.data;
  },

  getShowsByMovie: async (movieId) => {
    const response = await api.get(`/show/get-shows-by-movie/${movieId}`);
    return response.data;
  },

  getShowsByTheater: async (theaterId) => {
    const response = await api.get(`/show/get-shows-by-theater/${theaterId}`);
    return response.data;
  },

  getShowById: async (showId) => {
    const response = await api.get(`/show/get-show/${showId}`);
    return response.data;
  }
};

// Seat Lock Services
export const seatLockService = {
  lockSeats: async (lockData) => {
    const response = await api.post('/api/seat-locks/lock', lockData);
    return response.data;
  },

  releaseLocks: async (sessionId) => {
    const response = await api.post(`/api/seat-locks/release/${sessionId}`);
    return response.data;
  },

  confirmLocks: async (sessionId) => {
    const response = await api.post(`/api/seat-locks/confirm/${sessionId}`);
    return response.data;
  }
};

// Payment Services
export const paymentService = {
  initiatePayment: async (paymentData) => {
    const response = await api.post('/api/payments/initiate', paymentData);
    return response.data;
  },

  processPayment: async (transactionId) => {
    const response = await api.post(`/api/payments/process/${transactionId}`);
    return response.data;
  },

  getPaymentStatus: async (transactionId) => {
    const response = await api.get(`/api/payments/status/${transactionId}`);
    return response.data;
  }
};

// Ticket/Booking Services
export const bookingService = {
  bookTicket: async (bookingData) => {
    const response = await api.post('/ticket/book-ticket', bookingData);
    return response.data;
  },

  getUserBookings: async (userId) => {
    const response = await api.get(`/api/user/bookings/${userId}`);
    return response.data;
  },

  cancelBooking: async (ticketId) => {
    const response = await api.post(`/api/booking/cancel/${ticketId}`);
    return response.data;
  },

  getBookingDetails: async (ticketId) => {
    const response = await api.get(`/api/booking/${ticketId}`);
    return response.data;
  }
};

// Admin Services
export const adminService = {
  getDashboard: async () => {
    const response = await api.get('/admin/dashboard');
    return response.data;
  },

  getAllUsers: async () => {
    const response = await api.get('/admin/users');
    return response.data;
  },

  activateUser: async (userId) => {
    const response = await api.post(`/admin/users/${userId}/activate`);
    return response.data;
  },

  deactivateUser: async (userId) => {
    const response = await api.post(`/admin/users/${userId}/deactivate`);
    return response.data;
  },

  getAnalytics: async (period) => {
    const response = await api.get(`/admin/analytics?period=${period}`);
    return response.data;
  },

  getAllMovies: async () => {
    const response = await api.get('/admin/movies');
    return response.data;
  },

  getAllTheaters: async () => {
    const response = await api.get('/admin/theaters');
    return response.data;
  },

  getAllShows: async () => {
    const response = await api.get('/admin/shows');
    return response.data;
  }
};

// User Profile Services
export const userService = {
  getProfile: async (userId) => {
    const response = await api.get(`/api/user/profile/${userId}`);
    return response.data;
  },

  updateProfile: async (userId, userData) => {
    const response = await api.put(`/api/user/profile/${userId}`, userData);
    return response.data;
  },

  getWalletBalance: async (userId) => {
    const response = await api.get(`/api/user/wallet/${userId}`);
    return response.data;
  }
};

export default {
  authService,
  movieService,
  theaterService,
  showService,
  seatLockService,
  paymentService,
  bookingService,
  adminService,
  userService
};
