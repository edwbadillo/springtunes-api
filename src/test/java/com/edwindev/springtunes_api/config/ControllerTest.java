package com.edwindev.springtunes_api.config;

import com.edwindev.springtunes_api.auth.JwtFilter;
import com.edwindev.springtunes_api.config.security.TestSecurityConfig;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to be used on a controller test class to exclude the
 * JwtFilter from the test
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@WebMvcTest(excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = JwtFilter.class
))
@Import(TestSecurityConfig.class)
public @interface ControllerTest {
    Class<?>[] controllers();
}