package com.susstore.config.swagger;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Sets;
import com.susstore.result.ResultCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.OperationBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.Example;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.Operation;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ApiListingScannerPlugin;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Component
public class SwaggerAddition implements ApiListingScannerPlugin {
    /**
     * Implement this method to manually add ApiDescriptions
     * 实现此方法可手动添加ApiDescriptions
     *
     * @param context - Documentation context that can be used infer documentation context
     * @return List of {@link ApiDescription}
     * @see ApiDescription
     */
    @Override
    public List<ApiDescription> apply(DocumentationContext context) {
        Multimap<String,Example> multimap = HashMultimap.create();
        multimap.put("email",new Example(""));
        multimap.put("password",new Example("123"));
        Operation usernamePasswordOperation = new OperationBuilder(new CachingOperationNameGenerator())
                .method(HttpMethod.POST)
                .summary("用户名密码登录")
                .notes("username/password登录")
                .consumes(Sets.newHashSet(MediaType.APPLICATION_JSON_VALUE)) // 接收参数格式
                .produces(Sets.newHashSet(MediaType.APPLICATION_JSON_VALUE)) // 返回参数格式
                .tags(Sets.newHashSet("登录"))
                .parameters(Arrays.asList(

                        new ParameterBuilder()
                                .description("默认：{ \"email\":\"\", \"password\":\"\"}")
                                .type(new TypeResolver().resolve(LoginForm.class))
                                .name("message")
                                .parameterType("body")
                                .complexExamples(multimap)
                                .parameterAccess("access")
                                .required(true)
                                .modelRef(new ModelRef("body"))
                                .build(),
//                        new ParameterBuilder()
//                                .description("密码")
//                                .type(new TypeResolver().resolve(String.class))
//                                .name("password")
//                                .defaultValue("123456")
//                                .parameterType("query")
//                                .parameterAccess("access")
//                                .required(true)
//                                .modelRef(new ModelRef("string"))
//                                .build()
                        new ParameterBuilder()
                                .description("验证码")
                                .type(new TypeResolver().resolve(String.class))
                                .name("checkCode")
                                .parameterType("query")
                                .parameterAccess("access")
                                .required(true)
                                .modelRef(new ModelRef("string"))
                                .build()

                                ))
                .responseMessages(Set.of(
                        new ResponseMessageBuilder().code(ResultCode.SUCCESS.code)
                                .message(ResultCode.SUCCESS.message)
                                .responseModel(new ModelRef(
                                        ResultCode.SUCCESS.message)
                                ).build(),
                        new ResponseMessageBuilder().code(ResultCode.CHECK_CODE_WRONG.code)
                                .message(ResultCode.CHECK_CODE_WRONG.message)
                                .responseModel(new ModelRef(
                                        ResultCode.CHECK_CODE_WRONG.message)
                                ).build(),
                        new ResponseMessageBuilder().code(ResultCode.LOGIN_FAIL.code)
                                .message(ResultCode.LOGIN_FAIL.message)
                                .responseModel(new ModelRef(
                                        ResultCode.LOGIN_FAIL.message)
                                ).build(),
                        new ResponseMessageBuilder().code(ResultCode.USER_NOT_ACTIVATE.code)
                                .message(ResultCode.USER_NOT_ACTIVATE.message)
                                .responseModel(new ModelRef(
                                        ResultCode.USER_NOT_ACTIVATE.message)
                                ).build(),
                        new ResponseMessageBuilder().code(ResultCode.USER_BANNED.code)
                                .message(ResultCode.USER_BANNED.message)
                                .responseModel(new ModelRef(
                                        ResultCode.USER_BANNED.message)
                                ).build()))
                .build();

        ApiDescription loginApiDescription = new ApiDescription("login", "/login", "登录接口",
                Collections.singletonList(usernamePasswordOperation), false);

        return Collections.singletonList(loginApiDescription);
    }

    /**
     * 是否使用此插件
     *
     * @param documentationType swagger文档类型
     * @return true 启用
     */
    @Override
    public boolean supports(DocumentationType documentationType) {
        return DocumentationType.SWAGGER_2.equals(documentationType);
    }



}
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class LoginForm{
    public String email;
    public String password;
}