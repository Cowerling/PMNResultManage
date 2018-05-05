package com.cowerling.pmn.config.datasource;

import com.cowerling.pmn.annotation.GeoData;
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
@MapperScan(basePackages = "com.cowerling.pmn.geodata.mapper", sqlSessionFactoryRef = "geoSessionFactory")
public class GeoDataSourceConfig {
    @Value("${geodatabase.url}")
    private String geodatabaseUrl;

    @Value("${geodatabase.username}")
    private String geodatabaseUsername;

    @Value("${geodatabase.password}")
    private String geodatabasePassword;

    private static final String GEO_SESSION_FACTORY_BEAN_NAME = "geoSessionFactory";
    private static final String GEO_TYPE_HANDLERS_PACKAGE = "com.cowerling.pmn.geodata.type";

    @Profile("development")
    @Bean
    @GeoData
    public DataSource jdbcGeoDataSource() {
        return BaseDataSourceConfig.driverManagerDataSource(geodatabaseUrl, geodatabaseUsername, geodatabasePassword);
    }

    @Bean(GEO_SESSION_FACTORY_BEAN_NAME)
    @GeoData
    public SqlSessionFactoryBean mybatisGeoSessionFactory(@GeoData DataSource dataSource) {
        SqlSessionFactoryBean geoSqlSessionFactoryBean = new SqlSessionFactoryBean();
        geoSqlSessionFactoryBean.setDataSource(dataSource);
        geoSqlSessionFactoryBean.setTypeHandlersPackage(GEO_TYPE_HANDLERS_PACKAGE);
        return geoSqlSessionFactoryBean;
    }

    @Bean
    public DataSourceTransactionManager myBatisGeoTransactionManager(@GeoData DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public MyBatisExceptionTranslator myBatisGeoExceptionTranslator(@GeoData DataSource dataSource) {
        return new MyBatisExceptionTranslator(dataSource, false);
    }
}
