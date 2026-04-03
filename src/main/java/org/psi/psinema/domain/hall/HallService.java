package org.psi.psinema.domain.hall;

import lombok.RequiredArgsConstructor;
import org.psi.psinema.domain.hall.dto.HallRequest;
import org.psi.psinema.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HallService {

    private final HallRepository hallRepository;
    private final SeatRepository seatRepository;

    public List<Hall> findAll() {
        return hallRepository.findAll();
    }

    public Hall findById(Long id) {
        return hallRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hall not found: " + id));
    }

    public List<Seat> findSeats(Long hallId) {
        findById(hallId);
        return seatRepository.findByHallIdOrderByRowNumberAscSeatNumberAsc(hallId);
    }

    @Transactional
    public Hall create(HallRequest request) {
        Hall hall = Hall.builder()
                .name(request.getName())
                .totalRows(request.getTotalRows())
                .seatsPerRow(request.getSeatsPerRow())
                .build();
        hall = hallRepository.save(hall);

        List<Seat> seats = new ArrayList<>();
        for (int row = 1; row <= request.getTotalRows(); row++) {
            for (int col = 1; col <= request.getSeatsPerRow(); col++) {
                seats.add(Seat.builder().hall(hall).rowNumber(row).seatNumber(col).build());
            }
        }
        seatRepository.saveAll(seats);
        return hall;
    }
}
