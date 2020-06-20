package cn.edu.hunnu.acm.service;

import cn.edu.hunnu.acm.model.Problem;

import java.util.Map;

public interface ProblemService {
    Problem queryProblemById(Integer problemId, Integer contestId);
    Problem checkProblemExist(Integer problemId, Integer contestId);
    Map<String, Object> queryProblemList(String keyword, int offset, int limit, String userId);
    Map<String, Object> queryContestProblemList(Integer contestId, String userId);
    void createProblem(Problem problem);
    void updateProblem(Problem problem);
}
