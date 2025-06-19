package restaurant.example.restaurant.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import restaurant.example.restaurant.domain.Permission;
import restaurant.example.restaurant.domain.Role;
import restaurant.example.restaurant.domain.User;
import restaurant.example.restaurant.repository.PermissionRepository;
import restaurant.example.restaurant.repository.RoleRepository;
import restaurant.example.restaurant.repository.UserRepository;
import restaurant.example.restaurant.util.constant.GenderEnum;

@Service
public class DatabaseInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(
            PermissionRepository permissionRepository,
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> START INIT DATABASE");

        long countPermissions = this.permissionRepository.count();
        long countRoles = this.roleRepository.count();
        long countUsers = this.userRepository.count();

        if (countPermissions == 0) {
            List<Permission> permissions = new ArrayList<>();

            // CATEGORY
            permissions.add(new Permission("Create a category", "/category", "POST", "CATEGORY"));
            permissions.add(new Permission("Update a category", "/category", "PUT", "CATEGORY"));
            permissions.add(new Permission("Delete a category", "/category/{id}", "DELETE", "CATEGORY"));
            permissions.add(new Permission("Get a category by id", "/category/{id}", "GET", "CATEGORY"));
            permissions.add(new Permission("Get category with pagination", "/category", "GET", "CATEGORY"));

            // DISH
            permissions.add(new Permission("Create a dish", "/dish", "POST", "DISH"));
            permissions.add(new Permission("Update a dish", "/dish", "PUT", "DISH"));
            permissions.add(new Permission("Delete a dish", "/dish/{id}", "DELETE", "DISH"));
            permissions.add(new Permission("Get a dish by id", "/dish/{id}", "GET", "DISH"));
            permissions.add(new Permission("Get dish with pagination", "/dish", "GET", "DISH"));

            // PERMISSIONS
            permissions.add(new Permission("Create a permission", "/permissions", "POST", "PERMISSIONS"));
            permissions.add(new Permission("Update a permission", "/permissions", "PUT", "PERMISSIONS"));
            permissions.add(new Permission("Delete a permission", "/permissions/{id}", "DELETE", "PERMISSIONS"));
            permissions.add(new Permission("Get a permission by id", "/permissions/{id}", "GET", "PERMISSIONS"));
            permissions.add(new Permission("Get permissions with pagination", "/permissions", "GET", "PERMISSIONS"));

            // ROLES
            permissions.add(new Permission("Create a role", "/roles", "POST", "ROLES"));
            permissions.add(new Permission("Update a role", "/roles", "PUT", "ROLES"));
            permissions.add(new Permission("Delete a role", "/roles/{id}", "DELETE", "ROLES"));
            permissions.add(new Permission("Get a role by id", "/roles/{id}", "GET", "ROLES"));
            permissions.add(new Permission("Get roles with pagination", "/roles", "GET", "ROLES"));

            // USERS
            permissions.add(new Permission("Create a user", "/users", "POST", "USERS"));
            permissions.add(new Permission("Update a user", "/users", "PUT", "USERS"));
            permissions.add(new Permission("Delete a user", "/users/{id}", "DELETE", "USERS"));
            permissions.add(new Permission("Get a user by id", "/users/{id}", "GET", "USERS"));
            permissions.add(new Permission("Get users with pagination", "/users", "GET", "USERS"));

            // CART - chú ý sửa đường dẫn theo base path /cart
            permissions.add(new Permission("Get cart user", "/cart", "GET", "CART"));
            permissions.add(new Permission("Delete cart user", "/cart", "DELETE", "CART"));
            permissions.add(new Permission("Add dish to cart", "/cart/add-dish", "POST", "CART_ITEM"));
            permissions.add(new Permission("Get all dishes in cart", "/cart/get-all-dish", "GET", "CART_ITEM"));
            permissions.add(new Permission("Update dish quantity in cart", "/cart/update-dish", "PUT", "CART_ITEM"));
            permissions.add(new Permission("Delete dish from cart", "/cart/delete-dish/{id}", "DELETE", "CART_ITEM"));

            // FILES
            permissions.add(new Permission("Download a file", "/files", "POST", "FILES"));
            permissions.add(new Permission("Upload a file", "/files", "GET", "FILES"));

            // ORDER - sửa đường dẫn cho phù hợp controller
            permissions.add(new Permission("Create order", "/order", "POST", "ORDER"));
            permissions.add(new Permission("Get orders of current user", "/list-order", "GET", "ORDER"));
            permissions.add(new Permission("Get all orders", "/get-all-order", "GET", "ORDER"));
            permissions.add(new Permission("Get order by id", "/order/{id}", "GET", "ORDER"));
            permissions.add(new Permission("Update order status", "/order/status/{id}", "PUT", "ORDER"));

            this.permissionRepository.saveAll(permissions);
        }

        if (countRoles == 0) {
            List<Permission> allPermissions = this.permissionRepository.findAll();

            // SUPER_ADMIN: full quyền
            Role adminRole = new Role();
            adminRole.setName("SUPER_ADMIN");
            adminRole.setDescription("Admin thì full permissions");
            adminRole.setPermissions(allPermissions);
            this.roleRepository.save(adminRole);

            // USER: chỉ mua hàng, xem đơn hàng của mình, thao tác giỏ hàng
            List<Permission> userPermissions = allPermissions.stream()
                    // sửa filter đường dẫn order và cart đúng với controller
                    .filter(p -> (p.getApiPath().equals("/order") && p.getMethod().equals("POST"))
                            || (p.getApiPath().equals("/list-order") && p.getMethod().equals("GET"))
                            || p.getApiPath().startsWith("/cart"))
                    .toList();

            Role userRole = new Role();
            userRole.setName("USER");
            userRole.setDescription("User bình thường - mua hàng và theo dõi đơn hàng của mình");
            userRole.setPermissions(userPermissions);
            this.roleRepository.save(userRole);

            // STAFF: xem tất cả đơn hàng, xem chi tiết, cập nhật trạng thái đơn hàng
            List<Permission> staffPermissions = allPermissions.stream()
                    .filter(p -> (p.getApiPath().equals("/get-all-order") && p.getMethod().equals("GET")) ||
                            (p.getApiPath().startsWith("/order/") && p.getMethod().equals("GET")) ||
                            (p.getApiPath().equals("/order/status/{id}") && p.getMethod().equals("PUT")))
                    .toList();

            Role staffRole = new Role();
            staffRole.setName("STAFF");
            staffRole.setDescription("Nhân viên quản lý đơn hàng");
            staffRole.setPermissions(staffPermissions);
            this.roleRepository.save(staffRole);
        }

        if (countUsers == 0) {
            Role adminRole = this.roleRepository.findByName("SUPER_ADMIN");
            Role userRole = this.roleRepository.findByName("USER");
            Role staffRole = this.roleRepository.findByName("STAFF");

            // SUPER ADMIN
            User adminUser = new User();
            adminUser.setEmail("admin@gmail.com");
            adminUser.setAddress("HCM");
            adminUser.setGender(GenderEnum.MALE);
            adminUser.setUsername("I'm super admin");
            adminUser.setPassword(this.passwordEncoder.encode("123456"));
            adminUser.setRole(adminRole);
            this.userRepository.save(adminUser);

            // NORMAL USER
            User normalUser = new User();
            normalUser.setEmail("user@gmail.com");
            normalUser.setAddress("Hanoi");
            normalUser.setGender(GenderEnum.FEMALE);
            normalUser.setUsername("Normal User");
            normalUser.setPassword(this.passwordEncoder.encode("123456"));
            normalUser.setRole(userRole);
            this.userRepository.save(normalUser);

            // STAFF
            User staffUser = new User();
            staffUser.setEmail("staff@gmail.com");
            staffUser.setAddress("Da Nang");
            staffUser.setGender(GenderEnum.MALE);
            staffUser.setUsername("Order Manager");
            staffUser.setPassword(this.passwordEncoder.encode("123456"));
            staffUser.setRole(staffRole);
            this.userRepository.save(staffUser);
        }

        if (countPermissions > 0 && countRoles > 0 && countUsers > 0) {
            System.out.println(">>> SKIP INIT DATABASE ~ ALREADY HAVE DATA...");
        } else {
            System.out.println(">>> END INIT DATABASE");
        }
    }
}
