package com.example.demo.config;

import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.persistence.service.UserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public WebSecurityConfig(
            UserDetailsService userDetailsService,
            JwtTokenProvider jwtTokenProvider,
            UserRepository userRepository
    ) throws Exception {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    };

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/public/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/user/create/**").permitAll()
                .anyRequest().authenticated();

        http.addFilter(new CustomUsernamePasswordAuthenticationFilter(
                authenticationManager(),
                jwtTokenProvider,
                userRepository
        ));

        http.addFilter(new CustomBasicAuthenticationFilter(
                authenticationManager(),
                jwtTokenProvider,
                userRepository
        ));
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.parentAuthenticationManager(authenticationManagerBean())
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }
}
