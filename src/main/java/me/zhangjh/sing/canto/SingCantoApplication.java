package me.zhangjh.sing.canto;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("me.zhangjh.sing.canto.dao.mapper")
public class SingCantoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SingCantoApplication.class, args);
    }

}
