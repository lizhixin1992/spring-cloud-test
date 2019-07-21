package com.lzx.cloud.config;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @ClassName
 * @Description TODO
 * @Date 2019-07-18 09:51
 **/
public class ServletInitializer extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(CloudConfigApplication .class);
    }
}