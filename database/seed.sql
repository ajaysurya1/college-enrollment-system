USE university_academic_system;

INSERT INTO departments (department_id, department_code, department_name) VALUES
(1, 'CS', 'Computer Science and Engineering'),
(2, 'MATH', 'Mathematics'),
(3, 'EE', 'Electrical Engineering')
ON DUPLICATE KEY UPDATE department_name = VALUES(department_name);

INSERT INTO users (user_id, username, password_hash, full_name, email, role, department_id, is_active) VALUES
(1, 'admin', '$2a$10$uy1i/2ryPL3dSa2cDmx99uLkhkIjkVZOWnnEHrsyPWumNHkI/ohii', 'System Administrator', 'admin@university.edu', 'ADMIN', 1, TRUE),
(2, 'prof.smith', '$2a$10$QjmeBmqAvygkHclaXGqqces1wSFhscH388jWeBESAs.Debt0/P7Xi', 'Prof. John Smith', 'john.smith@university.edu', 'FACULTY', 1, TRUE),
(3, 'dr.watson', '$2a$10$QjmeBmqAvygkHclaXGqqces1wSFhscH388jWeBESAs.Debt0/P7Xi', 'Dr. Emma Watson', 'emma.watson@university.edu', 'FACULTY', 2, TRUE),
(4, 'prof.alan', '$2a$10$QjmeBmqAvygkHclaXGqqces1wSFhscH388jWeBESAs.Debt0/P7Xi', 'Prof. Alan Turing', 'alan.turing@university.edu', 'FACULTY', 3, TRUE),
(5, 'student.alice', '$2a$10$KQ2ye8zf7r1L8PEbe6iavumwLi9XpBXJZdqpP4n2xfeuIcXphNO4y', 'Alice Johnson', 'alice.johnson@university.edu', 'STUDENT', 1, TRUE),
(6, 'student.bob', '$2a$10$KQ2ye8zf7r1L8PEbe6iavumwLi9XpBXJZdqpP4n2xfeuIcXphNO4y', 'Bob Williams', 'bob.williams@university.edu', 'STUDENT', 1, TRUE),
(7, 'student.charlie', '$2a$10$KQ2ye8zf7r1L8PEbe6iavumwLi9XpBXJZdqpP4n2xfeuIcXphNO4y', 'Charlie Brown', 'charlie.brown@university.edu', 'STUDENT', 2, TRUE),
(8, 'student.diana', '$2a$10$KQ2ye8zf7r1L8PEbe6iavumwLi9XpBXJZdqpP4n2xfeuIcXphNO4y', 'Diana Prince', 'diana.prince@university.edu', 'STUDENT', 3, TRUE),
(9, 'student.evan', '$2a$10$KQ2ye8zf7r1L8PEbe6iavumwLi9XpBXJZdqpP4n2xfeuIcXphNO4y', 'Evan Wright', 'evan.wright@university.edu', 'STUDENT', 1, TRUE)
ON DUPLICATE KEY UPDATE full_name = VALUES(full_name), password_hash = VALUES(password_hash);

INSERT INTO faculty (user_id, designation) VALUES
(2, 'Associate Professor'),
(3, 'Assistant Professor'),
(4, 'Professor')
ON DUPLICATE KEY UPDATE designation = VALUES(designation);

INSERT INTO students (user_id, roll_number, programme, year_of_study) VALUES
(5, 'CS2025001', 'B.Tech Computer Science', 2),
(6, 'CS2024002', 'B.Tech Computer Science', 3),
(7, 'MA2026003', 'B.Sc Mathematics', 1),
(8, 'EE2025004', 'B.Tech Electrical Engineering', 2),
(9, 'CS2026005', 'B.Tech Computer Science', 1)
ON DUPLICATE KEY UPDATE programme = VALUES(programme), year_of_study = VALUES(year_of_study);

INSERT INTO courses (course_id, course_code, course_title, credits, department_id, description) VALUES
(1, 'CS101', 'Introduction to Programming', 4, 1, 'Fundamental concepts of programming using Java and basic algorithms.'),
(2, 'CS201', 'Data Structures and Algorithms', 4, 1, 'Stacks, queues, linked lists, trees, graphs, sorting and searching.'),
(3, 'CS301', 'Database Management Systems', 4, 1, 'Relational models, SQL queries, normalization, transactions and JDBC.'),
(4, 'MATH101', 'Calculus I', 3, 2, 'Differential and integral calculus with engineering applications.'),
(5, 'EE101', 'Basic Circuit Analysis', 3, 3, 'Kirchhoff laws, nodal analysis, AC/DC circuit behaviors and theorems.')
ON DUPLICATE KEY UPDATE course_title = VALUES(course_title), credits = VALUES(credits);

INSERT INTO course_prerequisites (course_id, prerequisite_course_id) VALUES
(2, 1),
(3, 2)
ON DUPLICATE KEY UPDATE prerequisite_course_id = VALUES(prerequisite_course_id);

INSERT INTO semesters (semester_id, semester_name, academic_year, start_date, end_date, registration_start, registration_end) VALUES
(1, 'Fall 2026', '2026-2027', '2026-08-01', '2026-12-15', '2026-07-01', '2026-08-10'),
(2, 'Spring 2027', '2026-2027', '2027-01-10', '2027-05-25', '2026-10-01', '2027-03-01')
ON DUPLICATE KEY UPDATE semester_name = VALUES(semester_name), registration_start = VALUES(registration_start), registration_end = VALUES(registration_end);

INSERT INTO course_offerings (offering_id, course_id, semester_id, faculty_id, section, capacity) VALUES
(1, 1, 1, 2, 'A', 30),
(2, 1, 2, 2, 'A', 35),
(3, 2, 2, 2, 'A', 30),
(4, 3, 2, 2, 'A', 25),
(5, 4, 2, 3, 'A', 40),
(6, 5, 2, 4, 'A', 30)
ON DUPLICATE KEY UPDATE capacity = VALUES(capacity);

INSERT INTO enrollments (enrollment_id, student_id, offering_id, enrollment_date, status, grade) VALUES
(1, 5, 1, '2026-08-05', 'COMPLETED', 'A'),
(2, 6, 1, '2026-08-05', 'COMPLETED', 'A'),
(3, 8, 1, '2026-08-05', 'COMPLETED', 'B+')
ON DUPLICATE KEY UPDATE status = VALUES(status), grade = VALUES(grade);
