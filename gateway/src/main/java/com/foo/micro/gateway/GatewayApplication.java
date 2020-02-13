package com.foo.micro.gateway;

import com.foo.micro.jwt.JwtWebSecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableZuulProxy
@Import(JwtWebSecurityConfig.class)
public class GatewayApplication {
   public static void main(String[] args) {
      SpringApplication.run(GatewayApplication.class, args);
   }
}
