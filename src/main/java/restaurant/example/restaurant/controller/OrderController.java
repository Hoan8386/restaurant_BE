// OrderController.java
package restaurant.example.restaurant.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import restaurant.example.restaurant.domain.Order;
import restaurant.example.restaurant.service.OrderService;

@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /** Người dùng tạo đơn hàng */
    @PostMapping("/order")
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Order created = orderService.createOrder(order, email);
        return ResponseEntity.ok(created);
    }

    /** Quản trị viên / nhân viên lấy toàn bộ đơn hàng */
    @GetMapping("/get-all-order")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    /** Cập nhật trạng thái đơn hàng */
    @PutMapping("/order/status/{id}")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam("status") String status) {
        Order updatedOrder = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(updatedOrder);
    }

    /** Người dùng xem đơn hàng của họ */
    @GetMapping("/list-order")
    public ResponseEntity<List<Order>> getUserOrders() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(orderService.getOrdersByUser(email));
    }

    /** Lấy chi tiết đơn hàng theo ID */
    @GetMapping("/order/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }
}
