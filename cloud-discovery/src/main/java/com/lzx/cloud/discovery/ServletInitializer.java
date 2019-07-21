package com.lzx.cloud.discovery;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @ClassName
 * @Description TODO
 * @Date 2019-07-17 09:28
 **/
public class ServletInitializer extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(CloudDiscoveryApplication.class);
    }
}