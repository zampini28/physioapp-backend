package br.com.physioapp.api.physioapp.security;

import br.com.physioapp.api.physioapp.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        try {
            String token = resolveToken(header);
            if (token != null) {
                Claims claims = jwtService.parseClaimsSafe(token);
                if (claims != null) {
                    String subject = claims.getSubject();
                    String idClaim = claims.get(JwtService.CLAIM_ID) != null
                            ? claims.get(JwtService.CLAIM_ID).toString()
                            : null;

                    UserDetails userDetails = loadUserDetails(subject, idClaim);
                    if (userDetails != null) {
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails,
                                null, userDetails.getAuthorities());
                        auth.setDetails(createWebAuthenticationDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        log.debug("Authenticated user {} for request {}", subject, request.getRequestURI());
                    }
                }
            }
        } catch (JwtException ex) {
            log.debug("JWT processing failed: {}", ex.getMessage());
            SecurityContextHolder.clearContext();
            throw new ServletException("Invalid or expired JWT token", ex);
        } catch (Exception ex) {
            log.error("Unexpected error in JWT filter", ex);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(String header) {
        if (!StringUtils.hasText(header))
            return null;
        if (!header.startsWith("Bearer "))
            return null;
        String token = header.substring(7).trim();
        return token.isEmpty() ? null : token;
    }

    private UserDetails loadUserDetails(String subject, String idClaim) {
        if (!StringUtils.hasText(subject))
            return null;
        try {
            return userDetailsService.loadUserByUsername(subject);
        } catch (Exception ex) {
            log.debug("UserDetailsService failed to load by username {}: {}", subject, ex.getMessage());
            return null;
        }
    }

    private Object createWebAuthenticationDetails(HttpServletRequest request) {
        try {
            Class<?> detailsClass = Class
                    .forName("org.springframework.security.web.authentication.WebAuthenticationDetails");
            return detailsClass.getConstructor(HttpServletRequest.class).newInstance(request);
        } catch (Exception ex) {
            return Collections.singletonMap("remoteAddress", request.getRemoteAddr());
        }
    }
}
