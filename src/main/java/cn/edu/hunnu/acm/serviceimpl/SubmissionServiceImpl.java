package cn.edu.hunnu.acm.serviceimpl;

import cn.edu.hunnu.acm.dao.ProblemMapper;
import cn.edu.hunnu.acm.dao.SubmissionMapper;
import cn.edu.hunnu.acm.dao.UserMapper;
import cn.edu.hunnu.acm.framework.annotation.Service;
import cn.edu.hunnu.acm.model.Submission;
import cn.edu.hunnu.acm.service.SubmissionService;
import cn.edu.hunnu.acm.util.Constants;
import cn.edu.hunnu.acm.util.GetSqlSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("submissionService")
public class SubmissionServiceImpl implements SubmissionService {
    @Override
    public Submission querySubmissionById(Integer submissionId) {
        SubmissionMapper submissionMapper = GetSqlSession.getSqlSession().getMapper(SubmissionMapper.class);
        Submission submission = null;
        try {
            submission = submissionMapper.querySubmissionById(submissionId);
            GetSqlSession.commit();
        } catch (Exception e) {
            GetSqlSession.rollback();
            throw new RuntimeException(e);
        } finally {
            GetSqlSession.close();
        }
        return submission;
    }

    @Override
    public Map<String, Object> querySubmissionList(Submission submission, int offset, int limit) {
        SubmissionMapper submissionMapper = GetSqlSession.getSqlSession().getMapper(SubmissionMapper.class);
        Map<String, Object> map = new HashMap<>();
        try {
            Integer submissionCount = submissionMapper.querySubmissionCount(submission);
            List<Submission> submissionList = submissionMapper.querySubmissionList(submission, offset, limit);
            map.put("submissionCount", submissionCount);
            map.put("submissionList", submissionList);
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
    public Map<String, Object> queryProblemSummary(Integer problemId, Short language, int offset, int limit) {
        SubmissionMapper submissionMapper = GetSqlSession.getSqlSession().getMapper(SubmissionMapper.class);
        Map<String, Object> map = new HashMap<>();
        try {
            Submission submission = new Submission();
            submission.setProblemId(problemId);
            submission.setContestId(-1);
            submission.setLanguage(language);

            Integer totalSubmission = submissionMapper.querySubmissionCount(submission);
            Integer userAccept = submissionMapper.queryProblemUserAccept(submission);
            Integer userSubmit = submissionMapper.queryProblemUserSubmit(submission);

            List<Integer> statistic = new ArrayList<>();

            for(short i = Constants.result_range.LRange; i <= Constants.result_range.RRange; i++) {
                submission.setResult(i);
                statistic.add(submissionMapper.querySubmissionCount(submission));
            }
            submission.setResult(Constants.result.Accepted);

            Integer submissionCount = submissionMapper.queryProblemUserSolveByLang(submission);
            List<Submission> submissionList = submissionMapper.queryProblemTopRank(problemId, language, offset, limit);

            GetSqlSession.commit();

            map.put("totalSubmission", totalSubmission);
            map.put("submissionCount", submissionCount);
            map.put("userSubmit", userSubmit);
            map.put("userAccept", userAccept);
            map.put("statistic", statistic);
            map.put("submissionList", submissionList);

        } catch (Exception e) {
            GetSqlSession.rollback();
            throw new RuntimeException(e);
        } finally {
            GetSqlSession.close();
        }
        return map;
    }

    @Override
    public Integer createSubmission(Submission submission) {
        SubmissionMapper submissionMapper = GetSqlSession.getSqlSession().getMapper(SubmissionMapper.class);
        UserMapper userMapper = GetSqlSession.getSqlSession().getMapper(UserMapper.class);
        ProblemMapper problemMapper = GetSqlSession.getSqlSession().getMapper(ProblemMapper.class);
        int submissionId = -1;
        try {
            submissionMapper.insertSubmission(submission);
            submissionId = submission.getRunId();
            if(submission.getContestId() == -1) userMapper.updateUserSubmit(submission.getUserId(), 0, 1);
            problemMapper.updateProblemSubmit(submission.getProblemId(), submission.getContestId(), 0, 1);
            GetSqlSession.commit();
        } catch (Exception e) {
            GetSqlSession.rollback();
            throw new RuntimeException(e);
        } finally {
            GetSqlSession.close();
        }
        return submissionId;
    }

    @Override
    public void updateSubmission(Submission submission) {
        SubmissionMapper submissionMapper = GetSqlSession.getSqlSession().getMapper(SubmissionMapper.class);
        UserMapper userMapper = GetSqlSession.getSqlSession().getMapper(UserMapper.class);
        ProblemMapper problemMapper = GetSqlSession.getSqlSession().getMapper(ProblemMapper.class);
        try {
            submissionMapper.updateSubmission(submission);
            submission = submissionMapper.querySubmissionById(submission.getRunId());
            if(submission != null) {
                if(submission.getContestId() == -1) userMapper.updateUserSubmit(submission.getUserId(), 1, 0);
                problemMapper.updateProblemSubmit(submission.getProblemId(), submission.getContestId(), 1, 0);
            }
            GetSqlSession.commit();
        } catch (Exception e) {
            GetSqlSession.rollback();
            throw new RuntimeException(e);
        } finally {
            GetSqlSession.close();
        }
    }
}
