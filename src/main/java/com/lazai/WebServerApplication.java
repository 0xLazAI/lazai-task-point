package com.lazai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Hello world!
 *
 */
@EnableScheduling
@SpringBootApplication
public class WebServerApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(WebServerApplication.class, args);
    }
}
