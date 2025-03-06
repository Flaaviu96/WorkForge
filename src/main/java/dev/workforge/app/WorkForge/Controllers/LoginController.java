package dev.workforge.app.WorkForge.Controllers;

import dev.workforge.app.WorkForge.DTO.UserDTO;
import dev.workforge.app.WorkForge.Service.ServiceImpl.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    private final AuthenticationService authenticationService;

    public LoginController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public void login(@RequestBody UserDTO userDTO, HttpServletRequest request) {
        authenticationService.login(userDTO, request.getSession().getId());
    }
}
