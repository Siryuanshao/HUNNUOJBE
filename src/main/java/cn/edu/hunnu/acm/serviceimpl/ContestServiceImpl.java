package cn.edu.hunnu.acm.serviceimpl;

import cn.edu.hunnu.acm.dao.ContestMapper;
import cn.edu.hunnu.acm.dao.ProblemMapper;
import cn.edu.hunnu.acm.dao.SubmissionMapper;
import cn.edu.hunnu.acm.framework.annotation.Service;
import cn.edu.hunnu.acm.model.Contest;
import cn.edu.hunnu.acm.model.Problem;
import cn.edu.hunnu.acm.model.Ranking;
import cn.edu.hunnu.acm.model.Submission;
import cn.edu.hunnu.acm.service.ContestService;
import cn.edu.hunnu.acm.util.AlgorithmUtils;
import cn.edu.hunnu.acm.util.GetSqlSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("contestService")
public class ContestServiceImpl implements ContestService {
    @Override
    public Contest queryContestById(Integer contestId) {
        ContestMapper contestMapper = GetSqlSession.getSqlSession().getMapper(ContestMapper.class);
        Contest contest = null;
        try {
            contest = contestMapper.queryContestById(contestId);
            GetSqlSession.commit();
        } catch (Exception e) {
            GetSqlSession.rollback();
            throw new RuntimeException(e);
        } finally {
            GetSqlSession.close();
        }
        return contest;
    }

    @Override
    public Map<String, Object> queryContestList(String keyword, int offset, int limit) {
        ContestMapper contestMapper = GetSqlSession.getSqlSession().getMapper(ContestMapper.class);
        Map<String, Object> map = new HashMap<>();
        try {
            int contestCount = 0;
            List<Contest> contestList = null;
            contestCount = contestMapper.queryContestCount(keyword);
            contestList = contestMapper.queryContestList(keyword, offset, limit);
            GetSqlSession.commit();
            map.put("contestCount", contestCount);
            map.put("contestList", contestList);
        } catch (Exception e) {
            GetSqlSession.rollback();
            throw new RuntimeException(e);
        } finally {
            GetSqlSession.close();
        }
        return map;
    }

    @Override
    public Map<String, Object> queryContestRankList(Integer contestId) {
        Map<String, Object> map = new HashMap<>();
        ContestMapper contestMapper = GetSqlSession.getSqlSession().getMapper(ContestMapper.class);
        ProblemMapper problemMapper = GetSqlSession.getSqlSession().getMapper(ProblemMapper.class);
        SubmissionMapper submissionMapper = GetSqlSession.getSqlSession().getMapper(SubmissionMapper.class);
        try {
            Contest contest = contestMapper.queryContestById(contestId);
            List<Problem> problemList = problemMapper.queryContestProblemList(contestId);
            List<Submission> submissionList = submissionMapper.queryContestRealTimeTotalList(contestId, contest.getEndTime());
            List<Ranking> rankingList = AlgorithmUtils.calculateRankings(contest, problemList, submissionList);
            GetSqlSession.commit();
            map.put("contestProblemList", problemList);
            map.put("rankingList", rankingList);
        } catch (Exception e) {
            GetSqlSession.rollback();
            throw new RuntimeException(e);
        } finally {
            GetSqlSession.close();
        }
        return map;
    }

    @Override
    public void createContest(Contest contest) {
        ContestMapper contestMapper = GetSqlSession.getSqlSession().getMapper(ContestMapper.class);
        try{
            contestMapper.insertContest(contest);
            GetSqlSession.commit();
        } catch (Exception e) {
            GetSqlSession.rollback();
            throw new RuntimeException(e);
        } finally {
            GetSqlSession.close();
        }
    }

    @Override
    public void updateContest(Contest contest) {
        ContestMapper contestMapper = GetSqlSession.getSqlSession().getMapper(ContestMapper.class);
        try {
            contestMapper.updateContest(contest);
            GetSqlSession.commit();
        } catch (Exception e) {
            GetSqlSession.rollback();
            throw new RuntimeException(e);
        } finally {
            GetSqlSession.close();
        }
    }
}
