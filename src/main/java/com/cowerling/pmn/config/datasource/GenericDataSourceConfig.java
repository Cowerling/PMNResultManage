package com.cowerling.pmn.config.datasource;

import com.cowerling.pmn.annotation.GenericData;
import org.mybatis.spring.MyBatisExceptionTranslator;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = "com.cowerling.pmn.data.mapper", sqlSessionFactoryRef = "genericSessionFactory")
public class GenericDataSourceConfig {
    @Value("${database.url}")
    private String databaseUrl;

    @Value("${database.username}")
    private String databaseUsername;

    @Value("${database.password}")
    private String databasePassword;

    private static final String GENERIC_SESSION_FACTORY_BEAN_NAME = "genericSessionFactory";
    private static final String GENERIC_TYPE_ALIASES_PACKAGE = "com.cowerling.pmn.domain",
            GENERIC_TYPE_HANDLERS_PACKAGE = "com.cowerling.pmn.data.type";

    @Profile("development")
    @Bean
    @GenericData
    public DataSource jdbcGenericDataSource() {
        return BaseDataSourceConfig.driverManagerDataSource(databaseUrl, databaseUsername, databasePassword);
    }

    @Bean(GENERIC_SESSION_FACTORY_BEAN_NAME)
    @GenericData
    public SqlSessionFactoryBean mybatisGenericSessionFactory(@GenericData DataSource dataSource) {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setTypeAliasesPackage(GENERIC_TYPE_ALIASES_PACKAGE);
        sqlSessionFactoryBean.setTypeHandlersPackage(GENERIC_TYPE_HANDLERS_PACKAGE);
        //org.apache.ibatis.logging.LogFactory.useLog4JLogging();
        return sqlSessionFactoryBean;
    }

    @Bean
    public DataSourceTransactionManager myBatisGenericTransactionManager(@GenericData DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public MyBatisExceptionTranslator myBatisExceptionTranslator(@GenericData DataSource dataSource) {
        return new MyBatisExceptionTranslator(dataSource, false);
    }
}
