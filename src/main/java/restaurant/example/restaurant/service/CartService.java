package restaurant.example.restaurant.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import restaurant.example.restaurant.controller.CartController;
import restaurant.example.restaurant.domain.Cart;
import restaurant.example.restaurant.domain.CartDetail;
import restaurant.example.restaurant.domain.Dish;
import restaurant.example.restaurant.domain.User;
import restaurant.example.restaurant.repository.CartDetailRepository;
import restaurant.example.restaurant.repository.CartRepository;
import restaurant.example.restaurant.repository.DishRepository;
import restaurant.example.restaurant.repository.UserRepository;

@Service
public class CartService {

    private final UserService userService;
    private final UserRepository userRepository;
    public final CartRepository cartRepository;
    public final CartDetailRepository cartDetailRepository;
    public final DishRepository dishRepository;

    public CartService(CartRepository cartRepository, UserService userService, UserRepository userRepository,
            CartDetailRepository cartDetailRepository, DishRepository dishRepository) {
        this.cartRepository = cartRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.dishRepository = dishRepository;
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
        // 1. Tìm user
        User user = this.userRepository.findByEmail(email);

        // 2. Tìm hoặc tạo cart
        Cart cart = cartRepository.findByUserId(user.getId()).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });

        // ⚠️ 3. Lấy Dish từ database theo ID
        Long dishId = request.getDish().getId();
        Dish dish = this.dishRepository.findById(dishId)
                .orElseThrow(() -> new RuntimeException("Dish not found with id: " + dishId));

        double unitPrice = dish.getPrice();

        // 4. Kiểm tra món đã có trong giỏ chưa
        Optional<CartDetail> existingDetailOpt = cartDetailRepository
                .findByCartIdAndDishId(cart.getId(), dishId);

        CartDetail detail;
        if (existingDetailOpt.isPresent()) {
            detail = existingDetailOpt.get();
            long newQuantity = detail.getQuantity() + request.getQuantity();
            detail.setQuantity(newQuantity);
            detail.setPrice(unitPrice);
            detail.setTotal(unitPrice * newQuantity);
        } else {
            detail = new CartDetail();
            detail.setCart(cart);
            detail.setDish(dish); // gán dish đã load đầy đủ
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
