package com.design.im.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

import static com.design.im.util.Constants.REFRESH_TOKEN_HEADER;
import static com.design.im.util.Constants.TOKEN_HEADER;

@Configuration
@EnableSwagger2
public class SwaggerConfig {


    @Bean
    public Docket createRestApi(){
        ParameterBuilder token = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        token.name(TOKEN_HEADER).description("user token")
                .modelRef(new ModelRef("string")).parameterType("header")
                .required(false).build();

        ParameterBuilder refreshToken = new ParameterBuilder();
        refreshToken.name(REFRESH_TOKEN_HEADER).description("refresh token")
                .modelRef(new ModelRef("string")).parameterType("header")
                .required(false).build();
        pars.add(token.build());
        pars.add(refreshToken.build());

        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.design.im"))
                .paths(PathSelectors.any()).build().globalOperationParameters(pars);
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("IM API Doc")
                .description("This is a restful api document of IM.")
                .version("1.0")
                .build();
    }

}