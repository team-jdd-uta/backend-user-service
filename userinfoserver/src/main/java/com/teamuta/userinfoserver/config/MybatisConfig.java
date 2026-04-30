package com.teamuta.userinfoserver.config;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "com.teamuta.userinfoserver.repository", annotationClass = Mapper.class)
public class MybatisConfig {
}
