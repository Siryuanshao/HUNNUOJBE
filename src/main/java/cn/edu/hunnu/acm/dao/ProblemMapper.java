package cn.edu.hunnu.acm.dao;

import cn.edu.hunnu.acm.model.Problem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProblemMapper {
    Problem queryProblemById(@Param("problemId") Integer problemId,
                             @Param("contestId") Integer contestId);
    Problem checkProblemExist(@Param("problemId") Integer problemId,
                              @Param("contestId") Integer contestId);
    Integer queryProblemCount(@Param("keyword") String keyword);
    List<Problem> queryProblemList(@Param("keyword") String keyword,
                                   @Param("offset") int offset,
                                   @Param("limit") int limit);
    List<Problem> queryContestProblemList(Integer contestId);
    void insertProblem(Problem problem);
    void updateProblem(Problem problem);
    void updateProblemSubmit(@Param("problemId") Integer problemId,
                             @Param("contestId") Integer contestId,
                             @Param("accept") Integer accept,
                             @Param("submit") Integer submit);
}
