package restaurant.example.restaurant.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Người dùng đặt hàng (giả định đã có entity User)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Instant orderTime;

    private String status; // Ví dụ: PENDING, CONFIRMED, DELIVERING, COMPLETED, CANCELLED

    private Double totalPrice;

    private String deliveryAddress;

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
