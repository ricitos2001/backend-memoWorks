INSERT INTO users (id, name, surnames, username, phone_number, email, password, rol)
VALUES (1, 'Cesar Gabriel', 'Ucha Sousa', 'ricitos2001', '+34627198083', 'cesar@example.com', '$2a$10$xkNM.N2UiMlUP.uQjCvN6.rUi22jOsQo/6PmAY9yIVjSu6VzkT6ty', 'USUARIO');

INSERT INTO tasks (id, title, description, date, assigment_for_id, status, labels)
VALUES (1, 'MemoWorks', 'realizar proyecto MemoWorks', '2025-12-18 10:30:00', 1, TRUE, 'DWEC, DWES, DIW, DAW'),
       (2, 'trabajo de diseño', 'realizar trabajo de diseño', '2025-12-18 10:30:00', 1, TRUE, 'DIW')
