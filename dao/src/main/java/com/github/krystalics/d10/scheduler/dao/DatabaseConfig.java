package com.github.krystalics.d10.scheduler.dao;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * @author linjiabao001
 * @date 2021/10/2
 * @description
 */
@MapperScan(basePackages = "com.gihub.krystalics.d10.scheduler.dao.mapper", sqlSessionFactoryRef = "baseSqlSessionFactory")
@EnableTransactionManagement
@Configuration
public class DatabaseConfig {
    @Bean("baseDataSource")
    public DataSource dataSource() {
        return new HikariDataSource();
    }

    @Bean(name = "baseSqlSessionFactory")
    public SqlSessionFactory baseSqlSessionFactory() throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource());
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mappers/*.xml"));
        return factoryBean.getObject();
    }
}
