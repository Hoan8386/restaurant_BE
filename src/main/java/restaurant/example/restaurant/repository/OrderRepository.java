package restaurant.example.restaurant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import restaurant.example.restaurant.domain.Order;
import restaurant.example.restaurant.domain.User;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);

    List<Order> findByUser(User user);
}
