package org.psi.psinema;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PsinemaApplication {

    public static void main(String[] args) {
        SpringApplication.run(PsinemaApplication.class, args);
    }

}
