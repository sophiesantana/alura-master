package br.com.alura.ProjetoAlura.register;

import br.com.alura.ProjetoAlura.course.Course;
import br.com.alura.ProjetoAlura.course.Status;
import br.com.alura.ProjetoAlura.registration.NewRegistrationDTO;
import br.com.alura.ProjetoAlura.registration.RegistrationRepository;
import org.springframework.boot.test.context.SpringBootTest;

import br.com.alura.ProjetoAlura.course.CourseRepository;
import br.com.alura.ProjetoAlura.user.Role;
import br.com.alura.ProjetoAlura.user.User;
import br.com.alura.ProjetoAlura.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseRepository courseRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RegistrationRepository registrationRepository;

    @Test
    void shouldCreateRegistration() throws Exception {
        // Dados de entrada
        NewRegistrationDTO dto = new NewRegistrationDTO();

        dto.setCourseCode("COURSE1");
        dto.setStudentEmail("student@example.com");

        // Mock do curso
        Course course = new Course("COURSE1", "Course Name", "Description", new User("Instructor", "instructor@example.com", Role.INSTRUCTOR, "password"));
        course.setStatus(Status.ACTIVE);

        // Mock do estudante
        User student = new User("Student Name", "student@example.com", Role.STUDENT, "password");

        // Configuração dos mocks
        Mockito.when(courseRepository.findByCode(dto.getCourseCode())).thenReturn(course);
        Mockito.when(userRepository.findByEmail(dto.getStudentEmail())).thenReturn(student);
        Mockito.when(registrationRepository.existsByUserAndCourse(student, course)).thenReturn(false);

        // Teste do endpoint
        mockMvc.perform(post("/registration/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldNotCreateRegistrationIfCourseIsInactive() throws Exception {
        NewRegistrationDTO dto = new NewRegistrationDTO();

        dto.setCourseCode("COURSE1");
        dto.setStudentEmail("student@example.com");

        Course course = new Course("COURSE1", "Course Name", "Description", new User("Instructor", "instructor@example.com", Role.INSTRUCTOR, "password"));
        course.setStatus(Status.INACTIVE);

        Mockito.when(courseRepository.findByCode(dto.getCourseCode())).thenReturn(course);

        mockMvc.perform(post("/registration/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The course must be active"));
    }

    @Test
    void shouldNotCreateRegistrationIfUserIsAlreadyEnrolled() throws Exception {
        NewRegistrationDTO dto = new NewRegistrationDTO();

        dto.setCourseCode("COURSE1");
        dto.setStudentEmail("student@example.com");

        Course course = new Course("COURSE1", "Course Name", "Description", new User("Instructor", "instructor@example.com", Role.INSTRUCTOR, "password"));
        course.setStatus(Status.ACTIVE);

        User student = new User("Student Name", "student@example.com", Role.STUDENT, "password");

        Mockito.when(courseRepository.findByCode(dto.getCourseCode())).thenReturn(course);
        Mockito.when(userRepository.findByEmail(dto.getStudentEmail())).thenReturn(student);
        Mockito.when(registrationRepository.existsByUserAndCourse(student, course)).thenReturn(true);

        mockMvc.perform(post("/registration/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User already enrolled in this course"));
    }

    @Test
    void shouldReturnRegistrationReport() throws Exception {
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{"Course Name", "COURSE1", "Instructor Name", "instructor@example.com", 5L});
        mockResults.add(new Object[]{"Another Course", "COURSE2", "Another Instructor", "another_instructor@example.com", 3L});

        Mockito.when(registrationRepository.getCourseRegistrationReport()).thenReturn(mockResults);

        mockMvc.perform(get("/registration/report"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].courseName").value("Course Name"))
                .andExpect(jsonPath("$[0].courseCode").value("COURSE1"))
                .andExpect(jsonPath("$[0].instructorName").value("Instructor Name"))
                .andExpect(jsonPath("$[0].instructorEmail").value("instructor@example.com"))
                .andExpect(jsonPath("$[0].totalRegistrations").value(5))
                .andExpect(jsonPath("$[1].courseName").value("Another Course"))
                .andExpect(jsonPath("$[1].courseCode").value("COURSE2"))
                .andExpect(jsonPath("$[1].instructorName").value("Another Instructor"))
                .andExpect(jsonPath("$[1].instructorEmail").value("another_instructor@example.com"))
                .andExpect(jsonPath("$[1].totalRegistrations").value(3));
    }
}
