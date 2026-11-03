package pl.edu.ug.cmms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath:beans.xml")
public class Main {

    public static void main(String[] args) {

        SpringApplication.run(Main.class, args);

    }

}
