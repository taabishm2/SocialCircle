package com.socialcircle.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import com.mashape.unirest.http.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@EnableWebMvc
@Configuration
@EnableSwagger2
@EnableScheduling
@ComponentScan({"com.socialcircle"})
@PropertySources({@PropertySource(value = "file:./socialcircle.properties")})
public class SpringConfig implements WebMvcConfigurer {

    @Autowired
    private ApplicationContext applicationContext;

    public static final String PACKAGE_CONTROLLER = "com.socialcircle.controller";

    private static final int BROWSER_CACHE_CONTROL = 604800;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/static/**").addResourceLocations("/static/").setCachePeriod(BROWSER_CACHE_CONTROL);
        registry.addResourceHandler("/socialcircle/**").addResourceLocations("/socialcircle/static/").setCachePeriod(BROWSER_CACHE_CONTROL);
    }

    @Bean
    public ITemplateResolver templateResolver() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setApplicationContext(applicationContext);
        resolver.setPrefix("/html/");
        resolver.setTemplateMode(TemplateMode.HTML);
        return resolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        templateEngine.setEnableSpringELCompiler(true);
        return templateEngine;
    }

    @Bean
    public ThymeleafViewResolver viewResolver() {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine());
        return viewResolver;
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)//
                .useDefaultResponseMessages(false)//
                .select().apis(RequestHandlerSelectors.basePackage(PACKAGE_CONTROLLER))//
                .paths(PathSelectors.regex(".*"))//
                .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(ZonedDateTime.class,
                new ZonedDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")));
        return Jackson2ObjectMapperBuilder.json().featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) // ISODate
                .modules(javaTimeModule).build();
    }

    @Bean
    public BCryptPasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public Unirest unirest() {
        return new Unirest();
    }

}