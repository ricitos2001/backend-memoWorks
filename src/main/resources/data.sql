DELETE FROM task_labels WHERE task_id = 1;
DELETE FROM task_labels WHERE task_id = 2;
DELETE FROM tasks WHERE id = 1;
DELETE FROM tasks WHERE id = 2;
DELETE FROM users WHERE id = 1;
ALTER TABLE users ALTER COLUMN password TYPE VARCHAR(255);

INSERT INTO users (id, name, surnames, username, phone_number, email, password, rol, avatar)
VALUES (1, 'usuario', 'usuario', 'usuario', '+34111111111', 'user@example.com', '$2a$10$OnYIEcds5O4V.c5pYMm8GuLncbMQGEIl4Upb0k3vFgTg2nD894vXq', 'USUARIO', '');

INSERT INTO tasks (id, title, description, date, time, user_id, status)
VALUES
    (1, 'MemoWorks', 'realizar proyecto MemoWorks', '2025-12-18', '18:45', 1, TRUE),
    (2, 'trabajo de diseño', 'realizar trabajo de diseño', '2025-12-18', '10:30',1, TRUE);

INSERT INTO task_labels (task_id, labels) VALUES
                                              (1, 'DWEC'),
                                              (1, 'DWES'),
                                              (1, 'DIW'),
                                              (1, 'DAW'),
                                              (2, 'DIW');
