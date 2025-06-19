package restaurant.example.restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import restaurant.example.restaurant.domain.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
}
