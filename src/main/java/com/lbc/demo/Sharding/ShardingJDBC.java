package com.lbc.demo.Sharding;


import com.google.common.collect.Maps;
import com.lbc.demo.constants.MonitorConstants;
import io.shardingsphere.api.config.ShardingRuleConfiguration;
import io.shardingsphere.api.config.TableRuleConfiguration;
import io.shardingsphere.api.config.strategy.StandardShardingStrategyConfiguration;
import io.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

/**
 */
@Configuration
@MapperScan(basePackages = "com.lenovo.corgi.dao.mapper", sqlSessionTemplateRef = "sqlSessionTemplate")
@EnableTransactionManagement
public class ShardingJDBC {
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    @Bean
    @Primary
    public DataSource shardingDataSource() throws Exception {

        //dataSource
        Properties properties = new Properties();
        properties.put("driverClassName", driverClassName);
        properties.put("url", url);
        properties.put("username", username);
        properties.put("password", password);
        DataSource dataSource = BasicDataSourceFactory.createDataSource(properties);
        Map<String, DataSource> dataSourceMap = Maps.newHashMap();
        dataSourceMap.put("ds", dataSource);

        //container_stats
        TableRuleConfiguration containerStatsTableRuleConfig = new TableRuleConfiguration();
        containerStatsTableRuleConfig.setLogicTable(MonitorConstants.CONTAINER_STATS);
        containerStatsTableRuleConfig.setTableShardingStrategyConfig(
                new StandardShardingStrategyConfiguration(
                        MonitorConstants.CONTAINER_STATS_SHARDING,
                        new DatePreciseShardingAlgorithm(MonitorConstants.CONTAINER_STATS),
                        new DateRangeShardingAlgorithm(MonitorConstants.CONTAINER_STATS)
                )
        );

        TableRuleConfiguration appStateTableRuleConfig = new TableRuleConfiguration();
        appStateTableRuleConfig.setLogicTable(MonitorConstants.APP_STATE);
        appStateTableRuleConfig.setTableShardingStrategyConfig(
                new StandardShardingStrategyConfiguration(
                        MonitorConstants.APP_STATE_SHARDING,
                        new DatePreciseShardingAlgorithm(MonitorConstants.APP_STATE),
                        new DateRangeShardingAlgorithm(MonitorConstants.APP_STATE)
                )
        );
//        appStateTableRuleConfig.setTableShardingStrategyConfig(
//                new ComplexShardingStrategyConfiguration("user_id,order_id",
//                        new ComplexShardingAlgorithm(MonitorConstants.APP_STATE)));

        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        Collection<TableRuleConfiguration> tableRuleConfigs = shardingRuleConfig.getTableRuleConfigs();
        tableRuleConfigs.add(containerStatsTableRuleConfig);
        tableRuleConfigs.add(appStateTableRuleConfig);

        dataSource = ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig, Maps.newConcurrentMap(), new Properties());

        return dataSource;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(shardingDataSource());
        sqlSessionFactoryBean.setTypeAliasesPackage("com.lbc.demo.entity");

        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

        sqlSessionFactoryBean.setMapperLocations(resourcePatternResolver.getResources("classpath:mapping/*.xml"));
        return sqlSessionFactoryBean.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {

        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean
    public PlatformTransactionManager platformTransactionManager() throws Exception {

        return new DataSourceTransactionManager(shardingDataSource());
    }


}
