package restaurant.example.restaurant.controller;

import org.springframework.web.bind.annotation.RestController;

import restaurant.example.restaurant.domain.User;
import restaurant.example.restaurant.service.UserService;
import restaurant.example.restaurant.util.error.IdInvalidException;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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
    @PostMapping("/users")
    public ResponseEntity<User> createNewUser(@RequestBody User newUser) {

        User user = this.userService.CreateUser(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    // get user
    @GetMapping("/users/{id}")
    public ResponseEntity<Optional<User>> getUserById(@PathVariable("id") Long id) throws IdInvalidException {

        if (id > 10) {
            throw new IdInvalidException("Không lớn hơn 10");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.handelGetUser(id));
    }

    // get user
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUser() {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.handelGetAllUser());
    }

    // delete user
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        this.userService.handelDeleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // update user
    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@RequestBody User updateUser) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.handelUpdateUser(updateUser));
    }

}
