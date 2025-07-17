package restaurant.example.restaurant.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import restaurant.example.restaurant.controller.CartController;
import restaurant.example.restaurant.domain.Cart;
import restaurant.example.restaurant.domain.CartDetail;
import restaurant.example.restaurant.domain.Dish;
import restaurant.example.restaurant.domain.User;
import restaurant.example.restaurant.domain.response.ResCartItem;
import restaurant.example.restaurant.repository.CartDetailRepository;
import restaurant.example.restaurant.repository.CartRepository;
import restaurant.example.restaurant.repository.DishRepository;
import restaurant.example.restaurant.repository.UserRepository;
import restaurant.example.restaurant.util.error.CartException;

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
            // cart.setCheckedOut(false); // hoặc xử lý trạng thái tùy ý
            cartRepository.save(cart);
        }
    }

    public ResCartItem addToCart(CartDetail request, String email) {
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
        cartDetailRepository.save(detail);
        ResCartItem res = new ResCartItem();
        res.setId(detail.getId());
        res.setQuantity(detail.getQuantity());
        res.setPrice(detail.getPrice());
        res.setTotal(detail.getTotal());
        res.setName(detail.getDish().getName());
        res.setImageUrl(detail.getDish().getImageUrl());
        res.setCategoryName(detail.getDish().getCategory().getName());
        return res;

    }

    public List<ResCartItem> getCartItemsByUserEmail(String email) {
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
        List<CartDetail> lst = cartDetailRepository.findAllByCartId(cartOpt.get().getId());
        List<ResCartItem> lstRes = new ArrayList<>();
        for (CartDetail item : lst) {
            ResCartItem res = new ResCartItem();
            res.setId(item.getId());
            res.setQuantity(item.getQuantity());
            res.setPrice(item.getPrice());
            res.setTotal(item.getTotal());
            res.setName(item.getDish().getName());
            res.setImageUrl(item.getDish().getImageUrl());
            res.setCategoryName(item.getDish().getCategory().getName());
            lstRes.add(res);
        }

        return lstRes;
    }

    /** Cập nhật số lượng món trong giỏ hàng */
    public ResCartItem updateQuantity(Long cartItemId, long quantity) {
        CartDetail detail = cartDetailRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found with ID: " + cartItemId));

        detail.setQuantity(quantity);

        // Cập nhật lại total = price * quantity
        detail.setTotal(detail.getPrice() * quantity);
        cartDetailRepository.save(detail);
        ResCartItem res = new ResCartItem();
        res.setId(detail.getId());
        res.setQuantity(detail.getQuantity());
        res.setPrice(detail.getPrice());
        res.setTotal(detail.getTotal());
        res.setName(detail.getDish().getName());
        res.setImageUrl(detail.getDish().getImageUrl());
        res.setCategoryName(detail.getDish().getCategory().getName());
        return res;
    }

    /**
     * Xóa một món khỏi giỏ hàng
     * 
     * @throws CartException
     */
    public void removeItem(Long cartItemId) throws CartException {
        if (!cartDetailRepository.existsById(cartItemId)) {
            throw new CartException("Cart item not found with ID: " + cartItemId);
        }
        cartDetailRepository.deleteById(cartItemId);
    }

    public void save(Cart cart) {
        this.cartRepository.save(cart);
    }
}
