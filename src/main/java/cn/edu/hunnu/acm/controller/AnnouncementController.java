package cn.edu.hunnu.acm.controller;

import cn.edu.hunnu.acm.framework.annotation.Controller;
import cn.edu.hunnu.acm.framework.annotation.GetMapping;
import cn.edu.hunnu.acm.framework.annotation.Qualifier;
import cn.edu.hunnu.acm.framework.annotation.RequestParam;
import cn.edu.hunnu.acm.model.Announcement;
import cn.edu.hunnu.acm.service.AnnouncementService;
import cn.edu.hunnu.acm.util.Constants;
import cn.edu.hunnu.acm.util.DataMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AnnouncementController {
    @Qualifier
    private AnnouncementService announcementService;

    @GetMapping(value = "/homePage", produces = "application/json;charset=utf-8")
    public DataMap getHomePage() {
        Map<String, Object> page = announcementService.getHomePage();
        List<String> carouselList = (List<String>) page.get("carouselList");
        List<Announcement> reportList = (List<Announcement>) page.get("reportList");
        List<Announcement> solutionList = (List<Announcement>) page.get("solutionList");

        DataMap dataMap = new DataMap();

        if(carouselList != null) {
            dataMap.set("carousels", carouselList);
        }

        if(reportList != null) {
            Map<String, Object> reports = new HashMap<>();
            List<Object> rpArray = new ArrayList<>();
            reports.put("sectionName", Constants.announcementType.ReportName);
            for(Announcement report : reportList) {
                Map<String, Object> rp = new HashMap<>();
                rp.put("newsId", report.getNewsId());
                rp.put("title", report.getTitle());
                rp.put("updateTime", report.getUpdateTime());
                rpArray.add(rp);
            }
            reports.put("messages", rpArray);
            dataMap.set("reports", reports);
        }

        if(solutionList != null) {
            Map<String, Object> solutions = new HashMap<>();
            List<Object> soArray = new ArrayList<>();
            solutions.put("sectionName", Constants.announcementType.SolutionName);
            for(Announcement solution : solutionList) {
                Map<String, Object> so = new HashMap<>();
                so.put("newsId", solution.getNewsId());
                so.put("title", solution.getTitle());
                so.put("updateTime", solution.getUpdateTime());
                soArray.add(so);
            }
            solutions.put("messages", soArray);
            dataMap.set("solutions", solutions);
        }

        return dataMap.success();
    }

    @GetMapping(value = "/announcementList", produces = "application/json;charset=utf-8")
    public DataMap getAnnouncementList(@RequestParam("newsType") String type, @RequestParam(value = "page", defaultValue = "1") Integer page) {
        Short newsType = null;
        String section = null;

        switch (type) {
            case "report" :
                newsType = Constants.announcementType.ReportType;
                section = Constants.announcementType.ReportName;
                break;
            case "solution" :
                newsType = Constants.announcementType.SolutionType;
                section = Constants.announcementType.SolutionName;
                break;
            default:
                newsType = -1;
                section = "未知的版块";
        }

        int limit = Constants.pagination.newsLimit;
        int offset = (page - 1) * limit;

        Map<String, Object> announcements = announcementService.queryAnnouncementList(newsType, offset, limit);
        Integer announcementCount = (Integer) announcements.get("announcementCount");
        List<Announcement> announcementList = (List<Announcement>) announcements.get("announcementList");

        DataMap dataMap = new DataMap();

        dataMap.set("announcementCount", announcementCount);

        if(announcementList != null) {
            dataMap.set("sectionName", section);

            List<Object> array = new ArrayList<>();
            for(Announcement announcement : announcementList) {
                Map<String, Object> ns = new HashMap<>();
                ns.put("newsId", announcement.getNewsId());
                ns.put("title", announcement.getTitle());
                ns.put("creator", announcement.getCreator());
                ns.put("createTime", announcement.getCreateTime());
                ns.put("updateTime",announcement.getUpdateTime());

                array.add(ns);
            }
            dataMap.set("announcementList", array);
        }
        return dataMap.success();
    }

    @GetMapping(value = "/announcementDetail", produces = "application/json;charset=utf-8")
    public DataMap getAnnouncementDetail(@RequestParam("newsId") Integer newsId) {
        Announcement announcement = announcementService.queryAnnouncementById(newsId);

        DataMap dataMap = new DataMap();
        if(announcement != null) {
            dataMap.set("newsId", announcement.getNewsId());
            dataMap.set("title", announcement.getTitle());
            dataMap.set("creator", announcement.getCreator());
            dataMap.set("createTime", announcement.getCreateTime());
            dataMap.set("updateTime", announcement.getUpdateTime());
            dataMap.set("content", announcement.getContent());

            return dataMap.success();
        } else {
            dataMap.setErrorInfo(Constants.errorMessage.news_not_found);
            return dataMap.fail();
        }
    }
}
