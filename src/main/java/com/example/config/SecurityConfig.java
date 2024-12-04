package com.example.tasko.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/", "/home", "/register", "/css/**", "/js/**", "/images/**", "/h2-console/**").permitAll()
                .antMatchers("/dashboard").authenticated()
                .antMatchers("/tasks/my-tasks/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/tasks/**").hasRole("ADMIN")
                .antMatchers("/attendance/log/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/attendance/calendar/**").hasRole("ADMIN")
                .antMatchers("/payroll/my-payroll/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/payroll/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
                .and()
            .logout()
                .logoutSuccessUrl("/")
                .permitAll()
                .and()
            .csrf()
                .ignoringAntMatchers("/h2-console/**")
                .and()
            .headers()
                .frameOptions()
                .sameOrigin();
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}