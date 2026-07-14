package com.jurajvetrak.taskmanagement.support;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@PostgresIntegrationTest
@AutoConfigureMockMvc
public @interface ApiIntegrationTest {

}
