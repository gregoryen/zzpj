package com.example.zzpj.security.jwt;

import com.example.zzpj.users.UserTokenInformation;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil implements Serializable {
    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.RS512;
    private static final long JWT_TOKEN_VALIDITY = 30 * 24 * 60 * 60;
    private static final String DEVICE_KEY = "device";
    private static final String LOGIN_KEY = "login";
    private static final String STEAM_KEY = "steam";


    private static final String ENCRYPTION = "RSA";

    private PrivateKey privateKey;
    private PublicKey publicKey;

    public JwtUtil(@Value("${security.jwt.private}") String privateKeyString, @Value("${security.jwt.public}") String publicKeyString) {
        try {
            KeyFactory kf = KeyFactory.getInstance(ENCRYPTION);
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyString));
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyString));

            privateKey = kf.generatePrivate(privateKeySpec);
            publicKey = kf.generatePublic(publicKeySpec);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }


    public String getLoginFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
    Long getSteamIdFromToken(String token) {
        return Long.getLong(getStringClaimFromToken(token, STEAM_KEY));
    }

    public Date getIssuedAtFromToken(String token) {
        return getClaimFromToken(token, Claims::getIssuedAt);
    }

    Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    String getDeviceInformationFromToken(String token) {
        return getStringClaimFromToken(token, DEVICE_KEY);
    }


    boolean isTokenValid(String token, UserDetails userDetails) {
        boolean isTokenNotExpired = isTokenExpired(getExpirationDateFromToken(token));

        boolean doesEmailMatch = doesLoginMatch(getLoginFromToken(token), userDetails.getUsername());

        return isTokenNotExpired  && doesEmailMatch;
    }



    private boolean doesLoginMatch(String loginFromToken, String login) {
        return loginFromToken.equals(login);
    }

    private boolean doesDeviceInfoMatch(String infoFromToken, String infoFromHeader) {
        return infoFromToken.equals(infoFromHeader);
    }

    private boolean isTokenExpired(Date expiration) {
        return expiration.after(new Date());
    }

    public String generateToken(UserTokenInformation userTokenInformation, String deviceInformation) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(DEVICE_KEY, deviceInformation);
        claims.put(LOGIN_KEY, userTokenInformation.getLogin());
        claims.put(STEAM_KEY,userTokenInformation.getSteamId());

        Date now = new Date();

        return Jwts.builder().setClaims(claims)
                .setSubject(userTokenInformation.getLogin())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(privateKey, SIGNATURE_ALGORITHM).compact();
    }


    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token).getBody();
    }

    private String getStringClaimFromToken(String token, String key) {
        return getCustomClaimFromToken(token, key, String.class);
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> getter) {
        final Claims claims = getAllClaimsFromToken(token);
        return getter.apply(claims);
    }

    private <T> T getCustomClaimFromToken(String token, String key, Class<T> returnClass) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get(key, returnClass);
    }
}
