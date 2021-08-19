package com.qunhe.instdeco.counterslow;

import com.qunhe.authnet.ssoclient.config.AutoKuAuth;
import com.qunhe.instdeco.commoditycenter.configuration.EnableCommodityCenterConfiguration8;
import com.qunhe.instdeco.counterslow.configuration.SoaConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @author tumei
 */
@Import(SoaConfiguration.class)
@EnableCommodityCenterConfiguration8
@SpringBootApplication(scanBasePackages = {"com.qunhe.instdeco.counterslow"})
@MapperScan(basePackages = {"com.qunhe.instdeco.counterslow.dao"})
@ComponentScan(basePackages = {"com.qunhe.devops", "com.qunhe.instdeco.counterslow"})
@AutoKuAuth
@EnableRedisHttpSession
public class CounterSlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(CounterSlowApplication.class, args);
    }
}
