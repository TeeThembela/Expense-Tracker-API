package com.teetech.expensetrackerapi.security.filter;

import com.teetech.expensetrackerapi.entity.Authority;
import com.teetech.expensetrackerapi.entity.User;
import com.teetech.expensetrackerapi.security.model.UserPrincipal;
import com.teetech.expensetrackerapi.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import static com.teetech.expensetrackerapi.util.SecurityConstants.*;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        Optional<String> token = extractToken(request);

        if (token.isEmpty()){
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = token.get();
        String username;

        try{
            username = jwtUtil.extractUsername(jwt);
        } catch (JwtException | IllegalArgumentException e){
            filterChain.doFilter(request, response);
            return;
        }

        if (username == null || SecurityContextHolder.getContext().getAuthentication() == null){

           if (!jwtUtil.isAccessTokenValid(jwt)){
               filterChain.doFilter(request, response);
               return;
           }

            Set<Authority> authorities = jwtUtil.extractAuthorities(jwt).stream().
                                    map(a -> Authority.builder().name(a.getAuthority()).build())
                                    .collect(Collectors.toSet());

            User partialUser = User.builder()
                    .id(jwtUtil.extractUserId(jwt))
                    .email(username)
                    .authorities(authorities)
                    .build();

            UserPrincipal principal = new UserPrincipal(partialUser);

            String sessionId = jwtUtil.extractSessionId(jwt);
            principal.setSessionId(sessionId);

            var authToken = new UsernamePasswordAuthenticationToken(principal, null,
                    principal.getAuthorities());

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);

        }
        filterChain.doFilter(request, response);
    }

    private Optional<String> extractToken(HttpServletRequest request){
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)){
            return Optional.empty();
        }

        return Optional.of(authHeader.substring(BEARER_PREFIX.length()));
    }
}
