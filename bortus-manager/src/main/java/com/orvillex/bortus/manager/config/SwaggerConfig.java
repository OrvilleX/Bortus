package com.orvillex.bortus.manager.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

@Configuration
@EnableSwagger2WebMvc
@ConditionalOnProperty(name = "swagger.enable",  havingValue = "true")
public class SwaggerConfig {
    @Bean
    public Docket createRestApi() {
         return new Docket(DocumentationType.SWAGGER_2)
                  .apiInfo(apiInfo())
                  .pathMapping("/")
                  .select()
                  .apis(RequestHandlerSelectors.basePackage("com.orvillex.bortus.manager.modules"))
                  .paths(PathSelectors.any())
                  .build();
    }
    
    private ApiInfo apiInfo() {
         return new ApiInfoBuilder()
                  .title("BortusUI接口文档")
                  .contact(new Contact("Y-Z-F",  "https://github.com/orvillex",  "yaozhengfa@orvillex.com"))
                  .version("1.0")
                  .description("此API为综合主题接口文档")
                  .build();
    }
}
