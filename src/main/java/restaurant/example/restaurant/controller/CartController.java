package restaurant.example.restaurant.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import restaurant.example.restaurant.domain.Cart;
import restaurant.example.restaurant.domain.CartDetail;
import restaurant.example.restaurant.domain.User;
import restaurant.example.restaurant.domain.response.ResCartDTO;
import restaurant.example.restaurant.service.CartDetailService;
import restaurant.example.restaurant.service.CartService;
import restaurant.example.restaurant.service.UserService;
import restaurant.example.restaurant.util.anotation.ApiMessage;

import org.springframework.security.core.Authentication;

@RestController
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
    @GetMapping("/cart")
    @ApiMessage("Get cart")
    public ResponseEntity<ResCartDTO> getCart() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.handelGetUserByUsername(username);
        Cart cart = cartService.getCartById(currentUser.getId());
        ResCartDTO resCartDTO = new ResCartDTO();
        resCartDTO.setTotalItems(cart.getCartDetails().size());
        List<CartDetail> lstCart = cart.getCartDetails();
        double totalPrice = 0;
        for (CartDetail cartDetail : lstCart) {
            totalPrice += cartDetail.getTotal();
        }
        resCartDTO.setId(cart.getId());
        resCartDTO.setTotalPrice(totalPrice);
        return ResponseEntity.ok(resCartDTO);
    }

    @DeleteMapping("/cart")
    @ApiMessage("Delete cart")
    public ResponseEntity<ResCartDTO> clearCart() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.handelGetUserByUsername(username);
        Cart cart = cartService.getCartById(currentUser.getId());
        ResCartDTO resCartDTO = new ResCartDTO();

        List<CartDetail> lst = cart.getCartDetails();
        for (CartDetail cartDetail : lst) {
            this.cartDetailService.handleDeleteByID(cartDetail.getId());
        }

        resCartDTO.setTotalItems(0);
        resCartDTO.setTotalPrice(0);
        resCartDTO.setId(cart.getId());
        return ResponseEntity.ok(resCartDTO);
    }

}
