import React, { useState, useEffect } from 'react';
import { movieService } from '../../services';
import { createMoviePlaceholder } from '../../utils/imageUtils';
import './Hero.scss';

const Hero = () => {
  const [featuredMovies, setFeaturedMovies] = useState([]);
  const [currentSlide, setCurrentSlide] = useState(0);
  const [loading, setLoading] = useState(true);

  console.log('🎭 Hero component rendering');

  useEffect(() => {
    fetchFeaturedMovies();
  }, []);

  const fetchFeaturedMovies = async () => {
    try {
      const movies = await movieService.getNowShowing();
      // Get top 5 highest rated movies for hero
      const topMovies = movies
        .sort((a, b) => b.rating - a.rating)
        .slice(0, 5);
      setFeaturedMovies(topMovies);
      console.log('🎬 Featured movies loaded:', topMovies.length);
    } catch (error) {
      console.error('Error loading featured movies:', error);
      setFeaturedMovies([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (featuredMovies.length === 0) return;
    
    const timer = setInterval(() => {
      setCurrentSlide((prev) => (prev + 1) % featuredMovies.length);
    }, 5000);
    return () => clearInterval(timer);
  }, [featuredMovies.length]);

  if (loading) {
    return (
      <div className="hero" style={{ minHeight: '400px', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
        <p>Loading featured movies...</p>
      </div>
    );
  }

  if (featuredMovies.length === 0) {
    return null;
  }

  const movie = featuredMovies[currentSlide];
  console.log('🎬 Current hero slide:', movie?.movieName);

  // Fallback image if posterUrl is not available
  const getImageUrl = (movie) => {
    if (movie.posterUrl && 
        movie.posterUrl !== 'null' && 
        movie.posterUrl !== 'undefined' && 
        !movie.posterUrl.includes('null') && 
        !movie.posterUrl.includes('undefined') &&
        movie.posterUrl.startsWith('http')) {
      return movie.posterUrl;
    }
    return createMoviePlaceholder(movie.movieName || 'Movie', 1200, 600);
  };

  return (
    <div className="hero">
      <div 
        className="hero-background"
        style={{ backgroundImage: `url(${getImageUrl(movie)})` }}
      >
        <div className="hero-overlay"></div>
      </div>
      
      <div className="hero-content">
        <div className="container">
          <div className="hero-text">
            <span className="hero-badge">Now Showing</span>
            <h1 className="hero-title">{movie.movieName}</h1>
            <p className="hero-description">{movie.description}</p>
            
            <div className="hero-meta">
              <span className="meta-item">
                <strong>Rating:</strong> {movie.rating}/10
              </span>
              <span className="meta-item">
                <strong>Duration:</strong> {movie.duration} min
              </span>
              <span className="meta-item">
                <strong>Language:</strong> {movie.language}
              </span>
            </div>
            
            <div className="hero-actions">
              <a href={`/movie/${encodeURIComponent(movie.movieName)}`} className="btn btn-primary btn-lg">
                Book Tickets
              </a>
            </div>
          </div>
        </div>
      </div>
      
      <div className="hero-indicators">
        {featuredMovies.map((_, index) => (
          <button
            key={index}
            className={`indicator ${index === currentSlide ? 'active' : ''}`}
            onClick={() => setCurrentSlide(index)}
          />
        ))}
      </div>
    </div>
  );
};

export default Hero;
