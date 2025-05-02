package restaurant.example.restaurant.controller;

import org.springframework.web.bind.annotation.RestController;

import restaurant.example.restaurant.domain.User;
import restaurant.example.restaurant.service.UserService;

import java.util.Optional;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
public class UserController {

    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // create user
    @PostMapping("/user")
    public User createNewUser(@RequestBody User newUser) {

        return this.userService.CreateUser(newUser);
    }

    // get user
    @GetMapping("/user/{id}")
    public Optional<User> getUserById(@PathVariable("id") Long id) {
        return this.userService.handelGetUser(id);
    }

    // delete user
    @DeleteMapping("/user/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        this.userService.handelDeleteUser(id);
        return "xóa thành công";
    }

    // update user
    @PutMapping("/user")
    public User updateUser(@RequestBody User updateUser) {
        return this.userService.handelUpdateUser(updateUser);
    }

}
