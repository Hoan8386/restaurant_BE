package restaurant.example.restaurant.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import restaurant.example.restaurant.domain.Order;
import restaurant.example.restaurant.domain.response.ResOrder;
import restaurant.example.restaurant.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /** ✅ 1. Hiển thị toàn bộ đơn hàng (admin/employee) */
    @GetMapping("/all")
    public ResponseEntity<List<ResOrder>> getAllOrders() {

        return ResponseEntity.ok(orderService.getAllOrders());
    }

    /** ✅ 2. Hiển thị đơn hàng của người dùng hiện tại */
    @GetMapping("/my")
    public ResponseEntity<List<ResOrder>> getUserOrders() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(orderService.getOrdersByUser(email));
    }

    /** ✅ 3. Lấy đơn hàng theo ID */
    @GetMapping("/{id}")
    public ResponseEntity<ResOrder> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    /** ✅ 4. Cập nhật trạng thái đơn hàng */
    @PutMapping("/status/{id}")
    public ResponseEntity<ResOrder> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam("status") String status) {
        ResOrder updatedOrder = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(updatedOrder);
    }

    /** ✅ 5. Xóa đơn hàng */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrderById(id);
        return ResponseEntity.noContent().build();
    }
}
