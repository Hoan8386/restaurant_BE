package restaurant.example.restaurant.service;

import org.springframework.stereotype.Service;

import restaurant.example.restaurant.domain.*;
import restaurant.example.restaurant.domain.response.ResOrder;
import restaurant.example.restaurant.repository.*;
import restaurant.example.restaurant.util.error.OrderException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
            OrderDetailRepository orderDetailRepository,
            UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.userRepository = userRepository;
    }

    /** ✅ Tạo đơn hàng từ giỏ hàng */
    public Order createOrderFromCart(Cart cart, String receiverName, String receiverPhone, String receiverAddress) {
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setReceiverName(receiverName);
        order.setReceiverPhone(receiverPhone);
        order.setReceiverAddress(receiverAddress);
        order.setStatus("PENDING");

        double total = 0;
        for (CartDetail item : cart.getCartDetails()) {
            total = total + item.getTotal();
        }
        order.setTotalPrice(total);
        order = orderRepository.save(order);

        for (CartDetail cartDetail : cart.getCartDetails()) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setDish(cartDetail.getDish());
            detail.setPrice(cartDetail.getPrice());
            detail.setQuantity(cartDetail.getQuantity());
            orderDetailRepository.save(detail);
        }
        return order;
    }

    /** ✅ Lấy tất cả đơn hàng */
    public List<ResOrder> getAllOrders() {
        List<Order> lst = orderRepository.findAll();
        List<ResOrder> lstRes = new ArrayList<>();
        for (Order item : lst) {
            ResOrder res = new ResOrder();
            res.setId(item.getId());
            res.setReceiverAddress(item.getReceiverAddress());
            res.setReceiverName(item.getReceiverName());
            res.setReceiverPhone(item.getReceiverPhone());
            res.setStatus(item.getStatus());
            res.setTotalPrice(item.getTotalPrice());
            lstRes.add(res);
        }
        return lstRes;

    }

    /** ✅ Lấy đơn hàng theo người dùng */
    public List<ResOrder> getOrdersByUser(String email) throws OrderException {
        User user = userRepository.findByEmail(email);
        List<Order> lst = orderRepository.findByUser(user);
        if (lst.isEmpty()) {
            throw new OrderException("Order is emty");
        }
        List<ResOrder> lstRes = new ArrayList<>();
        for (Order item : lst) {
            ResOrder res = new ResOrder();
            res.setId(item.getId());
            res.setReceiverAddress(item.getReceiverAddress());
            res.setReceiverName(item.getReceiverName());
            res.setReceiverPhone(item.getReceiverPhone());
            res.setStatus(item.getStatus());
            res.setTotalPrice(item.getTotalPrice());
            lstRes.add(res);
        }
        return lstRes;
    }

    /**
     * ✅ Lấy đơn hàng theo ID
     **/
    public ResOrder getOrderById(Long id) throws OrderException {
        Optional<Order> item = orderRepository.findById(id);
        Order order = new Order();
        if (item.isPresent()) {
            order = item.get();
        } else {
            throw new OrderException("Not found order");
        }
        ResOrder res = new ResOrder();
        res.setId(order.getId());
        res.setReceiverAddress(order.getReceiverAddress());
        res.setReceiverName(order.getReceiverName());
        res.setReceiverPhone(order.getReceiverPhone());
        res.setStatus(order.getStatus());
        res.setTotalPrice(order.getTotalPrice());
        return res;
    }

    /**
     * ✅ Cập nhật trạng thái đơn hàng
     * 
     * @throws OrderException
     */
    public ResOrder updateOrderStatus(Long id, String status) throws OrderException {
        Optional<Order> item = this.orderRepository.findById(id);
        if (!item.isPresent()) {
            throw new OrderException("Not found order");
        }
        Order order = new Order();
        order = item.get();
        order.setStatus(status);
        orderRepository.save(order);
        ResOrder res = new ResOrder();
        res.setId(order.getId());
        res.setReceiverAddress(order.getReceiverAddress());
        res.setReceiverName(order.getReceiverName());
        res.setReceiverPhone(order.getReceiverPhone());
        res.setStatus(order.getStatus());
        res.setTotalPrice(order.getTotalPrice());
        return res;
    }

    /**
     * ✅ Xóa đơn hàng
     */
    public void deleteOrderById(Long id) throws OrderException {
        if (!orderRepository.existsById(id)) {
            throw new OrderException("Order not found");
        }
        orderRepository.deleteById(id);
    }
}
