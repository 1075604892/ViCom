package com.vicom.backend.security;


import com.vicom.backend.entity.User;
import com.vicom.backend.repository.UserRepository;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

@Component
public class JwtTokenUtil implements Serializable {
    public JwtTokenUtil() {
    }

    @Autowired
    UserRepository userRepository;


    private int validity = 1800000000;

    private String secret = "Fudan2022";

    public String generateToken(User user) {
        JwtBuilder jwtBuilder = Jwts.builder();
        String token = jwtBuilder
                .setHeaderParam("type", "JWT")
                .setHeaderParam("alg", "HS512")
                .claim("username", user.getUsername())
                .claim("id", user.getId())
                .setSubject("superAdmin")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + validity))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
        return token;
    }

    public User parse(String token) {
        JwtParser parser = Jwts.parser();
        Jws<Claims> claimsJws = parser.setSigningKey(secret).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();

        User user = userRepository.findByUsername((String) claims.get("username"));
        return user;
    }

    public boolean verify(String token) {
        try {
            JwtParser parser = Jwts.parser();
            parser.setSigningKey(secret);
            Jws<Claims> claimsJws = parser.parseClaimsJws(token);
            Claims body = claimsJws.getBody();
            return true;
        } catch (ExpiredJwtException | UnsupportedJwtException
                | MalformedJwtException | SignatureException
                | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return false;
    }


}
