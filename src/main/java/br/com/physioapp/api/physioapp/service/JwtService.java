package br.com.physioapp.api.physioapp.service;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.physioapp.api.physioapp.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;

@Service
public class JwtService {

  private final SecretKey secretKey;
  private final long expirationMs;
  private final Clock clock;

  public static final String CLAIM_ID = "id";
  public static final String CLAIM_FULLNAME = "fullname";
  public static final String CLAIM_TYPE = "type";

  private static final int MIN_SECRET_BYTES = 32; // 256 bits (HS256)

  public JwtService(@Value("${jwt.secret}") String secret,
      @Value("${jwt.expiration-ms:3600000}") long expirationMs,
      Clock clock) {
    if (secret == null || secret.isBlank()) {
      throw new IllegalArgumentException("JWT secret must be provided");
    }

    byte[] keyBytes = decodeSecret(secret);
    if (keyBytes.length < MIN_SECRET_BYTES) {
      throw new IllegalArgumentException(
          "JWT secret is too short. Must be at least " + MIN_SECRET_BYTES + " bytes for HS256");
    }

    this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    this.expirationMs = expirationMs;
    this.clock = clock != null ? clock : Clock.systemUTC();
  }

  private static byte[] decodeSecret(String secret) {
    if (isBase64(secret)) {
      return Base64.getDecoder().decode(secret);
    }
    return secret.getBytes(StandardCharsets.UTF_8);
  }

  private static boolean isBase64(String s) {
    try {
      Base64.getDecoder().decode(s);
      return true;
    } catch (IllegalArgumentException ex) {
      return false;
    }
  }

  public String generateToken(User user) {
    Instant now = Instant.now(clock);
    Instant exp = now.plusMillis(expirationMs);

    JwtBuilder b = Jwts.builder()
        .setSubject(user.getEmail())
        .claim(CLAIM_ID, user.getId() != null ? user.getId().toString() : null)
        .claim(CLAIM_FULLNAME, user.getFullname())
        .claim(CLAIM_TYPE, user.getType() != null ? user.getType().name() : null)
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(exp))
        .signWith(secretKey, SignatureAlgorithm.HS256);

    return b.compact();
  }

  public Claims parseClaimsOrThrow(String token) throws JwtException {
    return Jwts.parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  public Claims parseClaimsSafe(String token) {
    try {
      return parseClaimsOrThrow(token);
    } catch (JwtException ex) {
      return null;
    }
  }

  public boolean validateToken(String token) {
    return parseClaimsSafe(token) != null;
  }

  public String extractSubject(String token) {
    Claims c = parseClaimsSafe(token);
    return c == null ? null : c.getSubject();
  }

  public UUID extractId(String token) {
    Claims c = parseClaimsSafe(token);
    if (c == null)
      return null;
    Object raw = c.get(CLAIM_ID);
    if (raw == null)
      return null;
    try {
      return UUID.fromString(raw.toString());
    } catch (IllegalArgumentException ex) {
      return null;
    }
  }

  public String extractFullname(String token) {
    Claims c = parseClaimsSafe(token);
    return c == null ? null : asString(c.get(CLAIM_FULLNAME));
  }

  public String extractType(String token) {
    Claims c = parseClaimsSafe(token);
    return c == null ? null : asString(c.get(CLAIM_TYPE));
  }

  private static String asString(Object o) {
    return o == null ? null : o.toString();
  }
}