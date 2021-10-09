package com.susstore.config.swagger;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private ApiInfo apiInfo(){
        return ApiInfo.DEFAULT;
    }

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                //加了ApiOperation注解的类，才生成接口文档
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                //包下的类，才生成接口文档
                //.apis(RequestHandlerSelectors.basePackage("io.sysssc.controller"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(security())
                .securityContexts(securityContexts());         //主要关注点--统一填写一次token
    }

    private List<ApiKey> security() {
        return Lists.newArrayList(
                new ApiKey("Authorization", "Authorization", "header")
        );
    }

    private List<SecurityContext> securityContexts(){
        return Lists.newArrayList( SecurityContext.builder() .securityReferences(defaultAuth())
                .forPaths(PathSelectors.any())
                .build());
    }

    List<SecurityReference> defaultAuth() {

            AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");

            AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];

            authorizationScopes[0] = authorizationScope;

            return Lists.newArrayList( new SecurityReference("Authorization", authorizationScopes));

    }


}
