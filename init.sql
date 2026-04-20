-- init.sql  -  seed PSInema catalog data (halls, seats, movies, screenings, snacks).
-- Admin/employee users are seeded by DataInitializer at app boot.
-- Re-runnable: wipes catalog tables first.

BEGIN TRANSACTION;

DELETE FROM ticket;
DELETE FROM payment;
DELETE FROM snack_order_item;
DELETE FROM snack_order;
DELETE FROM orders;
DELETE FROM seat_reservation;
DELETE FROM screening;
DELETE FROM seat;
DELETE FROM hall;
DELETE FROM movie;
DELETE FROM snack_item;

-- Halls
INSERT INTO hall (id, name, total_rows, seats_per_row) VALUES
  (1, 'Sala A', 5, 8),
  (2, 'Sala B', 4, 6);

-- Seats for Sala A (5x8=40) and Sala B (4x6=24)
INSERT INTO seat (id, row_number, seat_number, type, hall_id) VALUES
  (1, 1, 1, 'WHEELCHAIR', 1),
  (2, 1, 2, 'STANDARD', 1),
  (3, 1, 3, 'STANDARD', 1),
  (4, 1, 4, 'STANDARD', 1),
  (5, 1, 5, 'STANDARD', 1),
  (6, 1, 6, 'STANDARD', 1),
  (7, 1, 7, 'STANDARD', 1),
  (8, 1, 8, 'STANDARD', 1),
  (9, 2, 1, 'STANDARD', 1),
  (10, 2, 2, 'STANDARD', 1),
  (11, 2, 3, 'STANDARD', 1),
  (12, 2, 4, 'STANDARD', 1),
  (13, 2, 5, 'STANDARD', 1),
  (14, 2, 6, 'STANDARD', 1),
  (15, 2, 7, 'STANDARD', 1),
  (16, 2, 8, 'STANDARD', 1),
  (17, 3, 1, 'STANDARD', 1),
  (18, 3, 2, 'STANDARD', 1),
  (19, 3, 3, 'STANDARD', 1),
  (20, 3, 4, 'STANDARD', 1),
  (21, 3, 5, 'STANDARD', 1),
  (22, 3, 6, 'STANDARD', 1),
  (23, 3, 7, 'STANDARD', 1),
  (24, 3, 8, 'STANDARD', 1),
  (25, 4, 1, 'STANDARD', 1),
  (26, 4, 2, 'STANDARD', 1),
  (27, 4, 3, 'STANDARD', 1),
  (28, 4, 4, 'STANDARD', 1),
  (29, 4, 5, 'STANDARD', 1),
  (30, 4, 6, 'STANDARD', 1),
  (31, 4, 7, 'STANDARD', 1),
  (32, 4, 8, 'STANDARD', 1),
  (33, 5, 1, 'VIP', 1),
  (34, 5, 2, 'VIP', 1),
  (35, 5, 3, 'VIP', 1),
  (36, 5, 4, 'VIP', 1),
  (37, 5, 5, 'VIP', 1),
  (38, 5, 6, 'VIP', 1),
  (39, 5, 7, 'VIP', 1),
  (40, 5, 8, 'VIP', 1),
  (41, 1, 1, 'STANDARD', 2),
  (42, 1, 2, 'STANDARD', 2),
  (43, 1, 3, 'STANDARD', 2),
  (44, 1, 4, 'STANDARD', 2),
  (45, 1, 5, 'STANDARD', 2),
  (46, 1, 6, 'STANDARD', 2),
  (47, 2, 1, 'STANDARD', 2),
  (48, 2, 2, 'STANDARD', 2),
  (49, 2, 3, 'STANDARD', 2),
  (50, 2, 4, 'STANDARD', 2),
  (51, 2, 5, 'STANDARD', 2),
  (52, 2, 6, 'STANDARD', 2),
  (53, 3, 1, 'STANDARD', 2),
  (54, 3, 2, 'STANDARD', 2),
  (55, 3, 3, 'STANDARD', 2),
  (56, 3, 4, 'STANDARD', 2),
  (57, 3, 5, 'STANDARD', 2),
  (58, 3, 6, 'STANDARD', 2),
  (59, 4, 1, 'VIP', 2),
  (60, 4, 2, 'VIP', 2),
  (61, 4, 3, 'VIP', 2),
  (62, 4, 4, 'VIP', 2),
  (63, 4, 5, 'VIP', 2),
  (64, 4, 6, 'VIP', 2);

-- Movies
INSERT INTO movie (id, title, description, genre, duration_minutes, director, release_date, poster_url, active) VALUES
  (1, 'Dune: Part Two', 'Paul Atreides unites with Chani and the Fremen to take revenge against those who destroyed his family.', 'SCI_FI', 166, 'Denis Villeneuve', '2024-03-01', NULL, 1),
  (2, 'Oppenheimer', 'The story of J. Robert Oppenheimer and his role in the development of the atomic bomb.', 'DRAMA', 180, 'Christopher Nolan', '2023-07-21', NULL, 1),
  (3, 'Barbie', 'Barbie and Ken leave the perfect world of Barbieland for the real world.', 'COMEDY', 114, 'Greta Gerwig', '2023-07-21', NULL, 1),
  (4, 'Inside Out 2', 'Riley enters puberty and new emotions arrive at Headquarters.', 'ANIMATION', 96, 'Kelsey Mann', '2024-06-14', NULL, 1),
  (5, 'The Batman', 'Vigilante Batman investigates corruption in Gotham City.', 'ACTION', 176, 'Matt Reeves', '2022-03-04', NULL, 1),
  (6, 'The Holdovers', 'A curmudgeonly teacher is forced to remain on campus over the holidays.', 'DRAMA', 133, 'Alexander Payne', '2023-10-27', NULL, 1);

-- Screenings over the next 7 days across 2 halls (no time conflicts per hall)
INSERT INTO screening (id, movie_id, hall_id, start_time, end_time, base_price, cancelled, status) VALUES
  (1, 1, 1, '2026-04-21 18:00:00', '2026-04-21 20:46:00', 9.5, 0, 'SCHEDULED'),
  (2, 3, 2, '2026-04-21 19:00:00', '2026-04-21 20:54:00', 9.0, 0, 'SCHEDULED'),
  (3, 2, 1, '2026-04-22 18:00:00', '2026-04-22 21:00:00', 10.0, 0, 'SCHEDULED'),
  (4, 5, 2, '2026-04-22 20:30:00', '2026-04-22 23:26:00', 10.0, 0, 'SCHEDULED'),
  (5, 4, 1, '2026-04-23 17:00:00', '2026-04-23 18:36:00', 8.5, 0, 'SCHEDULED'),
  (6, 3, 2, '2026-04-23 19:30:00', '2026-04-23 21:24:00', 8.5, 0, 'SCHEDULED'),
  (7, 6, 1, '2026-04-24 18:30:00', '2026-04-24 20:43:00', 10.5, 0, 'SCHEDULED'),
  (8, 1, 2, '2026-04-24 17:00:00', '2026-04-24 19:46:00', 12.0, 0, 'SCHEDULED'),
  (9, 5, 1, '2026-04-25 20:00:00', '2026-04-25 22:56:00', 9.0, 0, 'SCHEDULED'),
  (10, 4, 2, '2026-04-25 18:30:00', '2026-04-25 20:06:00', 9.0, 0, 'SCHEDULED'),
  (11, 2, 1, '2026-04-26 19:00:00', '2026-04-26 22:00:00', 10.5, 0, 'SCHEDULED'),
  (12, 6, 2, '2026-04-26 20:00:00', '2026-04-26 22:13:00', 10.5, 0, 'SCHEDULED'),
  (13, 3, 1, '2026-04-27 17:30:00', '2026-04-27 19:24:00', 9.0, 0, 'SCHEDULED'),
  (14, 1, 2, '2026-04-27 19:30:00', '2026-04-27 22:16:00', 12.0, 0, 'SCHEDULED');

-- Snack items
INSERT INTO snack_item (id, name, description, price, stock_quantity, image_url, available) VALUES
  (1, 'Popcorn Large', 'Freshly popped salted popcorn, large size', 6.5, 200, NULL, 1),
  (2, 'Popcorn Medium', 'Freshly popped salted popcorn, medium size', 4.5, 200, NULL, 1),
  (3, 'Sweet Popcorn Large', 'Caramel popcorn, large size', 7.0, 150, NULL, 1),
  (4, 'Cola 0.5L', 'Chilled Coca-Cola', 3.5, 300, NULL, 1),
  (5, 'Sparkling Water 0.5L', 'Chilled sparkling water', 2.5, 300, NULL, 1),
  (6, 'Nachos with cheese', 'Corn nachos with cheese dip', 5.5, 100, NULL, 1),
  (7, 'Hot Dog', 'Classic hot dog with mustard and ketchup', 4.9, 80, NULL, 1),
  (8, 'Mineral Water 0.5L', 'Still mineral water', 2.0, 300, NULL, 1),
  (9, 'Ice Cream Vanilla', 'Single scoop vanilla ice cream', 3.0, 120, NULL, 1);

COMMIT;
