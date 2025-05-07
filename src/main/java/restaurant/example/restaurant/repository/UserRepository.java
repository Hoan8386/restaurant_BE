package restaurant.example.restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import restaurant.example.restaurant.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public User findByEmail(String email);
}
