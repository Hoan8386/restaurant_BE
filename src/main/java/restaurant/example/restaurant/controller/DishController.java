package restaurant.example.restaurant.controller;

import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import restaurant.example.restaurant.domain.Dish;
import restaurant.example.restaurant.service.DishService;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class DishController {
    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @PostMapping("/dish")
    public ResponseEntity<Dish> createDish(@Valid @RequestBody Dish dish) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.dishService.handleCreatedDish(dish));
    }

    @GetMapping("/dish/{id}")
    public ResponseEntity<Dish> getDishById(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(this.dishService.handleGetDishById(id).isPresent() ? this.dishService.handleGetDishById(id).get()
                        : null);
    }

    @GetMapping("/dish")
    public ResponseEntity<List<Dish>> getAllDish(
            @RequestParam("current") Optional<String> currentOptional,
            @RequestParam("pageSize") Optional<String> pageSizeOptional) {
        String sCurrent = currentOptional.isPresent() ? currentOptional.get() : "";
        String sPageSizeOptional = pageSizeOptional.isPresent() ? pageSizeOptional.get() : "";

        int current = Integer.parseInt(sCurrent);
        int pageSize = Integer.parseInt(sPageSizeOptional);
        Pageable pageable = PageRequest.of(current - 1, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(this.dishService.handleGetAllDish(pageable));
    }

    @PutMapping("/dish")
    public ResponseEntity<Dish> updateDish(@Valid @RequestBody Dish dish) {
        return ResponseEntity.status(HttpStatus.OK).body(this.dishService.handleUpdateDish(dish));
    }

    @DeleteMapping("/dish/{id}")
    public ResponseEntity<Void> deleteDishById(@PathVariable("id") Long id) {
        this.dishService.handleDeleteDishById(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
