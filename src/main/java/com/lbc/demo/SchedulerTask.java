package com.lenovo.corgi;

import com.lenovo.corgi.bean.AppInfoBean;
import com.lenovo.corgi.entity.ContainerStats;
import com.lenovo.corgi.service.AgentInfoService;
import com.lenovo.corgi.service.AppInfoService;
import com.lenovo.corgi.service.BillService;
import com.lenovo.corgi.service.ExMailService;
import com.lenovo.corgi.service.FreeChartsService;
import com.lenovo.corgi.service.RedisService;
import com.lenovo.corgi.utils.AppIdMapCache;
import com.lenovo.corgi.utils.MapCacheManager;
import com.lenovo.corgi.utils.StatsMapCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map.Entry;

@EnableScheduling
@Component
public class SchedulerTask {


    private static final Log LOG = LogFactory.getLog(SchedulerTask.class);
    static MapCacheManager cacheMap = MapCacheManager.getInstance();
    static AppIdMapCache resultMap = AppIdMapCache.getInstance();
    @Autowired
    RedisService redisService;
    @Autowired
    AgentInfoService agentInfoService;
    @Autowired
    AppInfoService appInfoService;
    @Autowired
    FreeChartsService freeChartsService;
    @Autowired
    ExMailService exMailService;
    @Autowired
    BillService billService;

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        JettyEmbeddedServletContainerFactory factory = new JettyEmbeddedServletContainerFactory();
        return factory;
    }

    @Scheduled(initialDelay = 1000, fixedDelay = 5000)
    public AppIdMapCache saveDataToMysqlandCheck() {

        /**
         * 从 grpc stub 取出agentInfo 然后保存到mysql中。
         *
         * 在 save之前，检出agent的异常信息保存到resultMap中 ，发送邮件
         *
         */
        try {
            agentInfoService.saveAgentBean(resultMap);
        } catch (SQLException e) {
            LOG.info(e.toString());
        }


        //get appInfo from stub
        List<AppInfoBean> appInfoBeanlist = appInfoService.setAppInfoBeanList();

        /**
         *
         *mail service not save data
         */
        try {
            appInfoService.saveAppInfoBean(appInfoBeanlist);
        } catch (SQLException e) {
            LOG.info(e.toString());
        }

        /**
         *
         *
         * cacheMap: 缓存的任务状态（每个正在运行的任务都会有个）--（appId:{})
         *
         * resultMap: 检出的需要发送任务的任务 (appId:{})
         *
         * appaId:{}
         */

        MapCacheManager cacheMap1 = appInfoService.updateToMap(appInfoBeanlist, cacheMap, resultMap);


        try {
            appInfoService.checkOutEndAppIdFromMap(cacheMap1, resultMap);
        } catch (Exception e) {
            LOG.info(e.toString());
        }

        try {
            appInfoService.checkOutSubmittingAppIdFromMap(cacheMap1, resultMap);
        } catch (Exception e) {
            LOG.info(e.toString());
        }
        LOG.info("data loading completed");
        return resultMap;
    }


    //    @Scheduled(initialDelay = 1000, fixedDelay = 10000)
    public void sendEmail() throws IOException {
        for (Entry<String, String> value : resultMap.getEntrySet()) {

            String mapKey = value.getKey();
            String mapValue = value.getValue();
            if ("EW".equals(mapValue)) {
                try {
                    exMailService.getUserInfoAndSendEnd(mapKey);
                } catch (Exception e) {
                    LOG.error(e.getMessage());
                }
                resultMap.removeCache(mapKey);
            }
            if ("SW".equals(mapValue)) {
                try {
                    exMailService.getUserInfoAndSendBegin(mapKey);
                } catch (Exception e) {
                    LOG.info(e.toString());
                }
                resultMap.setCache(mapKey, "SWED");

            }
            if ("SWEDEML".equals(mapValue)) {
                try {
                    exMailService.getUserInfoAndSendRunning(mapKey);
                } catch (Exception e) {
                    LOG.info(e.toString());
                }
                resultMap.removeCache(mapKey);

            }
            if ("AW".equals(mapValue)) {
                try {
                    exMailService.sendWarnOfAgent(mapKey);
                } catch (Exception e) {
                    LOG.info(e.toString());
                }
                resultMap.setCache(mapKey, "agentWarnHaveSended");
            }
            if ("interval24".equals(mapValue)) {
                try {
                    exMailService.sendWarnOfOverTime(mapKey);
                    resultMap.setCache(mapKey, "interval24_done");
                } catch (Exception e) {
                    LOG.info(e.toString());
                }
                resultMap.removeCache(mapKey);
            }
            if ("interval20".equals(mapValue)) {
                try {
                    exMailService.sendRemindOver20(mapKey);
                } catch (Exception e) {
                    LOG.info(e.toString());
                }
                resultMap.setCache(mapKey, "interval20_done");
            }
            if ("maxTime".equals(mapValue)) {
                try {
                    exMailService.sendRemindOcptOver(mapKey);
                } catch (Exception e) {
                    LOG.info(e.toString());
                }
                resultMap.removeCache(mapKey);
            }
        }
    }

//    @Scheduled(cron = "0 0 0 ? * MON")
//    public void makeUpUserCoins() {
//        billService.makeUpUserCoins();
//    }

    @Scheduled(initialDelay = 1000, fixedDelay = 5000)
    public void stats() {
        try {
            //get gpu stats
            List<AppInfoBean> appInfoBeanlist = appInfoService.setAppInfoBeanList();
            List<ContainerStats> statsList = appInfoService.getStats(appInfoBeanlist);
            agentInfoService.statsAgentBeanList(statsList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(initialDelay = 1000, fixedDelay = 5000)
    public void saveStats() {
        StatsMapCache statsMapCache = StatsMapCache.getInstance();
        agentInfoService.saveStats(statsMapCache);
        LOG.info("statsMapCache size:" + statsMapCache.getEntrySet().size());
    }

}
