package br.com.physioapp.api.physioapp.service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.physioapp.api.physioapp.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

@Service
public class JwtService {

  private final SecretKey secretKey;
  private final long expirationMs;

  private static final String CLAIM_ID = "id";
  private static final String CLAIM_FULLNAME = "fullname";
  private static final String CLAIM_TYPE = "type";

  private static final int MIN_SECRET_LENGTH = 32; // 256 bits (hS256)

  public JwtService(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration-ms:3600000}") long expirationMs) {
    byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);

    if (secretBytes.length < MIN_SECRET_LENGTH) {
      throw new IllegalArgumentException(
        "JWT secret is too short. It must be at least "
        + MIN_SECRET_LENGTH + " bytes long for HS256.");
    }
    
    this.secretKey = Keys.hmacShaKeyFor(secretBytes);
    this.expirationMs = expirationMs;
  }

  public String generateToken(User user) {
    Instant now = Instant.now();
    Instant exp = now.plusMillis(expirationMs);

    return Jwts.builder()
        .setSubject(user.getEmail())
        .claim(CLAIM_ID, user.getId() != null ? user.getId().toString() : null)
        .claim(CLAIM_FULLNAME, user.getFullname())
        .claim(CLAIM_TYPE, user.getType() != null ? user.getType().name() : null)
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(exp))
        .signWith(secretKey, SignatureAlgorithm.HS256)
        .compact();
  }

  public Claims parseClaims(String token) throws JwtException{
    return Jwts.parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  public boolean validateToken(String token) {
    return parseClaims(token) != null;
  }

  public String extractFullname(String token) {
    Claims claims = parseClaims(token);
    if (claims == null)
      return null;
    Object fullname = claims.get("fullname");
    return fullname != null ? fullname.toString() : null;
  }

}
