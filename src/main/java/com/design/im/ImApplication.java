package com.design.im;

import com.design.im.api.IMWebSocket;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.design.im.dao")
@EnableTransactionManagement
public class ImApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext appConetxt = SpringApplication.run(ImApplication.class, args);
		IMWebSocket.setApplicationContext(appConetxt);
	}

}
