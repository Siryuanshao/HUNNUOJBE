package cn.edu.hunnu.acm.serviceimpl;

import cn.edu.hunnu.acm.dao.AnnouncementMapper;
import cn.edu.hunnu.acm.framework.annotation.Service;
import cn.edu.hunnu.acm.model.Announcement;
import cn.edu.hunnu.acm.service.AnnouncementService;
import cn.edu.hunnu.acm.util.Constants;
import cn.edu.hunnu.acm.util.GetSqlSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("announcementService")
public class AnnouncementServiceImpl implements AnnouncementService {
    public Map<String, Object> getHomePage() {
        AnnouncementMapper announcementMapper = GetSqlSession.getSqlSession().getMapper(AnnouncementMapper.class);
        Map<String, Object> map = new HashMap<>();
        try {
            List<String> carouselList = announcementMapper.queryCarouselList();
            List<Announcement> reportList = announcementMapper.queryHotspotList(Constants.announcementType.ReportType);
            List<Announcement> solutionList = announcementMapper.queryHotspotList(Constants.announcementType.SolutionType);
            GetSqlSession.commit();
            map.put("carouselList", carouselList);
            map.put("reportList", reportList);
            map.put("solutionList", solutionList);
        } catch (Exception e){
            GetSqlSession.rollback();
            throw new RuntimeException(e);
        } finally {
            GetSqlSession.close();
        }
        return map;
    }

    public Announcement queryAnnouncementById(Integer newsId) {
        AnnouncementMapper announcementMapper = GetSqlSession.getSqlSession().getMapper(AnnouncementMapper.class);
        Announcement announcement = null;
        try {
            announcement = announcementMapper.queryAnnouncementById(newsId);
            GetSqlSession.commit();
        } catch (Exception e) {
            GetSqlSession.rollback();
            throw new RuntimeException(e);
        } finally {
            GetSqlSession.close();
        }
        return announcement;
    }

    public Map<String, Object> queryAnnouncementList(Short newsType, int offset, int limit) {
        AnnouncementMapper announcementMapper = GetSqlSession.getSqlSession().getMapper(AnnouncementMapper.class);
        Map<String, Object> map = new HashMap<>();
        try {
            int announcementCount = 0;
            List<Announcement> announcementList = null;
            announcementCount = announcementMapper.queryAnnouncementCount(newsType);
            announcementList = announcementMapper.queryAnnouncementList(newsType, offset, limit);
            GetSqlSession.commit();
            map.put("announcementCount", announcementCount);
            map.put("announcementList", announcementList);
        } catch (Exception e) {
            GetSqlSession.rollback();
            throw new RuntimeException(e);
        } finally {
            GetSqlSession.close();
        }
        return map;
    }

    public List<Announcement> queryContestAnnouncementList(Integer contestId) {
        AnnouncementMapper announcementMapper = GetSqlSession.getSqlSession().getMapper(AnnouncementMapper.class);
        List<Announcement> contestAnnouncement = null;
        try {
            contestAnnouncement = announcementMapper.queryContestAnnouncementList(contestId);
            GetSqlSession.commit();
        } catch (Exception e) {
            GetSqlSession.rollback();
            throw new RuntimeException(e);
        } finally {
            GetSqlSession.close();
        }
        return contestAnnouncement;
    }

    public void createAnnouncement(Announcement announcement) {
        AnnouncementMapper announcementMapper = GetSqlSession.getSqlSession().getMapper(AnnouncementMapper.class);
        try {
            announcementMapper.insertAnnouncement(announcement);
            GetSqlSession.commit();
        } catch (Exception e) {
            GetSqlSession.rollback();
            throw new RuntimeException(e);
        } finally {
            GetSqlSession.close();
        }
    }

    public void updateAnnouncement(Announcement announcement) {
        AnnouncementMapper announcementMapper = GetSqlSession.getSqlSession().getMapper(AnnouncementMapper.class);
        try{
            announcementMapper.updateAnnouncement(announcement);
            GetSqlSession.commit();
        } catch (Exception e) {
            GetSqlSession.rollback();
            throw new RuntimeException(e);
        } finally {
            GetSqlSession.close();
        }
    }
}
