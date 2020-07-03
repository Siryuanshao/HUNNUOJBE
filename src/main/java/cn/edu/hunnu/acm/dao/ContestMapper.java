package cn.edu.hunnu.acm.dao;

import cn.edu.hunnu.acm.model.Contest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ContestMapper {
    Contest queryContestById(Integer contestId);
    Integer queryContestCount(@Param("keyword") String keyword);
    List<Contest> queryContestList(@Param("keyword") String keyword,
                                   @Param("offset") int offset,
                                   @Param("limit") int limit);
    void insertContest(Contest contest);
    void updateContest(Contest contest);
}
