package com.foo.micro.web;

import com.foo.micro.jwt.JwtWebSecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(JwtWebSecurityConfig.class)
public class WebApplication {
   public static void main(String[] args) {
      SpringApplication.run(WebApplication.class, args);
   }
}
