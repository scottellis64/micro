package com.foo.micro.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * JWT Security configuration for any rest service that has security annotations for individual api endpoints
 * that are secured by the JWT token.  The token must be on all requests that are protected by security configuration,
 * and if the annotated endpoint specifies security roles, they must be part of the JWT token.
 */

@Configuration
@Import(JwtConfiguration.class)
@EnableWebSecurity
@EnableGlobalMethodSecurity(
      securedEnabled = true,
      jsr250Enabled = true,
      prePostEnabled = true
)
public class JwtWebSecurityConfig extends WebSecurityConfigurerAdapter {
   private JwtFilter jwtFilter;
   private RequestMatcher securityExclusions;

   @Autowired
   public JwtWebSecurityConfig(
         JwtFilter jwtFilter,
         RequestMatcher securityExclusions) {
      this.jwtFilter = jwtFilter;
      this.securityExclusions = securityExclusions;
   }

   @Override
   public void configure(WebSecurity web) {
      web.ignoring().requestMatchers(securityExclusions);
   }

   @Override
   protected void configure(HttpSecurity http) throws Exception {
      http.authorizeRequests()
            .requestMatchers().permitAll()

            // All other requests are authenticated
            .anyRequest().authenticated()

            .and()

            .addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class)

            .formLogin()
            .loginPage("/web/login");
   }
}