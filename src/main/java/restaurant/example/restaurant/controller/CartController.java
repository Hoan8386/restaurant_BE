package restaurant.example.restaurant.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import restaurant.example.restaurant.domain.Cart;
import restaurant.example.restaurant.domain.CartDetail;
import restaurant.example.restaurant.domain.Order;
import restaurant.example.restaurant.domain.User;
import restaurant.example.restaurant.domain.request.CartItemUpdate;
import restaurant.example.restaurant.domain.request.CheckoutRequest;
import restaurant.example.restaurant.domain.response.ResCartItem;
import restaurant.example.restaurant.domain.response.ResOrder;
import restaurant.example.restaurant.domain.response.ResCartDTO;
import restaurant.example.restaurant.service.CartDetailService;
import restaurant.example.restaurant.service.CartService;
import restaurant.example.restaurant.service.OrderService;
import restaurant.example.restaurant.service.UserService;
import restaurant.example.restaurant.util.anotation.ApiMessage;
import restaurant.example.restaurant.util.error.CartException;

@RestController
@RequestMapping("/cart") // Base path cho tất cả API giỏ hàng
public class CartController {

    private final UserService userService;
    private final CartService cartService;
    private final CartDetailService cartDetailService;
    private final OrderService orderService;

    public CartController(UserService userService, CartService cartService, CartDetailService cartDetailService,
            OrderService orderService) {
        this.userService = userService;
        this.cartService = cartService;
        this.cartDetailService = cartDetailService;
        this.orderService = orderService;
    }

    /**
     * Lấy giỏ hàng của người dùng hiện tại
     * 
     * 
     */
    @GetMapping
    @ApiMessage("Get cart")
    public ResponseEntity<ResCartDTO> getCart() throws CartException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.handelGetUserByUsername(username);

        Cart cart = cartService.getCartById(currentUser.getId());
        if (cart == null) {
            throw new CartException("Not found cart");
        }
        ResCartDTO resCartDTO = new ResCartDTO();
        resCartDTO.setTotalItems(cart.getCartDetails().size());
        double totalPrice = cart.getCartDetails().stream()
                .mapToDouble(CartDetail::getTotal)
                .sum();
        resCartDTO.setId(cart.getId());
        resCartDTO.setTotalPrice(totalPrice);
        return ResponseEntity.ok(resCartDTO);
    }

    /**
     * Xóa toàn bộ giỏ hàng
     * 
     * 
     */
    @DeleteMapping
    @ApiMessage("Delete cart")
    public ResponseEntity<ResCartDTO> clearCart() throws CartException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.handelGetUserByUsername(username);
        Cart cart = cartService.getCartById(currentUser.getId());
        if (cart == null) {
            throw new CartException("Not found cart");
        }
        List<CartDetail> lst = cart.getCartDetails();
        for (CartDetail cartDetail : lst) {
            this.cartDetailService.handleDeleteByID(cartDetail.getId());
        }

        ResCartDTO resCartDTO = new ResCartDTO();
        resCartDTO.setTotalItems(0);
        resCartDTO.setTotalPrice(0);
        resCartDTO.setId(cart.getId());
        return ResponseEntity.ok(resCartDTO);
    }

    /**
     * Thêm món vào giỏ hàng
     * 
     * 
     */
    @PostMapping("/add-dish")
    @ApiMessage("add item in cart")
    public ResponseEntity<ResCartItem> addToCart(@RequestBody CartDetail request) throws CartException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        ResCartItem item = cartService.addToCart(request, email);
        if (item == null) {
            throw new CartException("error add item");
        }
        return ResponseEntity.ok(item);
    }

    /**
     * Lấy tất cả món trong giỏ hàng của user hiện tại
     * 
     * 
     */
    @GetMapping("/get-all-dish")
    @ApiMessage("get all item")
    public ResponseEntity<List<ResCartItem>> getCartItems() throws CartException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<ResCartItem> lstRes = cartService.getCartItemsByUserEmail(email);
        if (lstRes.isEmpty()) {
            throw new CartException("Not item in my cart");
        }
        return ResponseEntity.ok(lstRes);
    }

    /**
     * Cập nhật số lượng món trong giỏ hàng
     * 
     * 
     */
    @PutMapping("/update-dish")
    @ApiMessage("update quantity")
    public ResponseEntity<ResCartItem> updateQuantity(@RequestBody CartItemUpdate dto) throws CartException {
        ResCartItem updated = cartService.updateQuantity(dto.getId(), dto.getQuantity());
        if (updated == null) {
            throw new CartException("Update fall");
        }
        return ResponseEntity.ok(updated);
    }

    /**
     * Xóa một món khỏi giỏ hàng
     * 
     * 
     */
    @DeleteMapping("/delete-dish/{id}")
    @ApiMessage("delete item")
    public ResponseEntity<Void> deleteCartItem(@PathVariable("id") Long cartItemId) throws CartException {
        cartService.removeItem(cartItemId);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/checkout")
    @ApiMessage("Checkout cart")
    public ResponseEntity<ResOrder> checkoutCart(@RequestBody CheckoutRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.handelGetUserByUsername(username);
        Cart cart = cartService.getCartById(user.getId());

        // Tạo order từ cart
        Order order = orderService.createOrderFromCart(cart, request.getReceiverName(),
                request.getReceiverPhone(), request.getReceiverAddress());

        // Xóa cartDetail sau khi checkout
        for (CartDetail detail : cart.getCartDetails()) {
            cartDetailService.handleDeleteByID(detail.getId());
        }

        cart.setCheckedOut(true);
        cartService.save(cart); // hoặc cartRepository.save(cart)
        ResOrder res = new ResOrder();
        res.setId(order.getId());
        res.setReceiverAddress(order.getReceiverAddress());
        res.setReceiverName(order.getReceiverName());
        res.setReceiverPhone(order.getReceiverPhone());
        res.setStatus(order.getStatus());
        res.setTotalPrice(order.getTotalPrice());
        return ResponseEntity.ok(res);
    }

}
