package com.wanda.service;

import com.wanda.entity.Users;
import com.wanda.utils.exception.CustomException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JWTService {

    private SecretKey secretKey;

    public String mainToken;


    public JWTService() {

        try {

            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
            keyGenerator.init(256);

            secretKey = keyGenerator.generateKey();

            String base64EncodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
            System.out.println("Base64 Encoded Secret Key: " + base64EncodedKey);


        }catch (Exception e) {
            e.printStackTrace();

            throw new CustomException("no such alorithm present");
        }
    }

    public String generateToken(Users user){
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmail());

        return Jwts
                .builder()
                .claims(claims)
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                .signWith(getKey(), Jwts.SIG.HS256)
                .compact();
    }

    public SecretKey getKey(){
        return this.secretKey;
    }

    public String extractEmail(String bearerToken) {

        if(bearerToken.isEmpty()){
            throw new CustomException("token not found");
        }

        if(!bearerToken.startsWith("Bearer")) {
            System.out.println(bearerToken);
            throw new CustomException("invalid Bearer token");
        }

        String token = bearerToken.substring(7);

        var email = this.getClaims(token).getSubject();

        System.out.println("email: " + email);

        if(!this.validateToken(token)){
            throw new CustomException("Token was expired");
        }

        return email;
    }

    public Boolean validateToken(String token) {
        return this.getClaims(token).getExpiration().after(new Date());
    }


    public Claims getClaims(String token) {
        try {

            return Jwts
                .parser()
                .verifyWith(this.secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        }catch (SignatureException e){
            throw new CustomException("token secrete was changed, can not parse token");
        }catch (Exception e) {
            if(e.getMessage().isEmpty()){
                throw new CustomException("invalid token");
            }
            throw new CustomException(e.getMessage());
        }
    }
}
