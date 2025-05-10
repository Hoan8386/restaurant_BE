package restaurant.example.restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import restaurant.example.restaurant.domain.Dish;
import restaurant.example.restaurant.domain.User;

public interface DishRepository extends JpaRepository<Dish, Long> {

}
