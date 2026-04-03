package org.psi.psinema.domain.screening;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ScreeningRepository extends JpaRepository<Screening, Long> {

    @Query("SELECT s FROM Screening s WHERE s.cancelled = false " +
           "AND (:movieId IS NULL OR s.movie.id = :movieId) " +
           "AND (:date IS NULL OR CAST(s.startTime AS date) = CAST(:date AS date))")
    List<Screening> findFiltered(@Param("movieId") Long movieId,
                                 @Param("date") LocalDateTime date);

    @Query("SELECT COUNT(s) FROM Screening s WHERE s.hall.id = :hallId " +
           "AND s.cancelled = false " +
           "AND s.startTime < :endTime " +
           "AND s.endTime > :startTime")
    long countConflicts(@Param("hallId") Long hallId,
                        @Param("startTime") LocalDateTime startTime,
                        @Param("endTime") LocalDateTime endTime);

    @Query("SELECT COUNT(s) FROM Screening s WHERE s.hall.id = :hallId " +
           "AND s.cancelled = false " +
           "AND s.id <> :excludeId " +
           "AND s.startTime < :endTime " +
           "AND s.endTime > :startTime")
    long countConflictsExcluding(@Param("hallId") Long hallId,
                                  @Param("startTime") LocalDateTime startTime,
                                  @Param("endTime") LocalDateTime endTime,
                                  @Param("excludeId") Long excludeId);

    List<Screening> findByCancelledFalseAndStartTimeAfter(LocalDateTime after);
}
