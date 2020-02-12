package com.foo.micro.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.LinkedList;

@Configuration
@Import({
      JwtTokenProvider.class
})
public class JwtConfiguration {
   private JwtTokenProvider jwtTokenProvider;

   @Autowired
   public JwtConfiguration(JwtTokenProvider jwtTokenProvider) {
      this.jwtTokenProvider = jwtTokenProvider;
   }

   @Bean
   public JwtFilter jwtFilter(RequestMatcher securityExclusions) {
      return new JwtFilter(jwtTokenProvider, securityExclusions);
   }

   @Bean
   public RequestMatcher securityExclusions() {
      final String[] urls = new String[] {
            "/**/login",
            "/**/*.css",
            "/**/*.png",
            "/**/*.gif",
            "/**/*.ico",
            "/**/*.jpg",
            "/**/*.js",
            "/**/actuator/**"
      };

      //Build Matcher List
      LinkedList<RequestMatcher> matcherList = new LinkedList<>();
      for (String url : urls) {
         matcherList.add(new AntPathRequestMatcher(url));
      }

      return new OrRequestMatcher(matcherList);
   }
}
