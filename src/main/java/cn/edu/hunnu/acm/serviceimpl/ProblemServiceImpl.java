package cn.edu.hunnu.acm.serviceimpl;

import cn.edu.hunnu.acm.dao.ProblemMapper;
import cn.edu.hunnu.acm.dao.SubmissionMapper;
import cn.edu.hunnu.acm.framework.annotation.Service;
import cn.edu.hunnu.acm.model.Problem;
import cn.edu.hunnu.acm.service.ProblemService;
import cn.edu.hunnu.acm.util.AlgorithmUtils;
import cn.edu.hunnu.acm.util.Constants;
import cn.edu.hunnu.acm.util.GetSqlSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("problemService")
public class ProblemServiceImpl implements ProblemService {
    @Override
    public Problem queryProblemById(Integer problemId, Integer contestId) {
        ProblemMapper problemMapper = GetSqlSession.getSqlSession().getMapper(ProblemMapper.class);
        Problem problem = null;
        try {
            problem = problemMapper.queryProblemById(problemId, contestId);
            GetSqlSession.commit();
        } catch (Exception e) {
            GetSqlSession.rollback();
            throw new RuntimeException(e);
        } finally {
            GetSqlSession.close();
        }
        return problem;
    }

    @Override
    public Problem checkProblemExist(Integer problemId, Integer contestId) {
        ProblemMapper problemMapper = GetSqlSession.getSqlSession().getMapper(ProblemMapper.class);
        Problem exist = null;
        try {
            exist = problemMapper.checkProblemExist(problemId, contestId);
            GetSqlSession.commit();
        } catch (Exception e) {
            GetSqlSession.rollback();
            throw new RuntimeException(e);
        } finally {
            GetSqlSession.close();
        }
        return exist;
    }

    @Override
    public Map<String, Object> queryProblemList(String keyword, int offset, int limit, String userId) {
        ProblemMapper problemMapper = GetSqlSession.getSqlSession().getMapper(ProblemMapper.class);
        SubmissionMapper submissionMapper = GetSqlSession.getSqlSession().getMapper(SubmissionMapper.class);
        Map<String, Object> map = new HashMap<>();
        try {
            int problemCount = 0;
            List<Problem> problemList = null;
            List<Short> statusList = null;
            problemCount = problemMapper.queryProblemCount(keyword);
            problemList = problemMapper.queryProblemList(keyword, offset, limit);
            if(userId != null && problemList != null) {
                List<Integer> solveList = submissionMapper.queryUserSolvedList(userId);
                List<Integer> totalList = submissionMapper.queryUserTotalList(userId);
                List<Integer> attemptList = AlgorithmUtils.calculateDiff(solveList, totalList);

                statusList = new ArrayList<>();

                for(Problem problem : problemList) {
                    if(solveList.contains(problem.getProblemId())) {
                        statusList.add(Constants.myStatus.success);
                    } else if(attemptList.contains(problem.getProblemId())) {
                        statusList.add(Constants.myStatus.failed);
                    } else {
                        statusList.add(Constants.myStatus.notTry);
                    }
                }
            }
            GetSqlSession.commit();
            map.put("problemCount", problemCount);
            map.put("problemList", problemList);
            map.put("statusList", statusList);
            GetSqlSession.commit();
        } catch (Exception e) {
            GetSqlSession.rollback();
            throw new RuntimeException(e);
        } finally {
            GetSqlSession.close();
        }
        return map;
    }

    @Override
    public Map<String, Object> queryContestProblemList(Integer contestId, String userId) {
        ProblemMapper problemMapper = GetSqlSession.getSqlSession().getMapper(ProblemMapper.class);
        SubmissionMapper submissionMapper = GetSqlSession.getSqlSession().getMapper(SubmissionMapper.class);
        Map<String, Object> map = new HashMap<>();
        try {
            List<Problem> problemList = null;
            List<Short> statusList = null;
            problemList = problemMapper.queryContestProblemList(contestId);
            if(userId != null && problemList != null) {
                List<Integer> solveList = submissionMapper.queryUserSolvedList(userId);
                List<Integer> totalList = submissionMapper.queryUserTotalList(userId);
                List<Integer> attemptList = AlgorithmUtils.calculateDiff(solveList, totalList);

                statusList = new ArrayList<>();

                for(Problem problem : problemList) {
                    if(solveList.contains(problem.getProblemId())) {
                        statusList.add(Constants.myStatus.success);
                    } else if(attemptList.contains(problem.getProblemId())) {
                        statusList.add(Constants.myStatus.failed);
                    } else {
                        statusList.add(Constants.myStatus.notTry);
                    }
                }
            }
            GetSqlSession.commit();
            map.put("problemList", problemList);
            map.put("statusList", statusList);
        } catch (Exception e) {
            GetSqlSession.rollback();
            throw new RuntimeException(e);
        } finally {
            GetSqlSession.close();
        }
        return map;
    }

    @Override
    public void createProblem(Problem problem) {
        ProblemMapper problemMapper = GetSqlSession.getSqlSession().getMapper(ProblemMapper.class);
        try {
            problemMapper.insertProblem(problem);
            GetSqlSession.commit();
        } catch (Exception e) {
            GetSqlSession.rollback();
            throw new RuntimeException(e);
        } finally {
            GetSqlSession.close();
        }
    }

    @Override
    public void updateProblem(Problem problem) {
        ProblemMapper problemMapper = GetSqlSession.getSqlSession().getMapper(ProblemMapper.class);
        try {
            problemMapper.updateProblem(problem);
            GetSqlSession.commit();
        } catch (Exception e) {
            GetSqlSession.rollback();
            throw new RuntimeException(e);
        } finally {
            GetSqlSession.close();
        }
    }
}
