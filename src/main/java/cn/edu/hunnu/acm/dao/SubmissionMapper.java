package cn.edu.hunnu.acm.dao;

import cn.edu.hunnu.acm.model.Submission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SubmissionMapper {
    Submission querySubmissionById(Integer runId);
    Integer querySubmissionCount(Submission sb);
    Integer queryProblemUserSubmit(Submission sb);
    Integer queryProblemUserAccept(Submission sb);
    Integer queryProblemUserSolveByLang(Submission sb);
    List<Submission> querySubmissionList(@Param("sb") Submission sb,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);
    List<Submission> queryProblemTopRank(@Param("problemId") Integer problemId,
                                         @Param("language") Short language,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);
    List<Integer> queryUserSolvedList(String userId);
    List<Integer> queryUserTotalList(String userId);
    List<Submission> queryContestRealTimeTotalList(@Param("contestId") Integer contestId,
                                                   @Param("endTime") String endTime);
    void insertSubmission(Submission submission);
    void updateSubmission(Submission submission);
}
