package restaurant.example.restaurant.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import restaurant.example.restaurant.domain.User;
import restaurant.example.restaurant.repository.UserRepository;

@Service
public class UserService {
    UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User CreateUser(User newUser) {
        return this.userRepository.save(newUser);
    }

    public Optional<User> handelGetUser(Long id) {
        return this.userRepository.findById(id);
    }

    public void handelDeleteUser(Long id) {
        this.userRepository.deleteById(id);
    }

    public User handelUpdateUser(User updateUser) {
        return this.userRepository.save(updateUser);
    }
}
