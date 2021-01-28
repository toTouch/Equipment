package com.xiliulou.afterserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.xiliulou.afterserver.mapper")
public class AfterServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AfterServerApplication.class, args);
	}

}
