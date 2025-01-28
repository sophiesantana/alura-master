CREATE TABLE Registration (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    registration_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES `User` (id),
    CONSTRAINT fk_course FOREIGN KEY (course_id) REFERENCES `Course` (id)
);
