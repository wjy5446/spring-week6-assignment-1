package com.codesoom.assignment.utils;

import com.codesoom.assignment.errors.InvalidAccessTokenException;
import com.codesoom.assignment.errors.NotSupportedIdException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtil {
    private final Key key;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * userId을 jwt로 인코딩된 token로 변환합니다.
     *
     * @param userId 인코딩할 유저 Id
     * @return jwt로 인코딩된 token
     * @throws NotSupportedIdException 유효하지 않은 userId이 전달되는 경우
     */
    public String encode(Long userId) {
        if(!checkValidUserId(userId)) {
            throw new NotSupportedIdException(userId);
        }

        return Jwts.builder()
                .claim("userId", userId)
                .signWith(key).compact();
    }

    /**
     * token을 파싱해 claim을 반환합니다.
     *
     * @param token 암호화된 token
     * @return claim
     * @throws InvalidAccessTokenException token이 유효하지 않은 값인 경우
     */
    public Claims decode(String token) {
        if(!checkValidToken(token)) {
            throw new InvalidAccessTokenException(token);
        }

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SignatureException e) {
            throw new InvalidAccessTokenException(token);
        }
    }

    /**
     * userId 값이 인코딩이 가능하다면 true, 그렇지 않다면 false를 리턴합니다.
     *
     * @param userId 유저의 id
     * @return 유저 id가 유효한 경우 true, 아닌 경우 false를 리턴합니다.
     */
    public boolean checkValidUserId(Long userId) {
        return userId != null;
    }

    /**
     * toekn이 파싱 가능한 값이라면 true, 아니면 false를 리턴합니다.
     *
     * @param token 검사할 token
     * @return 파싱 가능한 token일 경우 true, 아닌 경우 false
     */
    public boolean checkValidToken(String token) {
        if(token == null) {
            return false;
        }

        return !token.isBlank();
    }
}
