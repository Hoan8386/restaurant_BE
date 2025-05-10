package restaurant.example.restaurant.service;

import org.springframework.stereotype.Service;

import restaurant.example.restaurant.domain.Category;
import restaurant.example.restaurant.repository.CategoryRepository;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category handleCreatedCategory(Category category) {
        return this.categoryRepository.save(category);
    }
}
