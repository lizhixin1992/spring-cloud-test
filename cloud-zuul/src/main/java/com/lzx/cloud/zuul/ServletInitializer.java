package com.lzx.cloud.zuul;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @ClassName
 * @Description TODO
 * @Date 2019-07-17 09:26
 **/
public class ServletInitializer extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(CloudZuulApplication.class);
    }
}