package com.foo.micro.jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider {
   @Value("${jwt.secret}")
   private String jwtSecret;

   @Value("${jwt.expirationInMs}")
   private int jwtExpirationInMs;

   private static String USER_ROLES = "USER_ROLES";
   private static String BEARER_PREFIX = "Bearer ";

   public String generateToken(Authentication authentication) {
      LdapUserDetailsImpl userPrincipal = (LdapUserDetailsImpl) authentication.getPrincipal();

      String roles = userPrincipal.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

      Date now = new Date();
      Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

      return Jwts.builder()
            .setSubject(userPrincipal.getUsername())
            .claim(USER_ROLES, roles)
            .setIssuedAt(new Date())
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
   }

   private Claims getJwtClaims(String token) {
      return Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .getBody();
   }

   private UserDetails getUserDetails(String token) {
      Claims claims = getJwtClaims(token);

      String allAuthorities = claims.get(USER_ROLES, String.class);

      List<SimpleGrantedAuthority> authorities =
            Arrays.stream(allAuthorities.split("\\s*,\\s*"))
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

      return new UserDetails() {
         @Override
         public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities;
         }

         @Override
         public String getPassword() {
            return null;
         }

         @Override
         public String getUsername() {
            return claims.getSubject();
         }

         @Override
         public boolean isAccountNonExpired() {
            return false;
         }

         @Override
         public boolean isAccountNonLocked() {
            return false;
         }

         @Override
         public boolean isCredentialsNonExpired() {
            return false;
         }

         @Override
         public boolean isEnabled() {
            return false;
         }
      };
   }

   UsernamePasswordAuthenticationToken getAuthenticationToken(String token) {
      UserDetails userDetails = getUserDetails(token);
      return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
   }
   
   String getTokenFromRequest(HttpServletRequest request) {
      String jwt = request.getHeader("Authorization");
      return StringUtils.hasText(jwt) && jwt.startsWith(BEARER_PREFIX) ? jwt.substring(BEARER_PREFIX.length()) : jwt;
   }
   
   boolean validateToken(String authToken) {
      try {
         Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
         return true;
      } catch (SignatureException ex) {
         log.error("Invalid JWT signature");
      } catch (MalformedJwtException ex) {
         log.error("Invalid JWT token");
      } catch (ExpiredJwtException ex) {
         log.error("Expired JWT token");
      } catch (UnsupportedJwtException ex) {
         log.error("Unsupported JWT token");
      } catch (IllegalArgumentException ex) {
         log.error("JWT claims string is empty.");
      }

      return false;
   }
}