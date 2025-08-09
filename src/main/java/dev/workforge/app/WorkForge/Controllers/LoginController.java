package dev.workforge.app.WorkForge.Controllers;

import dev.workforge.app.WorkForge.DTO.UserDTO;
import dev.workforge.app.WorkForge.Service.ServiceImpl.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
public class LoginController {

    private final AuthenticationService authenticationService;

    public LoginController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody UserDTO userDTO, HttpServletRequest request) {
        authenticationService.login(userDTO, request);
        request.getSession().setAttribute("user", userDTO.username());
        ResponseCookie cookie = ResponseCookie.from("SESSIONID", request.getSession().getId())
                .httpOnly(true)
                .secure(false)
                .sameSite("None")
                .path("/")
                .maxAge(Duration.ofHours(1))
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @GetMapping("/checkSession")
    public ResponseEntity<Boolean> isSessionValid(HttpSession session) {
        Object user = session.getAttribute("user");
        return ResponseEntity.ok(user != null);
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request) {
        authenticationService.logout(request);
    }
}
