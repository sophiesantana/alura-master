package br.com.alura.ProjetoAlura.course;

import br.com.alura.ProjetoAlura.user.Role;
import br.com.alura.ProjetoAlura.user.User;
import br.com.alura.ProjetoAlura.user.UserRepository;
import br.com.alura.ProjetoAlura.util.ErrorItemDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class CourseController {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public CourseController(
            CourseRepository courseRepository,
            UserRepository userRepository
    ) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/course/new")
    public ResponseEntity createCourse(@Valid @RequestBody NewCourseDTO newCourse) {
        if (courseRepository.existsByCode(newCourse.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Code must be unique");
        }

        User instructor = userRepository.findByEmail(newCourse.getInstructorEmail());
        if (instructor == null || instructor.getRole() != Role.INSTRUCTOR) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid instructor");
        }

        if (!instructor.getEmail().equals(newCourse.getInstructorEmail())) {
            System.out.println(newCourse.getInstructorEmail());
            System.out.println(instructor.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid instructor email");
        }

        Course course = new Course(
                newCourse.getCode(),
                newCourse.getName(),
                newCourse.getDescription(),
                instructor
        );

        courseRepository.save(course);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/course/{code}/inactive")
    public ResponseEntity createCourse(@PathVariable("code") String courseCode) {
        Course course = courseRepository.findByCode(courseCode);

        if (!course.getCode().equals(courseCode)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid code or not exists");
        }

        course.setStatus(Status.INACTIVE);
        course.setInactivation_date(LocalDateTime.now());

        courseRepository.save(course);

        return ResponseEntity.status(HttpStatus.OK).body("Course successfully inactivated!");
    }

}
