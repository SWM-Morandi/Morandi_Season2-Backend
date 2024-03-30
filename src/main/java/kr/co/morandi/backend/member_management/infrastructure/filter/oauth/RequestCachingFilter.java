package kr.co.morandi.backend.member_management.infrastructure.filter.oauth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RequestCachingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        CachedBodyHttpServletWrapper cachedBodyHttpServletWrapper = new CachedBodyHttpServletWrapper(request);
        filterChain.doFilter(cachedBodyHttpServletWrapper, response);
    }
}
