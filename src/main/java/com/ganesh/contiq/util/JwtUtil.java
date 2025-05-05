package com.ganesh.contiq.util;

import org.springframework.security.oauth2.jwt.Jwt;

public class JwtUtil {
    public static String getUserId(Jwt jwt){
        return jwt.getSubject();
    }
}
