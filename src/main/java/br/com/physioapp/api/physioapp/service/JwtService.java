package br.com.physioapp.api.physioapp.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.physioapp.api.physioapp.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

  private final byte[] secretBytes;
  private final long expirationMs;

  public JwtService(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration-ms:3600000}") long expirationMs) {
    this.secretBytes = secret.getBytes(StandardCharsets.UTF_8);
    this.expirationMs = expirationMs;
  }

  public String generateToken(User user) {
    Date now = new Date();
    Date exp = new Date(now.getTime() + expirationMs);

    return Jwts.builder()
        .setSubject(user.getEmail())
        .claim("id", user.getId() != null ? user.getId().toString() : null)
        .claim("fullname", user.getFullname())
        .claim("type", user.getType() != null ? user.getType().name() : null)
        .setIssuedAt(now)
        .setExpiration(exp)
        .signWith(Keys.hmacShaKeyFor(secretBytes), SignatureAlgorithm.HS256)
        .compact();
  }

}
