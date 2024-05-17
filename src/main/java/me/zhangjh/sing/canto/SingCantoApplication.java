package me.zhangjh.sing.canto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"me.zhangjh"})
public class SingCantoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SingCantoApplication.class, args);
    }

}
