package com.wanda.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanda.dto.CustomUserDetails;
import com.wanda.service.CustomUserDetailsService;
import com.wanda.service.JWTService;
import com.wanda.utils.exception.CustomException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Component
public class JWTFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JWTFilter.class);

    private final JWTService jwtService;
    private final CustomUserDetailsService userDetailsService;

    private final ObjectMapper objectMapper = new ObjectMapper(); // For JSON serialization

    // Configurable list of paths to skip filtering
    private final List<String> publicPaths = List.of("/login", "/register");

    public JWTFilter(JWTService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Skip filtering for public paths
        if (publicPaths.contains(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader("Authorization");




        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                logger.warn("Missing or invalid Authorization header");
                System.out.println("Missing or invalid Authorization header");
                throw new CustomException("Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
            }

            String email = jwtService.extractEmail(authorizationHeader);

            System.out.println(email);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                CustomUserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (!email.equals(userDetails.getEmail())) {
                    throw new CustomException("Invalid username or password", HttpStatus.UNAUTHORIZED);
                }

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

            filterChain.doFilter(request, response);

        } catch (CustomException e) {
            logger.error("Authentication failed: {}", e.getMessage());
            System.out.println("Authentication failed: {}"+ e.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            logger.error("Error processing JWT: {}", e.getMessage(), e);
            System.out.println("Error processing JWT: {}"+ e.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "An error occurred while processing the JWT");
        }
    }


    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");

        Map<String, Object> errorResponse = new LinkedHashMap<>();
        errorResponse.put("status", false);
        errorResponse.put("message", "Authentication Error");
        errorResponse.put("error", message);
        errorResponse.put("data", new ArrayList<>());

        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
    }
}
