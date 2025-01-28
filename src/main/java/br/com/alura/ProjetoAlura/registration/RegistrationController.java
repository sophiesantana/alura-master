package br.com.alura.ProjetoAlura.registration;

import br.com.alura.ProjetoAlura.course.Course;
import br.com.alura.ProjetoAlura.course.CourseRepository;
import br.com.alura.ProjetoAlura.course.Status;
import br.com.alura.ProjetoAlura.user.Role;
import br.com.alura.ProjetoAlura.user.User;
import br.com.alura.ProjetoAlura.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class RegistrationController {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final RegistrationRepository registrationRepository;

    public RegistrationController(CourseRepository courseRepository, UserRepository userRepository, RegistrationRepository registrationRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.registrationRepository = registrationRepository;
    }

    @PostMapping("/registration/new")
    public ResponseEntity createCourse(@Valid @RequestBody NewRegistrationDTO newRegistration) {
        Course course = courseRepository.findByCode(newRegistration.getCourseCode());
        if (!course.getCode().equals(newRegistration.getCourseCode())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid code or not exists");
        }

        if (course.getStatus() != Status.ACTIVE) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The course must be active");
        }

        User user = userRepository.findByEmail(newRegistration.getStudentEmail());
        if (!user.getEmail().equals(newRegistration.getStudentEmail())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        if (registrationRepository.existsByUserAndCourse(user, course)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already enrolled in this course");
        }

        Registration registration = new Registration(user, course);
        registrationRepository.save(registration);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/registration/report")
    public ResponseEntity<List<RegistrationReportItem>> report() {

        List<Object[]> results = registrationRepository.getCourseRegistrationReport();

        List<RegistrationReportItem> items = results.stream()
                .map(result -> new RegistrationReportItem(
                        (String) result[0],
                        (String) result[1],
                        (String) result[2],
                        (String) result[3],
                        ((Number) result[4]).longValue()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(items);
    }

}
