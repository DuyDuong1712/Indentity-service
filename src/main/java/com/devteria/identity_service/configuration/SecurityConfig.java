package com.devteria.identity_service.configuration;

import com.devteria.identity_service.enums.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final String[] PUBLIC_ENDPOINTS = {"/users", "/auth/login", "/auth/introspect"};

    @Value("${jwt.signerKey}")
    private String signerKey;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable); //Tat CSRF. JWT đã cung cấp cơ chế xác thực riêng và không phụ thuộc vào session hoặc cookie (nơi CSRF thường được áp dụng).

        httpSecurity.authorizeHttpRequests(requests ->
                requests.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll()
//                        .requestMatchers(HttpMethod.GET, "/users").hasAuthority("SCOPE_ADMIN")
//                        .requestMatchers(HttpMethod.GET, "/users").hasAuthority("ROLE_ADMIN")
//                        .requestMatchers(HttpMethod.GET, "/users").hasRole(Role.ADMIN.name())
                        .anyRequest().authenticated() // Tất cả request phải đc authenticated mới đc access
        );

        //xác thực các JWT được gửi trong request
        httpSecurity.oauth2ResourceServer(
                oauth2 -> oauth2.jwt(
                        jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder())
                                                                        .jwtAuthenticationConverter(jwtAuthenticationConverter())) //chuyển JWT thành đối tượng Authentication
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint()) //Khi có lỗi xác thực (ví dụ token sai, hết hạn), Spring sẽ gọi JwtAuthenticationEntryPoint để xử lý.
        );


        return httpSecurity.build();
    }


    @Bean
    public JwtDecoder jwtDecoder() {
        //kiểm tra chữ ký (signature) của token để đảm bảo token hợp lệ.

        SecretKeySpec secretKey = new SecretKeySpec(signerKey.getBytes(), "HS512");

        NimbusJwtDecoder nimbusJwtDecoder =  NimbusJwtDecoder
                                                    .withSecretKey(secretKey)
                                                    .macAlgorithm(MacAlgorithm.HS512)
                                                    .build();

        return nimbusJwtDecoder;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        // ánh xạ claim trong JWT thành quyền (authority).
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

}