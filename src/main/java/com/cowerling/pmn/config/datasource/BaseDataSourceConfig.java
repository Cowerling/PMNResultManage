package com.cowerling.pmn.config.datasource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class BaseDataSourceConfig {
    @Value("${database.driver}")
    private String _driver;

    private static final String SET_DRIVER_METHOD_NAME = ".setDriver";

    private static String driver;

    public static DriverManagerDataSource driverManagerDataSource(String url, String username, String password) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;
    }

    private static void setDriver(String driver) {
        BaseDataSourceConfig.driver = driver;
    }

    @Bean
    public MethodInvokingFactoryBean setDriverMethodInvokingFactoryBean() {
        MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
        methodInvokingFactoryBean.setStaticMethod(this.getClass().getName() + SET_DRIVER_METHOD_NAME);
        methodInvokingFactoryBean.setArguments(_driver);
        return methodInvokingFactoryBean;
    }

    @Bean
    public BeanPostProcessor persistenceTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }
}
