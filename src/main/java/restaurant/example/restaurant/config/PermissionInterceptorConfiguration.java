package restaurant.example.restaurant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PermissionInterceptorConfiguration implements WebMvcConfigurer {
    @Bean
    PermissionInterceptor getPermissionInterceptor() {
        return new PermissionInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] whiteList = {
                "/", "/api/v1/auth/**", "/storage/**",
                "/api/v1/companies/**", "/api/v1/jobs/**", "/api/v1/skills/**", "/api/v1/files",
                "/api/v1/resumes/**",
                "/api/v1/subscribers/**",
                "/api/v1/auth/account",
                "/category",
                "/dish",
                "/flies",
                "/flies/**",
                "/pre-signed-url/**",
                "/error"
        };
        registry.addInterceptor(getPermissionInterceptor())
                .excludePathPatterns(whiteList);
    }
}

// Client → DispatcherServlet → 🔐 Interceptor → 🚀 Controller → 🧼 Interceptor
// → Response