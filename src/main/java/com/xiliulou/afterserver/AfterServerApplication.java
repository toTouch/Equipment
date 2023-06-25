package com.xiliulou.afterserver;

import com.xiliulou.afterserver.config.MinioConfig;
import com.xiliulou.cache.redis.EnableRedis;
import com.xiliulou.iot.EnableIot;
import com.xiliulou.mq.EnableMq;
import com.xiliulou.storage.EnableStorage;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@MapperScan("com.xiliulou.afterserver.mapper")
@EnableConfigurationProperties
@EnableRedis
@EnableStorage
@EnableMq
@EnableIot
public class AfterServerApplication {

    public static void main(String[] args) {

        SpringApplication.run(AfterServerApplication.class, args);

    }

}
