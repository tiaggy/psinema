package org.psi.psinema.domain.screening;

import lombok.RequiredArgsConstructor;
import org.psi.psinema.domain.hall.Hall;
import org.psi.psinema.domain.hall.HallService;
import org.psi.psinema.domain.hall.Seat;
import org.psi.psinema.domain.hall.SeatRepository;
import org.psi.psinema.domain.movie.Movie;
import org.psi.psinema.domain.movie.MovieService;
import org.psi.psinema.domain.reservation.SeatReservationRepository;
import org.psi.psinema.domain.screening.dto.ScreeningRequest;
import org.psi.psinema.domain.screening.dto.SeatAvailabilityDto;
import org.psi.psinema.domain.ticket.TicketRepository;
import org.psi.psinema.domain.ticket.TicketStatus;
import org.psi.psinema.exception.ResourceNotFoundException;
import org.psi.psinema.exception.ScreeningConflictException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScreeningService {

    private final ScreeningRepository screeningRepository;
    private final MovieService movieService;
    private final HallService hallService;
    private final SeatRepository seatRepository;
    private final SeatReservationRepository seatReservationRepository;
    private final TicketRepository ticketRepository;

    public List<Screening> findAll(Long movieId, LocalDateTime date) {
        return screeningRepository.findFiltered(movieId, date);
    }

    public Screening findById(Long id) {
        return screeningRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Screening not found: " + id));
    }

    public List<SeatAvailabilityDto> getSeatAvailability(Long screeningId) {
        Screening screening = findById(screeningId);
        List<Seat> seats = seatRepository.findByHallIdOrderByRowNumberAscSeatNumberAsc(screening.getHall().getId());

        Set<Long> takenSeatIds = ticketRepository
                .findByOrderScreeningIdAndStatusIn(screeningId, List.of(TicketStatus.ACTIVE, TicketStatus.USED))
                .stream().map(t -> t.getSeat().getId()).collect(Collectors.toSet());

        Set<Long> tempReservedSeatIds = seatReservationRepository
                .findActiveReservations(screeningId, LocalDateTime.now())
                .stream().map(r -> r.getSeat().getId()).collect(Collectors.toSet());

        return seats.stream().map(seat -> {
            String status;
            if (takenSeatIds.contains(seat.getId())) {
                status = "TAKEN";
            } else if (tempReservedSeatIds.contains(seat.getId())) {
                status = "TEMPORARILY_RESERVED";
            } else {
                status = "AVAILABLE";
            }
            return new SeatAvailabilityDto(seat.getId(), seat.getRowNumber(), seat.getSeatNumber(), seat.getType(), status);
        }).toList();
    }

    @Transactional
    public Screening create(ScreeningRequest request) {
        Movie movie = movieService.findById(request.getMovieId());
        Hall hall = hallService.findById(request.getHallId());
        LocalDateTime endTime = request.getStartTime().plusMinutes(movie.getDurationMinutes());

        long conflicts = screeningRepository.countConflicts(hall.getId(), request.getStartTime(), endTime);
        if (conflicts > 0) {
            throw new ScreeningConflictException("Hall is already booked for this time slot");
        }

        Screening screening = Screening.builder()
                .movie(movie)
                .hall(hall)
                .startTime(request.getStartTime())
                .endTime(endTime)
                .basePrice(request.getBasePrice())
                .build();
        return screeningRepository.save(screening);
    }

    @Transactional
    public Screening update(Long id, ScreeningRequest request) {
        Screening screening = findById(id);
        Movie movie = movieService.findById(request.getMovieId());
        Hall hall = hallService.findById(request.getHallId());
        LocalDateTime endTime = request.getStartTime().plusMinutes(movie.getDurationMinutes());

        long conflicts = screeningRepository.countConflictsExcluding(hall.getId(), request.getStartTime(), endTime, id);
        if (conflicts > 0) {
            throw new ScreeningConflictException("Hall is already booked for this time slot");
        }

        screening.setMovie(movie);
        screening.setHall(hall);
        screening.setStartTime(request.getStartTime());
        screening.setEndTime(endTime);
        screening.setBasePrice(request.getBasePrice());
        return screeningRepository.save(screening);
    }

    @Transactional
    public void cancel(Long id) {
        Screening screening = findById(id);
        screening.setCancelled(true);
        screeningRepository.save(screening);
        // cascade ticket cancellation + refunds handled by OrderService
    }
}
