package com.example.Project_Online_market.SecurityConfig;

import java.io.IOException;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;



public class JwtTokenValidator extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwt = request.getHeader(JwtConstant.JWT_HEADER);
        System.out.println("JWT Token in JwtTokenValidator: " + jwt);

        if (jwt != null && jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7);
            System.out.println("JWT Token after removing 'Bearer': " + jwt);

            try {
                SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());
                Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(jwt).getBody();
                System.out.println("Claims in JWT: " + claims);

                String email = String.valueOf(claims.get("email"));
                System.out.println("Email extracted from JWT: " + email);

                // Extract roles from JWT
                String roles = String.valueOf(claims.get("roles"));
                System.out.println("Roles extracted from JWT: " + roles);

                // Prefix roles with "ROLE_" for Spring Security compatibility
                if (roles != null && !roles.startsWith("ROLE_")) {
                    roles = "ROLE_" + roles;
                }
                System.out.println("Roles after adding 'ROLE_' prefix: " + roles);

                List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(roles);
                System.out.println("Authorities created from roles: " + authorities);

                // Create authentication object with roles
                Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("Authentication set in SecurityContext: " + authentication);

            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                System.err.println("Token has expired: " + e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has expired");
                return;
            } catch (io.jsonwebtoken.SignatureException e) {
                System.err.println("Invalid token signature: " + e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token signature");
                return;
            } catch (io.jsonwebtoken.MalformedJwtException e) {
                System.err.println("Malformed token: " + e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Malformed token");
                return;
            } catch (BadCredentialsException e) {
                System.err.println("Bad credentials: " + e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            } catch (Exception e) {
                System.err.println("Unexpected error: " + e.getMessage());
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected error during token validation");
                return;
            }
        } else {
            System.err.println("No JWT token provided in request header.");
        }

        filterChain.doFilter(request, response);
    }
}
