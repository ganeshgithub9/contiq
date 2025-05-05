package com.ganesh.contiq.util;

import java.time.Instant;
import java.util.UUID;

public class ErrorResponseUtil {
    public static String getRandomId(){
        return UUID.randomUUID().toString();
    }

    public static String getTimeStamp(){
        return Instant.now().toString();
    }
}

