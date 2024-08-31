package com.example.demo.config;

import com.example.demo.constants.KeyConstants;
import com.example.demo.model.CustomUserDetails;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class CustomBasicAuthenticationFilter extends BasicAuthenticationFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private static final Logger log = LogManager.getLogger(CustomBasicAuthenticationFilter.class);

    public CustomBasicAuthenticationFilter(
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            UserRepository userRepository
    ) {
        super(authenticationManager);
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String bearerToken = request.getHeader(KeyConstants.AUTHORIZATION);

        if (bearerToken == null || !bearerToken.startsWith(KeyConstants.TOKEN_PREFIX)) {
            if (!KeyConstants.URL_CREATE_USER.equals(request.getRequestURL().toString())) {
                log.error("Access Denied. {}", 403);
            }
            chain.doFilter(request, response);
            return;
        }

        try {
            Authentication authentication = getAuthentication(bearerToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } catch (AuthenticationException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private Authentication getAuthentication(String bearerToken) {

        String jwt = getJwtFromBearerToken(bearerToken);
        jwtTokenProvider.validateToken(jwt);

        Long userId = jwtTokenProvider.getUserIdFromJWT(jwt);
        Optional<User> user = userRepository.findById(userId);

        if (!user.isPresent()) return null;

        CustomUserDetails userDetails = new CustomUserDetails(user.get());
        return new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
    }

    private String getJwtFromBearerToken(String bearerToken) {
       return bearerToken.substring(7);
    }
}

