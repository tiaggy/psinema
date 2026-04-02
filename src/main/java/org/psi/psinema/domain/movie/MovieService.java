package org.psi.psinema.domain.movie;

import lombok.RequiredArgsConstructor;
import org.psi.psinema.domain.movie.dto.MovieRequest;
import org.psi.psinema.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    public List<Movie> findAll(Genre genre, String title) {
        return movieRepository.findActiveFiltered(genre, title);
    }

    public Movie findById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found: " + id));
    }

    public Movie create(MovieRequest request) {
        Movie movie = Movie.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .genre(request.getGenre())
                .durationMinutes(request.getDurationMinutes())
                .posterUrl(request.getPosterUrl())
                .director(request.getDirector())
                .releaseDate(request.getReleaseDate())
                .build();
        return movieRepository.save(movie);
    }

    public Movie update(Long id, MovieRequest request) {
        Movie movie = findById(id);
        movie.setTitle(request.getTitle());
        movie.setDescription(request.getDescription());
        movie.setGenre(request.getGenre());
        movie.setDurationMinutes(request.getDurationMinutes());
        movie.setPosterUrl(request.getPosterUrl());
        movie.setDirector(request.getDirector());
        movie.setReleaseDate(request.getReleaseDate());
        return movieRepository.save(movie);
    }

    public void deactivate(Long id) {
        Movie movie = findById(id);
        movie.setActive(false);
        movieRepository.save(movie);
    }
}
