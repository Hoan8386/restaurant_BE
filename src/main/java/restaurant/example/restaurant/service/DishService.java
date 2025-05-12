package restaurant.example.restaurant.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import restaurant.example.restaurant.domain.Dish;
import restaurant.example.restaurant.repository.DishRepository;

@Service
public class DishService {
    private final DishRepository dishRepository;

    public DishService(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    public Dish handleCreatedDish(Dish dish) {
        return this.dishRepository.save(dish);
    }

    public Optional<Dish> handleGetDishById(Long id) {
        return this.dishRepository.findById(id);
    }

    public List<Dish> handleGetAllDish(Pageable pageable) {
        return this.dishRepository.findAll(pageable).getContent();
    }

    public Dish handleUpdateDish(Dish dish) {
        return this.dishRepository.save(dish);
    }

    public void handleDeleteDishById(Long id) {
        this.dishRepository.deleteById(id);
    }
}
