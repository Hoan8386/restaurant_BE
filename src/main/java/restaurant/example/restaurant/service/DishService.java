package restaurant.example.restaurant.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import restaurant.example.restaurant.domain.Category;
import restaurant.example.restaurant.domain.Dish;
import restaurant.example.restaurant.domain.User;
import restaurant.example.restaurant.domain.response.ResultPaginationDataDTO;
import restaurant.example.restaurant.repository.CategoryRepository;
import restaurant.example.restaurant.repository.DishRepository;

@Service
public class DishService {
    private final DishRepository dishRepository;
    private final CategoryRepository categoryRepository;

    public DishService(DishRepository dishRepository, CategoryRepository categoryRepository) {
        this.dishRepository = dishRepository;
        this.categoryRepository = categoryRepository;
    }

    public Dish handleCreatedDish(Dish dish) {
        if (dish.getCategory() != null && dish.getCategory().getId() != null) {
            // Lấy Category từ DB theo ID
            Category category = categoryRepository.findById(dish.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Không tìm thấy category với id = " + dish.getCategory().getId()));

            // Gán lại Category đã load đầy đủ vào dish
            dish.setCategory(category);
        } else {
            throw new RuntimeException("Category không hợp lệ");
        }

        return dishRepository.save(dish);
    }

    public Optional<Dish> handleGetDishById(Long id) {
        return this.dishRepository.findById(id);
    }

    public ResultPaginationDataDTO handleGetAllDish(Specification<Dish> spec, Pageable pageable) {
        Page<Dish> pageUser = this.dishRepository.findAll(spec, pageable);
        ResultPaginationDataDTO rs = new ResultPaginationDataDTO();
        ResultPaginationDataDTO.Meta meta = new ResultPaginationDataDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageUser.getTotalPages());
        meta.setTotal(pageUser.getTotalElements());
        rs.setMeta(meta);
        rs.setResult(pageUser.getContent());
        return rs;
    }

    public Dish handleUpdateDish(Dish dish) {
        return this.dishRepository.save(dish);
    }

    public void handleDeleteDishById(Long id) {
        this.dishRepository.deleteById(id);
    }
}
