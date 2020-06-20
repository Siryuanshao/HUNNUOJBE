package cn.edu.hunnu.acm.service;

import cn.edu.hunnu.acm.model.Announcement;

import java.util.List;
import java.util.Map;


public interface AnnouncementService {
    Map<String, Object> getHomePage();
    Announcement queryAnnouncementById(Integer newsId);
    Map<String, Object> queryAnnouncementList(Short newsType, int offset, int limit);
    List<Announcement> queryContestAnnouncementList(Integer contestId);
    void createAnnouncement(Announcement announcement);
    void updateAnnouncement(Announcement announcement);
}
