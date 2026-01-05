package com.driver.bookMyShow.Services;

import com.driver.bookMyShow.Enums.Genre;
import com.driver.bookMyShow.Enums.Language;
import com.driver.bookMyShow.Models.Movie;
import com.driver.bookMyShow.Models.Show;
import com.driver.bookMyShow.Models.Theater;
import com.driver.bookMyShow.Repositories.MovieRepository;
import com.driver.bookMyShow.Repositories.ShowRepository;
import com.driver.bookMyShow.Repositories.TheaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MovieSearchService - Advanced search and filter operations
 * 
 * NEW SERVICE - Does not affect existing MovieService
 * Provides enhanced search capabilities
 */
@Service
public class MovieSearchService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    /**
     * Search movies by name (partial match, case-insensitive)
     */
    public List<Movie> searchMoviesByName(String keyword) {
        List<Movie> allMovies = movieRepository.findAll();
        return allMovies.stream()
                .filter(movie -> movie.getMovieName().toLowerCase()
                        .contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Filter movies by genre
     */
    public List<Movie> filterByGenre(Genre genre) {
        List<Movie> allMovies = movieRepository.findAll();
        return allMovies.stream()
                .filter(movie -> movie.getGenre() == genre)
                .collect(Collectors.toList());
    }

    /**
     * Filter movies by language
     */
    public List<Movie> filterByLanguage(Language language) {
        List<Movie> allMovies = movieRepository.findAll();
        return allMovies.stream()
                .filter(movie -> movie.getLanguage() == language)
                .collect(Collectors.toList());
    }

    /**
     * Filter movies by minimum rating
     */
    public List<Movie> filterByMinimumRating(Double minRating) {
        List<Movie> allMovies = movieRepository.findAll();
        return allMovies.stream()
                .filter(movie -> movie.getRating() != null && movie.getRating() >= minRating)
                .collect(Collectors.toList());
    }

    /**
     * Get currently running movies (movies with active shows)
     */
    public List<Movie> getCurrentlyRunningMovies() {
        List<Movie> allMovies = movieRepository.findAll();
        
        // Return movies marked as "now showing" OR that have upcoming shows
        return allMovies.stream()
                .filter(movie -> {
                    // Check if movie is marked as now showing
                    if (movie.getNowShowing() != null && movie.getNowShowing()) {
                        return true;
                    }
                    // Otherwise check if it has shows
                    Date today = new Date(System.currentTimeMillis());
                    return movie.getShows() != null && movie.getShows().stream()
                            .anyMatch(show -> !show.getDate().before(today));
                })
                .collect(Collectors.toList());
    }

    /**
     * Get upcoming movies (release date in future)
     */
    public List<Movie> getUpcomingMovies() {
        List<Movie> allMovies = movieRepository.findAll();
        Date today = new Date(System.currentTimeMillis());
        
        return allMovies.stream()
                .filter(movie -> movie.getReleaseDate() != null && 
                               movie.getReleaseDate().after(today))
                .collect(Collectors.toList());
    }

    /**
     * Get movies by city (based on theater location)
     */
    public List<Movie> getMoviesByCity(String city) {
        List<Theater> cityTheaters = theaterRepository.findAll().stream()
                .filter(theater -> theater.getAddress().toLowerCase()
                        .contains(city.toLowerCase()))
                .collect(Collectors.toList());

        return cityTheaters.stream()
                .flatMap(theater -> theater.getShowList().stream())
                .map(Show::getMovie)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Advanced filter with multiple criteria
     */
    public List<Movie> advancedFilter(String keyword, Genre genre, Language language, 
                                     Double minRating, String city) {
        List<Movie> movies = movieRepository.findAll();

        // Apply filters sequentially
        if (keyword != null && !keyword.isEmpty()) {
            movies = movies.stream()
                    .filter(movie -> movie.getMovieName().toLowerCase()
                            .contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (genre != null) {
            movies = movies.stream()
                    .filter(movie -> movie.getGenre() == genre)
                    .collect(Collectors.toList());
        }

        if (language != null) {
            movies = movies.stream()
                    .filter(movie -> movie.getLanguage() == language)
                    .collect(Collectors.toList());
        }

        if (minRating != null) {
            movies = movies.stream()
                    .filter(movie -> movie.getRating() != null && movie.getRating() >= minRating)
                    .collect(Collectors.toList());
        }

        if (city != null && !city.isEmpty()) {
            List<Movie> cityMovies = getMoviesByCity(city);
            movies = movies.stream()
                    .filter(cityMovies::contains)
                    .collect(Collectors.toList());
        }

        return movies;
    }

    /**
     * Get shows for a movie in a specific city
     */
    public List<Show> getShowsForMovieInCity(Integer movieId, String city) {
        List<Theater> cityTheaters = theaterRepository.findAll().stream()
                .filter(theater -> theater.getAddress().toLowerCase()
                        .contains(city.toLowerCase()))
                .collect(Collectors.toList());

        return cityTheaters.stream()
                .flatMap(theater -> theater.getShowList().stream())
                .filter(show -> show.getMovie().getId().equals(movieId))
                .collect(Collectors.toList());
    }
}
