package restaurant.example.restaurant.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import restaurant.example.restaurant.domain.Category;
import restaurant.example.restaurant.service.CategoryService;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;

    }

    @PostMapping("/category")
    public ResponseEntity<Category> createdCategory(@Valid @RequestBody Category category) {

        return ResponseEntity.status(HttpStatus.CREATED).body(this.categoryService.handleCreatedCategory(category));
    }

}
