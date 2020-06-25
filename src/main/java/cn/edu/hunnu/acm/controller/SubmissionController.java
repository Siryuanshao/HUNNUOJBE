package cn.edu.hunnu.acm.controller;

import cn.edu.hunnu.acm.aspect.annotation.LoginRequired;
import cn.edu.hunnu.acm.framework.annotation.*;
import cn.edu.hunnu.acm.model.Contest;
import cn.edu.hunnu.acm.model.Problem;
import cn.edu.hunnu.acm.model.RunRequest;
import cn.edu.hunnu.acm.model.Submission;
import cn.edu.hunnu.acm.service.ContestService;
import cn.edu.hunnu.acm.service.ProblemService;
import cn.edu.hunnu.acm.service.SubmissionService;
import cn.edu.hunnu.acm.service.UDPServerNotify;
import cn.edu.hunnu.acm.util.Constants;
import cn.edu.hunnu.acm.util.DataMap;
import cn.edu.hunnu.acm.util.TextUtils;
import cn.edu.hunnu.acm.util.ValidateChecker;
import com.alibaba.fastjson.JSONArray;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SubmissionController {
    @Qualifier
    private SubmissionService submissionService;

    @Qualifier
    private ProblemService problemService;

    @Qualifier
    private ContestService contestService;

    @Qualifier
    private UDPServerNotify udpServerNotify;

    private boolean checkContestStart(String userId, Integer contestId) {
        Contest contest = contestService.queryContestById(contestId);
        return contest != null &&
               contest.getStartTime().compareTo(TextUtils.getFormatLocalDateTime()) <= 0 &&
               (contest.getType() != Constants.contestType.PRIVATE || JSONArray.parseArray(contest.getUserPrivilege()).contains(userId));
    }

    @GetMapping(value = "/submissionList", produces = "application/json;charset=utf-8")
    public DataMap getSubmissionList(Submission query,
                                     @RequestParam(value = "page", defaultValue = "1") Integer page) {
        if(query.getResult() != null && query.getResult() == -1) query.setResult(null);
        if(query.getLanguage() != null && query.getLanguage() == -1) query.setLanguage(null);
        query.setContestId(-1);

        int limit = Constants.pagination.submissionLimit;
        int offset = (page - 1) * limit;

        Map<String, Object> submissions = submissionService.querySubmissionList(query, offset, limit);
        Integer submissionCount = (Integer) submissions.get("submissionCount");
        List<Submission> submissionList = (List<Submission>) submissions.get("submissionList");

        DataMap dataMap = new DataMap();
        dataMap.set("submissionCount", submissionCount);

        if(submissionList != null) {
            List<Object> submissionArray = new ArrayList<>();
            for(Submission submission : submissionList) {
                Map<String, Object> sb = new HashMap<>();
                sb.put("submissionId", submission.getRunId());
                sb.put("problemId", submission.getProblemId());
                sb.put("when", submission.getSubmitTime());
                sb.put("status", submission.getResult());
                sb.put("timeUsed", submission.getTimeUsed());
                sb.put("memoryUsed", submission.getMemoryUsed());
                sb.put("language", submission.getLanguage());
                sb.put("length", submission.getLength());
                sb.put("userId", submission.getUserId());
                sb.put("permission", true);

                submissionArray.add(sb);
            }
            dataMap.set("submissionList", submissionArray);
        }
        return dataMap.success();
    }

    @LoginRequired
    @GetMapping(value = "/submissionDetail", produces = "application/json;charset=utf-8")
    public DataMap getSubmissionDetail(HttpSession session,
                                       @RequestParam("runId") Integer submissionId) {
        DataMap dataMap = new DataMap();
        String userId = (String) session.getAttribute("userId");
        String userType = (String) session.getAttribute("userType");

        boolean isAdminRole = Constants.userType.SuperAdmin.equals(userType) || Constants.userType.Admin.equals(userType);

        if(userId == null) {
            dataMap.setErrorInfo(Constants.errorMessage.invalid_session);
            return dataMap.fail();
        }

        Submission submission = submissionService.querySubmissionById(submissionId);
        if(submission != null) {
            if(isAdminRole || submission.getContestId() == -1 || userId.equals(submission.getUserId())) {
                dataMap.set("submissionId", submission.getRunId());
                dataMap.set("userId", submission.getUserId());
                dataMap.set("when", submission.getSubmitTime());
                dataMap.set("status", submission.getResult());

                dataMap.set("sourcecode", submission.getSourcecode());
                dataMap.set("length", submission.getLength());
                dataMap.set("language", submission.getLanguage());
                dataMap.set("timeUsed", submission.getTimeUsed());
                dataMap.set("memoryUsed", submission.getMemoryUsed());
                dataMap.set("ext", submission.getExt());

                return dataMap.success();
            } else {
                dataMap.setErrorInfo(Constants.errorMessage.have_not_permission);
                return dataMap.fail();
            }
        } else {
            dataMap.setErrorInfo(Constants.errorMessage.submission_not_found);
            return dataMap.fail();
        }
    }

    @LoginRequired
    @PostMapping(value = "/uploadSubmission", produces = "application/json;charset=utf-8")
    public DataMap uploadSubmission(HttpSession session, Submission submission) {
        DataMap dataMap = new DataMap();
        if(!ValidateChecker.checkSubmissionValidate(submission)) {
            dataMap.setErrorInfo(Constants.errorMessage.invalid_parameter);
            return dataMap.fail();
        }
        String userId = (String) session.getAttribute("userId");
        if(submission.getContestId() != -1 && !checkContestStart(userId, submission.getContestId())) {
            dataMap.setErrorInfo(Constants.errorMessage.contest_not_start);
            return dataMap.fail();
        }
        if(userId == null) {
            dataMap.setErrorInfo(Constants.errorMessage.invalid_session);
            return dataMap.fail();
        }

        Problem simpleProblem = null;

        // 这里还要确认一下这个
        simpleProblem = problemService.checkProblemExist(submission.getProblemId(), submission.getContestId());

        if(simpleProblem != null) {
            submission.setUserId(userId);
            submission.setResult(Constants.result.Pending);
            submission.setTimeUsed(-1);
            submission.setMemoryUsed(-1);
            submission.setLength(submission.getSourcecode().length());

            Integer submissionId = submissionService.createSubmission(submission);

            RunRequest runRequest = new RunRequest();
            runRequest.submissionId = submissionId;
            runRequest.problemId = submission.getProblemId();
            runRequest.contestId = submission.getContestId();
            runRequest.language = submission.getLanguage();
            runRequest.sourcecode = submission.getSourcecode();
            runRequest.timeLimit = simpleProblem.getTimeLimit();
            runRequest.memoryLimit = simpleProblem.getMemoryLimit();
            runRequest.isSpj = (simpleProblem.getSpj() ? 1 : 0);

            udpServerNotify.notifyServer(runRequest);

            return dataMap.success();
        } else {
            dataMap.setErrorInfo(Constants.errorMessage.problem_not_found);
            return dataMap.fail();
        }
    }
}
