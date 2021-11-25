package com.susstore.config.swagger;

import com.google.common.collect.Lists;
import com.spring4all.swagger.SwaggerProperties;
import com.susstore.result.ResultCode;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private Boolean enable = false;

    private ApiInfo apiInfo(){
        return ApiInfo.DEFAULT;
    }

    @Bean
    public Docket createRestApi() {
        List<ResponseMessage> responseMessageList = List.of(
                new ResponseMessageBuilder().
                        code(ResultCode.ACCESS_DENIED.code).
                        message(ResultCode.ACCESS_DENIED.getMessage()).
                        responseModel(new ModelRef(ResultCode.ACCESS_DENIED.getMessage())).build(),
                new ResponseMessageBuilder().
                        code(ResultCode.USER_NOT_LOGIN.code).
                        message(ResultCode.USER_NOT_LOGIN.getMessage()).
                        responseModel(new ModelRef(ResultCode.USER_NOT_LOGIN.getMessage())).build(),
                new ResponseMessageBuilder().
                        code(ResultCode.SUCCESS.code).
                        message(ResultCode.SUCCESS.getMessage()).
                        responseModel(new ModelRef(ResultCode.SUCCESS.getMessage())).build()
        );


        return new Docket(DocumentationType.SWAGGER_2)
                .globalResponseMessage(RequestMethod.GET, responseMessageList)
                .globalResponseMessage(RequestMethod.POST, responseMessageList)
                .globalResponseMessage(RequestMethod.PUT, responseMessageList)
                .globalResponseMessage(RequestMethod.DELETE, responseMessageList)
                .apiInfo(apiInfo())
                .select()
                //加了ApiOperation注解的类，才生成接口文档
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                //包下的类，才生成接口文档
                //.apis(RequestHandlerSelectors.basePackage("io.sysssc.controller"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(security())
                .enable(enable)
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
