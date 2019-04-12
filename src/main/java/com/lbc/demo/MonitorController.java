package com.lenovo.corgi;

import com.lenovo.corgi.service.AppInfoService;
import com.lenovo.corgi.service.BillService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
public class MonitorController {
    private static final Log LOG = LogFactory.getLog(MonitorController.class);

    @Autowired
    AppInfoService appInfoService;
    @Autowired
    BillService billService;

    @RequestMapping(value = "/mng", method = RequestMethod.GET)
    public String manageGpuLable(@RequestParam String sDay, @RequestParam String eDay, @RequestParam String v100TOP) {
        appInfoService.manageGpuLables(sDay, eDay, "request", v100TOP);
        billService.makeUpUserCoins();
        return "done";
    }

    @RequestMapping(value = "/addWhiteList", method = RequestMethod.POST)
    public String addWhiteList(@RequestParam String user) {
        appInfoService.addWhiteList(user);
        return "add finish ";
    }

    @RequestMapping(value = "/rmWhiteList", method = RequestMethod.POST)
    public String rmWhiteList(@RequestParam String user) {
        appInfoService.rmWhiteList(user);
        return "rm finish ";
    }

}
