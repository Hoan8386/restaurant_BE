package restaurant.example.restaurant.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import restaurant.example.restaurant.domain.Cart;
import restaurant.example.restaurant.domain.CartDetail;
import restaurant.example.restaurant.domain.User;
import restaurant.example.restaurant.domain.request.CartItemUpdate;
import restaurant.example.restaurant.domain.response.ResCartDTO;
import restaurant.example.restaurant.service.CartDetailService;
import restaurant.example.restaurant.service.CartService;
import restaurant.example.restaurant.service.UserService;
import restaurant.example.restaurant.util.anotation.ApiMessage;

@RestController
@RequestMapping("/cart") // Base path cho tất cả API giỏ hàng
public class CartController {

    private final UserService userService;
    private final CartService cartService;
    private final CartDetailService cartDetailService;

    public CartController(UserService userService, CartService cartService, CartDetailService cartDetailService) {
        this.userService = userService;
        this.cartService = cartService;
        this.cartDetailService = cartDetailService;
    }

    /** Lấy giỏ hàng của người dùng hiện tại */
    @GetMapping
    @ApiMessage("Get cart")
    public ResponseEntity<ResCartDTO> getCart() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.handelGetUserByUsername(username);
        Cart cart = cartService.getCartById(currentUser.getId());
        ResCartDTO resCartDTO = new ResCartDTO();
        resCartDTO.setTotalItems(cart.getCartDetails().size());
        double totalPrice = cart.getCartDetails().stream()
                .mapToDouble(CartDetail::getTotal)
                .sum();
        resCartDTO.setId(cart.getId());
        resCartDTO.setTotalPrice(totalPrice);
        return ResponseEntity.ok(resCartDTO);
    }

    /** Xóa toàn bộ giỏ hàng */
    @DeleteMapping
    @ApiMessage("Delete cart")
    public ResponseEntity<ResCartDTO> clearCart() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.handelGetUserByUsername(username);
        Cart cart = cartService.getCartById(currentUser.getId());

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

    /** Thêm món vào giỏ hàng */
    @PostMapping("/add-dish")
    @ApiMessage("add item in cart")
    public ResponseEntity<CartDetail> addToCart(@RequestBody CartDetail request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        CartDetail item = cartService.addToCart(request, email);
        return ResponseEntity.ok(item);
    }

    /** Lấy tất cả món trong giỏ hàng của user hiện tại */
    @GetMapping("/get-all-dish")
    @ApiMessage("get all item")
    public ResponseEntity<List<CartDetail>> getCartItems() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<CartDetail> items = cartService.getCartItemsByUserEmail(email);
        return ResponseEntity.ok(items);
    }

    /** Cập nhật số lượng món trong giỏ hàng */
    @PutMapping("/update-dish")
    @ApiMessage("update quantity")
    public ResponseEntity<CartDetail> updateQuantity(@RequestBody CartItemUpdate dto) {
        CartDetail updated = cartService.updateQuantity(dto.getId(), dto.getQuantity());
        return ResponseEntity.ok(updated);
    }

    /** Xóa một món khỏi giỏ hàng */
    @DeleteMapping("/delete-dish/{id}")
    @ApiMessage("delete item")
    public ResponseEntity<Void> deleteCartItem(@PathVariable("id") Long cartItemId) {
        cartService.removeItem(cartItemId);
        return ResponseEntity.noContent().build();
    }
}
