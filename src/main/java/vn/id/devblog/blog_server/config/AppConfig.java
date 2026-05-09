package vn.id.devblog.blog_server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import vn.id.devblog.blog_server.security.JwtHttpInterceptor;

@Configuration
public class AppConfig implements WebMvcConfigurer {
    private JwtHttpInterceptor jwtInterceptor;

    @Autowired
    public AppConfig(JwtHttpInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                //API need token
                .addPathPatterns("/api/v1/post/**")
                .addPathPatterns("/api/v1/upload/**")
                .addPathPatterns("/api/v1/dashboard/**");

    }
}