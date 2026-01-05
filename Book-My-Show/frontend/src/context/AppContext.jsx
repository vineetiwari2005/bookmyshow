import React, { createContext, useState, useContext, useEffect } from 'react';
import { cities } from '../mockData/indianData';

const AppContext = createContext(null);

export const AppProvider = ({ children }) => {
  console.log('🌍 AppProvider initialized');
  
  const [selectedCity, setSelectedCity] = useState(() => {
    return localStorage.getItem('selectedCity') || 'Mumbai';
  });
  
  console.log('📍 Selected city:', selectedCity);
  
  const [searchQuery, setSearchQuery] = useState('');
  const [filters, setFilters] = useState({
    genre: '',
    language: '',
    minRating: 0
  });

  useEffect(() => {
    localStorage.setItem('selectedCity', selectedCity);
  }, [selectedCity]);

  const changeCity = (city) => {
    setSelectedCity(city);
  };

  const updateFilters = (newFilters) => {
    setFilters({ ...filters, ...newFilters });
  };

  const resetFilters = () => {
    setFilters({
      genre: '',
      language: '',
      minRating: 0
    });
    setSearchQuery('');
  };

  const value = {
    selectedCity,
    changeCity,
    searchQuery,
    setSearchQuery,
    filters,
    updateFilters,
    resetFilters,
    cities,
  };

  return <AppContext.Provider value={value}>{children}</AppContext.Provider>;
};

export const useApp = () => {
  const context = useContext(AppContext);
  if (!context) {
    throw new Error('useApp must be used within an AppProvider');
  }
  return context;
};

export default AppContext;
