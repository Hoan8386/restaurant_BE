package restaurant.example.restaurant.controller;

import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import restaurant.example.restaurant.domain.Dish;
import restaurant.example.restaurant.service.DishService;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class DishController {
    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @PostMapping("/dish")
    public ResponseEntity<Dish> postMethodName(@Valid @RequestBody Dish dish) {

        return ResponseEntity.status(HttpStatus.CREATED).body(this.dishService.handleCreatedDish(dish));
    }

}
