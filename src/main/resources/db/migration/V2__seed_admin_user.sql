-- V2: Seed default admin user
-- Password is BCrypt encoded value of 'admin123'
INSERT INTO users (username, email, password, first_name, last_name, role, active)
VALUES ('admin', 'admin@realestate.com', '$2a$10$2a2CAUzkSMl50aLbmGfs1.FDDvEkq4TKzCraq1QuLd7g.qIooVhky', 'System', 'Admin', 'ADMIN', true);
