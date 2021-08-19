package com.qunhe.instdeco.counterslow.configuration;

import com.google.common.collect.ImmutableList;
import com.qunhe.authnet.ssoclient.filter.FilterConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author tumei
 */
@Configuration
public class SsoFilterConfiguration {

    @Bean
    public FilterConfig filterConfig() {
        return new FilterConfig(
                ImmutableList.<String>builder()
                        .add("/counter/sql")
                        .add("/counter/monitor")
                        .add("/healthy")
                        .build(),
                ImmutableList.of(),
                ImmutableList.of(),
                true
        );
    }

}
