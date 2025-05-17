package restaurant.example.restaurant.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RestController;

import restaurant.example.restaurant.domain.User;
import restaurant.example.restaurant.domain.DTO.LoginDTO;
import restaurant.example.restaurant.domain.DTO.ResLoginDTO;
import restaurant.example.restaurant.service.UserService;
import restaurant.example.restaurant.util.SecurityUtil;
import restaurant.example.restaurant.util.anotation.ApiMessage;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;

    @Value("${restaurant.jwt.refresh-token-validity-in-seconds}")
    private long refreshJwtExpiration;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder,
            SecurityUtil securityUtil, UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping("auth/login")
    public ResponseEntity<ResLoginDTO> login(@RequestBody LoginDTO loginDTO) {
        // Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());

        // xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // nạp thông tin (nếu xử lý thành công) vào SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // create token

        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO res = new ResLoginDTO();
        User currentUserBD = this.userService.handelGetUserByUsername(loginDTO.getUsername());

        if (currentUserBD != null) {
            ResLoginDTO.UserLogin user = res.new UserLogin();
            user.setEmail(currentUserBD.getEmail());
            user.setId(currentUserBD.getId());
            user.setName(currentUserBD.getUsername());
            res.setUser(user);
        }

        String access_token = this.securityUtil.createAccessToken(authentication, res.getUser());
        res.setAccessToken(access_token);

        String refresh_token = this.securityUtil.createRefreshToken(loginDTO.getUsername(), res);

        // update user
        this.userService.updateUserToken(refresh_token, loginDTO.getUsername());

        // set cookies
        ResponseCookie responseCookies = ResponseCookie.from("refresh_token", refresh_token).httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshJwtExpiration)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookies.toString())
                .body(res);
    }

    @GetMapping("auth/account")
    @ApiMessage("fetch account")
    public ResponseEntity<ResLoginDTO.UserLogin> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        User currentUserBD = this.userService.handelGetUserByUsername(email);

        ResLoginDTO.UserLogin user = new ResLoginDTO().new UserLogin();
        if (currentUserBD != null) {
            user.setEmail(currentUserBD.getEmail());
            user.setId(currentUserBD.getId());
            user.setName(currentUserBD.getUsername());
        }

        return ResponseEntity.ok().body(user);
    }

    @GetMapping("/auth/refresh")
    @ApiMessage("Get user by refresh token")
    public ResponseEntity<String> getRefreshToken(@CookieValue(name = "refresh_token") String refresh_token) {
        // check valid
        Jwt decodeToken = this.securityUtil.checkValidRefreshToken(refresh_token);
        String email = decodeToken.getSubject();
        return ResponseEntity.ok().body(email);
    }

}
