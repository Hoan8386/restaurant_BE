package restaurant.example.restaurant.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import restaurant.example.restaurant.domain.Order;
import restaurant.example.restaurant.domain.User;
import restaurant.example.restaurant.repository.OrderRepository;
import restaurant.example.restaurant.repository.UserRepository;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    /** Tạo đơn hàng mới */
    public Order createOrder(Order order, String email) {
        User user = userRepository.findByEmail(email);
        order.setUser(user);
        return orderRepository.save(order);
    }

    /** Lấy tất cả đơn hàng (cho admin hoặc nhân viên) */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /** Lấy đơn hàng của user hiện tại */
    public List<Order> getOrdersByUser(String email) {
        User user = userRepository.findByEmail(email);
        return orderRepository.findByUserId(user.getId());
    }

    /** Lấy đơn hàng theo ID */
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    /** (Tuỳ chọn) Cập nhật trạng thái đơn hàng */
    public Order updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null) {
            order.setStatus(status);
            return orderRepository.save(order);
        }
        return null;
    }

    /** (Tuỳ chọn) Xoá đơn hàng */
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}
