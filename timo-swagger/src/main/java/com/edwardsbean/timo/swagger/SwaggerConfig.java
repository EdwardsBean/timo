package com.edwardsbean.timo.swagger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;

/**
 * swagger相关配置，用于加载swagger相关类
 * Created by edwardsbean on 2015/4/7.
 */
@Configuration
@EnableSwagger
public class SwaggerConfig {

    private String projectDescription;
    private String projectName;
    private String email;

    private SpringSwaggerConfig springSwaggerConfig;

    /**
     * Required to autowire SpringSwaggerConfig
     */
    @Autowired
    public void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
        this.springSwaggerConfig = springSwaggerConfig;
    }

    /**
     * Every SwaggerSpringMvcPlugin bean is picked up by the swagger-mvc
     * framework - allowing for multiple swagger groups i.e. same code base
     * multiple swagger resource listings.
     */
    @Bean
    public SwaggerSpringMvcPlugin customImplementation() {
        return new SwaggerSpringMvcPlugin(this.springSwaggerConfig).apiInfo(apiInfo()).includePatterns(
                ".*?");
    }


    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfo(
                projectName,
                projectDescription,
                "My Apps API terms of service",
                email,
                "My Apps API Licence Type",
                "My Apps API License URL");
        return apiInfo;
    }
}