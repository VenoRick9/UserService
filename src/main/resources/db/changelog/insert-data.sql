INSERT INTO users (name, surname, birth_date, email)
VALUES ('Alice', 'Johnson', '1990-01-01', 'alice.johnson@example.com'),
       ('Bob', 'Smith', '1985-02-15', 'bob.smith@example.com'),
       ('Charlie', 'Brown', '1992-03-20', 'charlie.brown@example.com'),
       ('Diana', 'Prince', '1987-04-10', 'diana.prince@example.com'),
       ('Eve', 'Walker', '1995-05-30', 'eve.walker@example.com'),
       ('Frank', 'Miller', '1980-06-25', 'frank.miller@example.com'),
       ('Grace', 'Davis', '1993-07-07', 'grace.davis@example.com'),
       ('Henry', 'Wilson', '1988-08-18', 'henry.wilson@example.com'),
       ('Irene', 'Thomas', '1991-09-09', 'irene.thomas@example.com'),
       ('Jack', 'Evans', '1986-10-12', 'jack.evans@example.com'),
       ('Karen', 'Moore', '1994-11-11', 'karen.moore@example.com'),
       ('Leo', 'Taylor', '1989-12-22', 'leo.taylor@example.com');

INSERT INTO card_info (user_id, number, holder, expiration_date)
VALUES
(1, '1111222233334444', 'ALICE JOHNSON', '2028-01-01'),
(1, '1111222233335555', 'ALICE JOHNSON', '2029-01-01'),
(2, '2222333344445555', 'BOB SMITH', '2027-02-15'),
(3, '3333444455556666', 'CHARLIE BROWN', '2028-03-20'),
(3, '3333444455557777', 'CHARLIE BROWN', '2029-03-20'),
(4, '4444555566667777', 'DIANA PRINCE', '2027-04-10'),
(5, '5555666677778888', 'EVE WALKER', '2026-05-30'),
(6, '6666777788889999', 'FRANK MILLER', '2028-06-25'),
(6, '6666777788880000', 'FRANK MILLER', '2029-06-25'),
(7, '7777888899990000', 'GRACE DAVIS', '2027-07-07'),
(8, '8888999900001111', 'HENRY WILSON', '2028-08-18'),
(8, '8888999900002222', 'HENRY WILSON', '2029-08-18'),
(9, '9999000011112222', 'IRENE THOMAS', '2026-09-09'),
(10, '0000111122223333', 'JACK EVANS', '2027-10-12'),
(11, '1111000022223333', 'KAREN MOORE', '2029-11-11'),
(11, '1111000022224444', 'KAREN MOORE', '2028-11-11'),
(12, '2222111133334444', 'LEO TAYLOR', '2027-12-22');
