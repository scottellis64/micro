package com.foo.micro.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {
   private JwtTokenProvider tokenProvider;
   private RequestMatcher securityExclusions;

   @Autowired
   public JwtFilter(
         JwtTokenProvider tokenProvider,
         RequestMatcher securityExclusions) {
      super();

      this.tokenProvider = tokenProvider;
      this.securityExclusions = securityExclusions;
   }

   @Override
   protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
      return securityExclusions.matches(request) || super.shouldNotFilter(request);
   }

   @Override
   protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
      try {
         String jwt = tokenProvider.getTokenFromRequest(request);
         if (! StringUtils.hasText(jwt)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing JWT bearer token");
            return;
         }

         if (tokenProvider.validateToken(jwt)) {
            UsernamePasswordAuthenticationToken authentication = tokenProvider.getAuthenticationToken(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
         } else {
            log.error("Invalid JWT token has been received");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token is invalid.");
            return;
         }
      } catch (Exception ex) {
         log.error("Could not set user authentication in security context", ex);
         response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error processing JWT token");
         return;
      }

      filterChain.doFilter(request, response);
   }
}
