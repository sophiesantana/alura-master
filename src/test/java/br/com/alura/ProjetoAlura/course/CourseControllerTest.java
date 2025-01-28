package br.com.alura.ProjetoAlura.course;

import br.com.alura.ProjetoAlura.course.CourseRepository;
import br.com.alura.ProjetoAlura.course.NewCourseDTO;
import br.com.alura.ProjetoAlura.user.Role;
import br.com.alura.ProjetoAlura.user.User;
import br.com.alura.ProjetoAlura.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseRepository courseRepository;

    @MockBean
    private UserRepository userRepository;

    @Test
    void shouldCreateCourse() throws Exception {
        NewCourseDTO dto = new NewCourseDTO();
        dto.setCode("COURSE1");
        dto.setName("Course Name");
        dto.setDescription("Description");
        dto.setInstructorEmail("instructor@example.com");

        User instructor = new User("Instructor Name", "instructor@example.com", Role.INSTRUCTOR, "password123");

        Mockito.when(courseRepository.existsByCode(dto.getCode())).thenReturn(false);
        Mockito.when(userRepository.findByEmail(dto.getInstructorEmail())).thenReturn(instructor);

        mockMvc.perform(post("/course/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldNotCreateCourseWithDuplicateCode() throws Exception {
        NewCourseDTO dto = new NewCourseDTO();
        dto.setCode("COURSE1");
        dto.setName("Course Name");
        dto.setDescription("Description");
        dto.setInstructorEmail("instructor@example.com");

        Mockito.when(courseRepository.existsByCode(dto.getCode())).thenReturn(true);

        mockMvc.perform(post("/course/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Code must be unique"));
    }
}
