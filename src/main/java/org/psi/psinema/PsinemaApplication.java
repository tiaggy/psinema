package org.psi.psinema;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;

@SpringBootApplication
@EnableScheduling
public class PsinemaApplication {

    public static void main(String[] args) {
        String[] effectiveArgs = Arrays.stream(args)
                .map(a -> a.equals("--dev") ? "--app.dev-mode=true" : a)
                .toArray(String[]::new);
        SpringApplication.run(PsinemaApplication.class, effectiveArgs);
    }

}
