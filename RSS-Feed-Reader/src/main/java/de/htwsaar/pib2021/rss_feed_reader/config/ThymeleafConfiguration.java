package de.htwsaar.pib2021.rss_feed_reader.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration
@EnableWebMvc
public class ThymeleafConfiguration implements WebMvcConfigurer {

    private static final String[] PATH_PATTERNS = { "/static/**", "/assets/**", "/assets/css/**", "/assets/images/**",
            "/assets/js/**", "/assets/plugins/**" };

    private static final String[] RESOURCE_LOCATIONS = { "classpath:/static/", "classpath:/static/assets/",
            "classpath:/static/assets/css/", "classpath:/static/assets/images/", "classpath:/static/assets/js/",
            "classpath:/static/assets/plugins/" };

    private static final String PEFIX = "templates/";
    private static final String SUFFIX = ".html";
    private static final String TEMPLATE_MODE = "HTML";
    private static final String UTF8_ENCODING = "UTF-8";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(PATH_PATTERNS).addResourceLocations(RESOURCE_LOCATIONS);
    }

    @Bean
    public ViewResolver getViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix(PEFIX);
        resolver.setSuffix(SUFFIX);
        return resolver;
    }

    @Bean
    public ClassLoaderTemplateResolver templateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix(PEFIX);
        templateResolver.setCacheable(false);
        templateResolver.setSuffix(SUFFIX);
        templateResolver.setTemplateMode(TEMPLATE_MODE);
        templateResolver.setCharacterEncoding(UTF8_ENCODING);
        return templateResolver;
    }
}
