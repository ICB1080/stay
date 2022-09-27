package com.icebear.stay.filter;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        // request can from any website address
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
        // request's method can be POST, GET, OPTIONS AND DELETE
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        // request can contain headers including: Authorization and Content-Type.
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");


        if ("OPTIONS".equalsIgnoreCase(httpServletRequest.getMethod())) {
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        } else {
            // CORS check has finished
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }
}

