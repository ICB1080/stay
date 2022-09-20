package com.icebear.stay.filter;

import com.icebear.stay.model.Authority;
import com.icebear.stay.repository.AuthorityRepository;
import com.icebear.stay.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

// validate token,
// find username in token
// and find corresponding authority of this user in database
@Component
public class JwtFilter extends OncePerRequestFilter {
    // OncePerRequestFilter only perform once
    private final String HEADER = "Authorization";
    private final String PREFIX = "Bearer ";
    private AuthorityRepository authorityRepository;
    private JwtUtil jwtUtil;

    @Autowired
    public JwtFilter(AuthorityRepository authorityRepository, JwtUtil jwtUtil) {
        this.authorityRepository = authorityRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {


        final String authorizationHeader = httpServletRequest.getHeader(HEADER);
        String jwt = null;
        if (authorizationHeader != null && authorizationHeader.startsWith(PREFIX)) {
            //  if request's header contains a token, get its value
            jwt = authorizationHeader.substring(PREFIX.length());
        }

        if (jwt != null && jwtUtil.validateToken(jwt)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            // SecurityContextHolder is null if it is first-time validation
            String username = jwtUtil.extractUsername(jwt);
            Authority authority = authorityRepository.findById(username).orElse(null);
            if (authority != null) {
                List<GrantedAuthority> grantedAuthorities = Arrays.asList(new GrantedAuthority[]{new SimpleGrantedAuthority(authority.getAuthority())});
                // this token used for storing user's info (username, authority)
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        username, null, grantedAuthorities);

                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
