package restaurant.example.restaurant.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import restaurant.example.restaurant.domain.Order;
import restaurant.example.restaurant.domain.User;
import org.springframework.data.domain.Page;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);

    List<Order> findByUser(User user);

    Page<Order> findByUser(User user, Specification spec, Pageable pageable);

}
