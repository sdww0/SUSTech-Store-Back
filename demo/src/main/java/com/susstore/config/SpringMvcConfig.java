package com.susstore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/")
                .addResourceLocations("classpath:/resources/")
                .addResourceLocations("classpath:/static/")
                .addResourceLocations("classpath:/public/");
        registry.addResourceHandler("/user/**").addResourceLocations("file:"+ Constants.USER_UPLOAD_PATH);
        registry.addResourceHandler("/goods/**").addResourceLocations("file:"+ Constants.GOODS_UPLOAD_PATH);

        WebMvcConfigurer.super.addResourceHandlers(registry);



    }



}
