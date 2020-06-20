package cn.edu.hunnu.acm.service;

import cn.edu.hunnu.acm.model.Contest;

import java.util.Map;

public interface ContestService {
    Contest queryContestById(Integer contestId);
    Map<String, Object> queryContestList(String keyword, int offset, int limit);
    Map<String, Object> queryContestRankList(Integer contestId);
    void createContest(Contest contest);
    void updateContest(Contest contest);
}
