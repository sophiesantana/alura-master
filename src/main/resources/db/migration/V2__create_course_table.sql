CREATE TABLE Course (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    inactivation_date DATETIME DEFAULT NULL,
    instructor_id BIGINT NOT NULL,
    CONSTRAINT fk_instructor FOREIGN KEY (instructor_id) REFERENCES User(id)
);
