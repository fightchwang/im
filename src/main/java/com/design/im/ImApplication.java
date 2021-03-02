package com.design.im;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.design.im.dao")
@EnableTransactionManagement
public class ImApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImApplication.class, args);
	}

}
