import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { useApp } from '../../context/AppContext';
import { movieService } from '../../services';
import MovieCard from '../../components/Movie/MovieCard';
import FilterBar from '../../components/Movie/FilterBar';
import Hero from '../../components/Home/Hero';
import './Home.scss';

const Home = () => {
  const { selectedCity, searchQuery, filters } = useApp();
  const [searchParams] = useSearchParams();
  const [filteredMovies, setFilteredMovies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  console.log('🏠 Home component rendering');
  console.log('📊 Initial state:', { selectedCity, searchQuery, filters });

  useEffect(() => {
    fetchMovies();
  }, [selectedCity, searchQuery, filters, searchParams]);

  const fetchMovies = async () => {
    setLoading(true);
    setError(null);
    
    try {
      console.log('🔍 Fetching movies from backend...');
      
      let movies = [];
      const query = searchParams.get('search') || searchQuery;
      
      // If there's a search query, use search endpoint
      if (query) {
        console.log(`  Searching for: "${query}"`);
        movies = await movieService.searchMovies(query);
      } 
      // If filters are applied, use filter endpoint
      else if (filters.genre || filters.language || filters.minRating > 0) {
        console.log('  Applying filters:', filters);
        movies = await movieService.filterMovies(filters);
      } 
      // Otherwise get all currently showing movies
      else {
        console.log('  Fetching all now-showing movies');
        movies = await movieService.getNowShowing();
      }
      
      // Filter by city (client-side for now, can be moved to backend)
      if (selectedCity && selectedCity !== 'All Cities') {
        console.log(`  Filtering by city: ${selectedCity}`);
        // For now, show all movies in all cities
        // In production, backend should filter by shows in that city
      }
      
      console.log(`✅ Loaded ${movies.length} movies from backend`);
      setFilteredMovies(movies);
      
    } catch (err) {
      console.error('❌ Error fetching movies:', err);
      setError('Failed to load movies. Please try again later.');
      setFilteredMovies([]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="home-page">
      <Hero />
      
      <div className="container">
        <FilterBar />
        
        <div className="movies-section">
          <div className="section-header">
            <h2>Movies in {selectedCity}</h2>
            <p className="movies-count">{filteredMovies.length} movies</p>
          </div>

          {loading ? (
            <div className="loading-container">
              <div className="spinner"></div>
              <p>Loading movies...</p>
              <small style={{ color: '#999', marginTop: '10px' }}>
                Check browser console if this takes too long
              </small>
            </div>
          ) : filteredMovies.length > 0 ? (
            <div className="movies-grid">
              {filteredMovies.map(movie => (
                <MovieCard key={movie.id} movie={movie} />
              ))}
            </div>
          ) : (
            <div className="no-results">
              <h3>No movies found</h3>
              <p>Try adjusting your filters or search query</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Home;
