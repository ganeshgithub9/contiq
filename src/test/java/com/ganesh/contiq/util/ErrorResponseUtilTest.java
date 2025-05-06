package com.ganesh.contiq.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ErrorResponseUtilTest {


    @Test
    public void shouldReturnUUIDString_whenGetRandomId(){
        String UUID=ErrorResponseUtil.getRandomId();
        assertNotNull(UUID);
    }

    @Test
    public void shouldReturnTime_whenGetTimestamp(){
        String time=ErrorResponseUtil.getTimeStamp();
        assertNotNull(time);
    }
}
