package restaurant.example.restaurant.service;

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
}
