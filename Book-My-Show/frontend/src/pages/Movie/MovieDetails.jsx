import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { FaStar, FaClock, FaCalendar } from 'react-icons/fa';
import { movies, generateShows } from '../../mockData/indianData';
import { useApp } from '../../context/AppContext';
import { useAuth } from '../../context/AuthContext';
import { createMoviePlaceholder } from '../../utils/imageUtils';
import './MovieDetails.scss';

const MovieDetails = () => {
  const { movieName } = useParams();
  const navigate = useNavigate();
  const { selectedCity } = useApp();
  const { isAuthenticated } = useAuth();
  const [movie, setMovie] = useState(null);
  const [shows, setShows] = useState([]);

  useEffect(() => {
    const decodedMovieName = decodeURIComponent(movieName);
    const foundMovie = movies.find(m => m.name === decodedMovieName || m.movieName === decodedMovieName);
    if (foundMovie) {
      setMovie(foundMovie);
      const allShows = generateShows();
      const movieShows = allShows.filter(
        s => s.movieId === foundMovie.id && s.city === selectedCity
      );
      setShows(movieShows);
    }
  }, [movieName, selectedCity]);

  const handleBookNow = () => {
    if (!isAuthenticated) {
      navigate('/login', { state: { from: { pathname: `/movie/${movieName}/shows` } } });
    } else {
      navigate(`/movie/${movieName}/shows`);
    }
  };

  if (!movie) {
    return <div className="loading-container"><div className="spinner"></div></div>;
  }

  const getImageUrl = (width, height) => {
    if (movie.posterUrl && 
        movie.posterUrl !== 'null' && 
        movie.posterUrl !== 'undefined' && 
        !movie.posterUrl.includes('null') && 
        !movie.posterUrl.includes('undefined') &&
        movie.posterUrl.startsWith('http')) {
      return movie.posterUrl;
    }
    return createMoviePlaceholder(movie.movieName || movie.name, width, height);
  };

  return (
    <div className="movie-details-page">
      <div className="movie-banner" style={{ backgroundImage: `url(${getImageUrl(1200, 600)})` }}>
        <div className="banner-overlay"></div>
        <div className="container">
          <div className="movie-header">
            <img 
              src={getImageUrl(300, 450)} 
              alt={movie.movieName || movie.name} 
              className="movie-poster-large"
              onError={(e) => {
                e.target.src = createMoviePlaceholder(movie.movieName || movie.name, 300, 450);
              }}
            />
            <div className="movie-info-main">
              <h1>{movie.movieName || movie.name}</h1>
              <div className="movie-meta-large">
                <span><FaStar /> {movie.rating}/10</span>
                <span><FaClock /> {movie.duration} min</span>
                <span>{Array.isArray(movie.genre) ? movie.genre.join(', ') : movie.genre}</span>
                <span>{movie.language}</span>
              </div>
              <p className="movie-description-large">{movie.description}</p>
              <div className="movie-cast">
                <strong>Cast:</strong> {Array.isArray(movie.cast) ? movie.cast.join(', ') : movie.cast}
              </div>
              <div className="movie-director">
                <strong>Director:</strong> {movie.director}
              </div>
              <button className="btn btn-primary btn-lg" onClick={handleBookNow}>
                Book Tickets
              </button>
            </div>
          </div>
        </div>
      </div>

      <div className="container">
        <div className="shows-info">
          <h2>Available Shows in {selectedCity}</h2>
          <p>{shows.length} shows available</p>
        </div>
      </div>
    </div>
  );
};

export default MovieDetails;
