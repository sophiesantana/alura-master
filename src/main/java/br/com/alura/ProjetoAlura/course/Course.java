package br.com.alura.ProjetoAlura.course;

import br.com.alura.ProjetoAlura.user.User;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @Pattern(regexp = "^[a-zA-Z\\-]{4,10}$", message = "Código inválido")
    private String code;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne
    private User instructor;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime inactiveAt;

    public Course() {}

    public Course(String code, String name, String description, User instructor) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.instructor = instructor;
        this.status = Status.ACTIVE;
        this.inactiveAt = null;
    }

    public String getCode() { return code; }

    public String getName() { return name; }

    public String getDescription() { return description; }

    public User getInstructor() { return instructor; }

    public Status getStatus() { return status; }

    public LocalDateTime getInactiveAt() { return inactiveAt; }
}

enum Status {
    ACTIVE, INACTIVE
}