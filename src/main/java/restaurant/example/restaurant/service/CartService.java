package restaurant.example.restaurant.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import restaurant.example.restaurant.controller.CartController;
import restaurant.example.restaurant.domain.Cart;
import restaurant.example.restaurant.domain.CartDetail;
import restaurant.example.restaurant.domain.User;
import restaurant.example.restaurant.repository.CartDetailRepository;
import restaurant.example.restaurant.repository.CartRepository;
import restaurant.example.restaurant.repository.UserRepository;

@Service
public class CartService {

    private final UserService userService;
    private final UserRepository userRepository;
    public final CartRepository cartRepository;
    public final CartDetailRepository cartDetailRepository;

    public CartService(CartRepository cartRepository, UserService userService, UserRepository userRepository,
            CartDetailRepository cartDetailRepository) {
        this.cartRepository = cartRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.cartDetailRepository = cartDetailRepository;
    }

    public Cart getCartById(Long id) {
        if (this.cartRepository.findById(id).isPresent()) {
            return this.cartRepository.findById(id).get();
        }
        return null;
    }

    public void clearCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId).orElse(null);
        if (cart != null) {
            cart.setCheckedOut(false); // hoặc xử lý trạng thái tùy ý
            cartRepository.save(cart);
        }
    }

    public CartDetail addToCart(CartDetail request, String email) {
        // 1. Tìm User theo email
        User user = this.userRepository.findByEmail(email);

        // 2. Tìm hoặc tạo Cart của user
        Cart cart = cartRepository.findByUserId(user.getId()).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });

        // 3. Lấy đơn giá món ăn
        double unitPrice = request.getDish().getPrice();

        // 4. Kiểm tra xem món đã tồn tại trong cart chưa
        Optional<CartDetail> existingDetailOpt = cartDetailRepository
                .findByCartIdAndDishId(cart.getId(), request.getDish().getId());

        CartDetail detail;
        if (existingDetailOpt.isPresent()) {
            // Đã có món, cộng dồn số lượng
            detail = existingDetailOpt.get();
            long newQuantity = detail.getQuantity() + request.getQuantity();
            detail.setQuantity(newQuantity);
            detail.setPrice(unitPrice);
            detail.setTotal(unitPrice * newQuantity);
        } else {
            // Món mới
            detail = new CartDetail();
            detail.setCart(cart);
            detail.setDish(request.getDish());
            detail.setQuantity(request.getQuantity());
            detail.setPrice(unitPrice);
            detail.setTotal(unitPrice * request.getQuantity());
        }

        return cartDetailRepository.save(detail);
    }

    public List<CartDetail> getCartItemsByUserEmail(String email) {
        // 1. Lấy user theo email
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found with email: " + email);
        }

        // 2. Lấy cart theo userId
        Optional<Cart> cartOpt = cartRepository.findByUserId(user.getId());
        if (cartOpt.isEmpty()) {
            throw new RuntimeException("Cart not found for user: " + user.getEmail());
        }

        // 3. Lấy các CartDetail theo cartId
        return cartDetailRepository.findAllByCartId(cartOpt.get().getId());
    }

    /** Cập nhật số lượng món trong giỏ hàng */
    public CartDetail updateQuantity(Long cartItemId, long quantity) {
        CartDetail detail = cartDetailRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found with ID: " + cartItemId));

        detail.setQuantity(quantity);

        // Cập nhật lại total = price * quantity
        detail.setTotal(detail.getPrice() * quantity);

        return cartDetailRepository.save(detail);
    }

    /** Xóa một món khỏi giỏ hàng */
    public void removeItem(Long cartItemId) {
        if (!cartDetailRepository.existsById(cartItemId)) {
            throw new RuntimeException("Cart item not found with ID: " + cartItemId);
        }
        cartDetailRepository.deleteById(cartItemId);
    }
}
