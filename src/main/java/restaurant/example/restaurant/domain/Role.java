package restaurant.example.restaurant.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;         // Ví dụ: ROLE_USER, ROLE_ADMIN

    private String description;

    // Nếu bạn dùng auditing cũng có thể thêm các trường này:
    private java.time.Instant createdAt;
    private java.time.Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @PrePersist
    public void prePersist() {
        this.createdAt = java.time.Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = java.time.Instant.now();
    }
}
