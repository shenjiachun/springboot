package com.lbc.demo;

import com.lbc.demo.util.MysqlUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class MysqlUtilTest {
    @Autowired
    MysqlUtil mysqlUtil;

    //use the same configuration of application
    @Test
    public void dataSource() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = mysqlUtil.getConnection();
            String sql = "select * from user_weekly_log";
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            MysqlUtil.close(connection, preparedStatement, resultSet);
        }
    }
}
