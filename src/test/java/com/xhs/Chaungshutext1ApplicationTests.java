package com.xhs;

import com.xhs.util.JwtUtils;
import io.jsonwebtoken.Claims;
import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class Chaungshutext1ApplicationTests {

 /*   @Test
    void contextLoads() {
    }*/
    @Autowired
    private JwtUtils jwtUtils;

    @Test
    void testGenerateAndParseToken() {
        String token = jwtUtils.generateToken(1);
        Claims claims = jwtUtils.parseToken(token);
        assertEquals(1, claims.get("userId"));
    }

    }



