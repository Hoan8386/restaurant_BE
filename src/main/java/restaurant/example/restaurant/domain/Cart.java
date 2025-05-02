package restaurant.example.restaurant.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "carts")
@Getter
@Setter
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Người sở hữu giỏ hàng
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Boolean checkedOut = false;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;


    @PrePersist
    public void handleBeforeCreate() {
        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedAt = Instant.now();
    }
}
