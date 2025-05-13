package restaurant.example.restaurant.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import restaurant.example.restaurant.domain.Dish;
import restaurant.example.restaurant.domain.User;
import restaurant.example.restaurant.domain.DTO.Meta;
import restaurant.example.restaurant.domain.DTO.ResultPaginationDataDTO;
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

    public ResultPaginationDataDTO handleGetAllDish(Pageable pageable) {
        Page<Dish> pageUser = this.dishRepository.findAll(pageable);
        ResultPaginationDataDTO rs = new ResultPaginationDataDTO();
        Meta meta = new Meta();
        meta.setPage(pageUser.getNumber() + 1);
        meta.setPageSize(pageUser.getSize());

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
