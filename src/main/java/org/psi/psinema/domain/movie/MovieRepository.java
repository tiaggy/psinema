package org.psi.psinema.domain.movie;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findByActiveTrue();

    @Query("SELECT m FROM Movie m WHERE m.active = true " +
           "AND (:genre IS NULL OR m.genre = :genre) " +
           "AND (:title IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%')))")
    List<Movie> findActiveFiltered(@Param("genre") Genre genre, @Param("title") String title);
}
