package com.ganesh.contiq.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class JwtUtilTest {

    @Test
    public void shouldReturnSubject_whenGetSubject(){
        Map<String,Object> headers=new HashMap<>(),claims=new HashMap<>();
        headers.put("alg","RS256");
        claims.put("sub","userId");

        Jwt jwt=new Jwt("token", Instant.now(),Instant.now().plusSeconds(900),headers,claims);
        String actualResult=JwtUtil.getUserId(jwt);
        assertEquals("userId",actualResult);
    }
}
