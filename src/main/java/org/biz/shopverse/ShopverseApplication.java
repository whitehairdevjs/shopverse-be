package org.biz.shopverse;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("org.biz.shopverse.mapper")
@EnableScheduling
public class ShopverseApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopverseApplication.class, args);
    }

}
