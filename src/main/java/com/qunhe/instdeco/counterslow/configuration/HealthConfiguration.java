package com.qunhe.instdeco.counterslow.configuration;

import com.qunhe.devops.probe.AbstractHealthHandler;
import com.qunhe.devops.probe.DefaultHealthHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author dahui
 * @date 2021/02/07 11:50
 */

@Configuration
public class HealthConfiguration {

    @Bean
    public AbstractHealthHandler healthHandler() {
        return new DefaultHealthHandler();
    }

}
