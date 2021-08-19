package com.qunhe.instdeco.counterslow.configuration;

import com.qunhe.rpc.integration.SpringZookeeperRegistryFactory;
import com.qunhe.rpc.registry.Registry;
import com.qunhe.rpc.route.ApiRegistry;
import com.qunhe.rpc.route.ServiceContainer;
import com.qunhe.rpc.route.impl.ServiceContainerImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author tumei
 */
@Configuration
public class SoaConfiguration {
    private static final String DEFAULT_STAGE = "dev";
    private static final String SERVICE_NAME = "com.qunhe.instdeco.pangtututools";
    private static final String DEV_ZKS = "10.1.7.101:2181," +
            "10.1.7.102:2181," +
            "10.1.7.103:2181" +
            "/qunhe_service";
    private static final String PROD_ZKS = "10.111.6.218:2181," +
            "10.111.2.51:2181," +
            "10.111.5.144:2181," +
            "10.111.6.219:2181," +
            "10.111.2.52:2181," +
            "10.111.5.145:2181," +
            "10.111.6.220:2181" +
            "/qunhe_service";
    private static final String[] VIPS = new String[]{"com.qunhe.exabrain.commoditycenterservice"};

    @Bean("projectStage")
    public String getStage() {
        final String stage = System.getProperty("stage");
        if (stage != null) {
            return stage;
        }
        return DEFAULT_STAGE;
    }

    @Bean("apiRegistry")
    public ApiRegistry mApiRegistry(@Qualifier("projectStage") final String stage) {
        final SpringZookeeperRegistryFactory springZookeeperRegistryFactory = new SpringZookeeperRegistryFactory();
        if (DEFAULT_STAGE.equals(stage)) {
            springZookeeperRegistryFactory.setUrls(DEV_ZKS);
        } else {
            springZookeeperRegistryFactory.setUrls(PROD_ZKS);
        }
        springZookeeperRegistryFactory.setStage(stage);
        final Registry registry = springZookeeperRegistryFactory.getRegistryService();
        final ServiceContainer serviceContainer = new ServiceContainerImpl(SERVICE_NAME, registry, VIPS);
        return new ApiRegistry(serviceContainer);
    }
}
