package com.zomato.menu_service.security;


import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class JWTAuthFilter extends OncePerRequestFilter {
    @Autowired
    private JWTAuthUtil authUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Incoming request: {}", request.getRequestURI());

        String requestHeader = request.getHeader("Authorization");
        if (requestHeader == null || !requestHeader.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = requestHeader.split(" ")[1];
        Claims claims = authUtil.getAllClaimsFromToken(token);

        //making a custom class so that we can set this in context and get value from context where needed
        String userId = claims.get("id", String.class);
        String username = claims.getSubject();
        String email = claims.get("email", String.class);
        String phoneNumber = claims.get("phoneNumber", String.class);
        String status = claims.get("status", String.class);
        CustomPrincipal principal=new CustomPrincipal(UUID.fromString(userId),username,phoneNumber,email,status);

        if (claims.getSubject() != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            String role = claims.get("role", String.class);

            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(principal, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(request, response);
        }
    }
}

