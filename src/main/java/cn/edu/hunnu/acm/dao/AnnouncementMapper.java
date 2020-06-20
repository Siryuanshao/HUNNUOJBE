package cn.edu.hunnu.acm.dao;

import cn.edu.hunnu.acm.model.Announcement;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AnnouncementMapper {
    List<String> queryCarouselList();
    List<Announcement> queryHotspotList(Short newsType);
    Announcement queryAnnouncementById(Integer newsId);
    Integer queryAnnouncementCount(Short newsType);
    List<Announcement> queryAnnouncementList(@Param("newsType") Short newsType,
                                             @Param("offset") int offset,
                                             @Param("limit") int limit);
    List<Announcement> queryContestAnnouncementList(Integer contestId);
    void insertAnnouncement(Announcement announcement);
    void updateAnnouncement(Announcement announcement);
}
