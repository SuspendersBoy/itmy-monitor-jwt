package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


class MyProjectBackendApplicationTests {
    @Test
    void contextLoads() {

        System.out.println(new BCryptPasswordEncoder().encode("123456"));
    }

}
