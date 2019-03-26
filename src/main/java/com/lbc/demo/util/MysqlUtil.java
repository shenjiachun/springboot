package com.lbc.demo.util;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Configuration
public class MysqlUtil {
    private static final Log LOG = LogFactory.getLog(MysqlUtil.class);
    private static BasicDataSource dataSource;
    @Value("${spring.datasource.driver-class-name}")
    private String driver;
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String user;
    @Value("${spring.datasource.password}")
    private String password;

    public static Connection getConnection() throws SQLException {
        Connection conn = null;
        if (dataSource != null) {
            conn = dataSource.getConnection();
//            LOG.info("building mysql conn success");
        }
        return conn;
    }

    public static void close(Connection con, PreparedStatement ps, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Bean
    public BasicDataSource setupDataSource() throws IOException {
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setUrl(url);
        // 初始化数量
        dataSource.setInitialSize(20);
        // 最大连接数
        dataSource.setMaxTotal(50);
        // 空闲时间
        dataSource.setMaxIdle(-1);
        dataSource.setMinIdle(-1);
        // 等待时间
        dataSource.setMaxWaitMillis(-1);
        dataSource.setRemoveAbandonedOnMaintenance(true);
        dataSource.setRemoveAbandonedTimeout(300);
        dataSource.setRemoveAbandonedOnBorrow(true);
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        return dataSource;
    }
}
