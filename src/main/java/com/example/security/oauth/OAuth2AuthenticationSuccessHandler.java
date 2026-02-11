package com.example.security.oauth;

import com.example.security.jwt.JwtService;
import com.example.security.model.User;
import com.example.security.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler
        implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
//        assert oauthUser != null;
        String email = oauthUser.getAttribute("email");

        User user = userRepository.findByUsername(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user);

        // Store token somewhere frontend can access (cookie or header-based redirect)
        // If you already do this for username/password, reuse SAME mechanism

        String redirectUrl;

        if (user.hasRole("ROLE_ADMIN")) {
            redirectUrl = "/admin/dashboard";
        } else if (user.hasRole("ROLE_HR")) {
            redirectUrl = "/hr/dashboard";
        } else {
            redirectUrl = "/employee/dashboard";
        }


        response.sendRedirect(redirectUrl);
    }

}
