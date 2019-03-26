package com.lbc.demo;


import com.lbc.demo.Sharding.ShardingJDBC;
import com.lbc.demo.dao.mapper.AppStateMapper;
import com.lbc.demo.entity.AppState;
import com.lbc.demo.util.DateCtrlUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@MybatisTest
@Import(ShardingJDBC.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AppStateTest {

    @Autowired
    AppStateMapper appStateMapper;

    @Test
    @Rollback(false)
    public void testShardingInsert() throws Exception {
        AppState appState = new AppState();
        appState.setAppId("TEST_APPID_01");
        appState.setStartTime(new Date());
        appState.setUser("TEST_USER");
        appState.setState("RUNNING");
        appStateMapper.insert(appState);
    }

    @Test
    @Rollback(false)
    public void selectByAppId() {
        AppState appState = new AppState();
        appState.setAppId("TEST_APPID_01");
        LocalDate start = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
        LocalDate end = LocalDate.now().plusYears(1).with(TemporalAdjusters.firstDayOfYear());
        appState.setStartTime(DateCtrlUtil.localDate2Date(start));
        appState.setEndTime(DateCtrlUtil.localDate2Date(end));
        List<AppState> test_appid_01 = appStateMapper.selectByAppId(appState);

        System.out.println(test_appid_01.size());
    }

    @Test
    @Rollback(false)
    public void allByAppId() {
        AppState appState = new AppState();
        LocalDate start = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
        LocalDate end = LocalDate.now().plusYears(1).with(TemporalAdjusters.firstDayOfYear());
        appState.setStartTime(DateCtrlUtil.localDate2Date(start));
        appState.setEndTime(DateCtrlUtil.localDate2Date(end));
        List<AppState> appStates = appStateMapper.allByAppId(appState);

        System.out.println(appStates.size());
    }

    @Test
    @Rollback(false)
    public void updateByPrimaryKey() {
        AppState appState = new AppState();
        appState.setAppId("TEST_APPID_01");
        LocalDate start = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
        LocalDate end = LocalDate.now().plusYears(1).with(TemporalAdjusters.firstDayOfYear());
        appState.setStartTime(DateCtrlUtil.localDate2Date(start));
        appState.setEndTime(DateCtrlUtil.localDate2Date(end));
        List<AppState> appStates = appStateMapper.selectByAppId(appState);
        AppState appState1 = appStates.get(0);
        appState1.setEndTime(new Date());
        //update last end_time
        appStateMapper.updateEndTime(appState1);
    }


}
