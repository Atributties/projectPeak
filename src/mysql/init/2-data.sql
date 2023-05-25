USE projectPeak;

-- Users
INSERT INTO User (fullname, email, user_password) VALUES
('John Doe', 'john.doe@example.com', 'password1'),
('Jane Doe', 'jane.doe@example.com', 'password2');

-- Projects
INSERT INTO Project (name, description, start_date, end_date, user_id) VALUES
('Project 1', 'This is project 1', '2023-05-22', '2023-06-22', 1),
('Project 2', 'This is project 2', '2023-05-29', '2023-07-23', 2);

-- ProjectMember
INSERT INTO ProjectMember (project_id, user_id) VALUES
(1, 1),
(2, 2);

-- Tasks
INSERT INTO Task (name, description, start_date, end_date, status, project_id) VALUES
('Task', 'This is task 1.1 for project 1', '2023-05-22', '2023-05-26', 'Completed', 1),
('Task', 'This is task 1.2 for project 1', '2023-05-23', '2023-05-24', 'Completed', 1),
('Task', 'This is task 2.1 for project 2', '2023-05-29', '2023-06-15', 'In progress', 2);
-- Subtasks
INSERT INTO Subtask (name, description, start_date, end_date, status, task_id) VALUES
('Subtask 1.1.1', 'This is subtask 1 for task 1.1', '2023-05-22', '2023-05-23', 'Completed', 1),
('Subtask 1.1.2', 'This is subtask 2 for task 1.1', '2023-05-23', '2023-05-26', 'Completed', 1),
('Subtask 2.1.1', 'This is subtask 1 for task 2.1', '2023-02-01', '2023-02-15', 'In progress', 3);
