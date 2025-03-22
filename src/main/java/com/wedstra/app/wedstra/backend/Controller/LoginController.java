package com.wedstra.app.wedstra.backend.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @GetMapping("/custom-login")
    public String showLoginPage() {
        return "LoginForm.html"; // This matches JSP or Thymeleaf template name
    }

}
