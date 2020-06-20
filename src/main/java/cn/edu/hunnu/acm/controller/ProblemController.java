package cn.edu.hunnu.acm.controller;

import cn.edu.hunnu.acm.framework.annotation.Controller;
import cn.edu.hunnu.acm.framework.annotation.GetMapping;
import cn.edu.hunnu.acm.framework.annotation.Qualifier;
import cn.edu.hunnu.acm.framework.annotation.RequestParam;
import cn.edu.hunnu.acm.model.Problem;
import cn.edu.hunnu.acm.model.Submission;
import cn.edu.hunnu.acm.service.ProblemService;
import cn.edu.hunnu.acm.service.SubmissionService;
import cn.edu.hunnu.acm.util.Constants;
import cn.edu.hunnu.acm.util.DataMap;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ProblemController {
    @Qualifier
    private ProblemService problemService;

    @Qualifier
    private SubmissionService submissionService;

    @GetMapping(value = "/problemList", produces = "application/json;charset=utf-8")
    public DataMap getProblemList(HttpSession session,
                                  @RequestParam(value = "description", required = false) String keyword,
                                  @RequestParam(value = "page", defaultValue = "1") Integer page) {
        int limit = Constants.pagination.problemLimit;
        int offset = (page - 1) * limit;
        String userId = (String) session.getAttribute("userId");

        Map<String, Object> problems = problemService.queryProblemList(keyword, offset, limit, userId);
        Integer problemCount = (Integer) problems.get("problemCount");
        List<Problem> problemList = (List<Problem>) problems.get("problemList");
        List<Short> statusList = (List<Short>) problems.get("statusList");

        DataMap dataMap = new DataMap();
        dataMap.set("problemCount", problemCount);
        if(problemList != null) {
            List<Object> problemArray = new ArrayList<>();

            for(Problem problem : problemList) {
                Map<String, Object> pb = new HashMap<>();
                pb.put("problemId", problem.getProblemId());
                if(statusList != null) {
                    pb.put("status", statusList.get(problemArray.size()));
                }
                pb.put("title", problem.getTitle());
                pb.put("accept", problem.getAccept());
                pb.put("submit", problem.getSubmit());
                pb.put("source", problem.getSource());

                problemArray.add(pb);
            }
            dataMap.set("problemList", problemArray);
        }
        return dataMap.success();
    }

    @GetMapping(value = "/problemDetail", produces = "application/json;charset=utf-8")
    public DataMap getProblemDetail(@RequestParam("problemId") Integer problemId) {
        Problem problem = problemService.queryProblemById(problemId, -1);
        DataMap dataMap = new DataMap();

        if(problem != null) {
            dataMap.set("problemId", problem.getProblemId());
            dataMap.set("contestId", problem.getContestId());
            dataMap.set("title", problem.getTitle());
            dataMap.set("description", problem.getDescription());
            dataMap.set("inputDesc", problem.getInputDesc());
            dataMap.set("outputDesc", problem.getOutputDesc());
            dataMap.set("timeLimit", problem.getTimeLimit());
            dataMap.set("memoryLimit", problem.getMemoryLimit());
            dataMap.set("hint", problem.getHint());
            dataMap.set("source", problem.getSource());
            dataMap.set("accept", problem.getAccept());
            dataMap.set("submit", problem.getSubmit());
            dataMap.set("inputSample", problem.getInputSample());
            dataMap.set("outputSample", problem.getOutputSample());
            dataMap.set("isSpj", problem.getSpj());

            return dataMap.success();
        }else{
            dataMap.setErrorInfo(Constants.errorMessage.problem_not_found);
            return dataMap.fail();
        }
    }

    @GetMapping(value = "/problemSummary", produces = "application/json;charset=utf-8")
    public DataMap getProblemSummary(@RequestParam("problemId") Integer problemId,
                                     @RequestParam(value = "language", required = false) Short language,
                                     @RequestParam(value = "page", defaultValue = "1") Integer page) {
        if(language != null && language == -1) language = null;
        int limit = Constants.pagination.summaryList;
        int offset = (page - 1) * limit;
        DataMap dataMap = new DataMap();

        Map<String, Object> summary = submissionService.queryProblemSummary(problemId, language, offset, limit);

        Integer totalSubmission = (Integer) summary.get("totalSubmission");
        Integer userSubmit = (Integer) summary.get("userSubmit");
        Integer userAccept = (Integer) summary.get("userAccept");
        Integer submissionCount = (Integer) summary.get("submissionCount");

        List<Integer> statistic = (List<Integer>) summary.get("statistic");
        List<Submission> submissionList = (List<Submission>) summary.get("submissionList");

        dataMap.set("totalSubmission", totalSubmission);
        dataMap.set("submissionCount", submissionCount);
        dataMap.set("userSubmit", userSubmit);
        dataMap.set("userAccept", userAccept);
        dataMap.set("statistic", statistic);

        if(submissionList != null) {
            int ranked = offset;
            List<Object> submissionArray = new ArrayList<>();
            for(Submission submission : submissionList) {
                Map<String, Object> sb = new HashMap<>();
                sb.put("rank", ++ranked);
                sb.put("submissionId", submission.getRunId());
                sb.put("userId", submission.getUserId());
                sb.put("timeUsed", submission.getTimeUsed());
                sb.put("memoryUsed", submission.getMemoryUsed());
                sb.put("language", submission.getLanguage());
                sb.put("length", submission.getLength());
                sb.put("when", submission.getSubmitTime());

                submissionArray.add(sb);
            }
            dataMap.set("summaryList", submissionArray);
        }

        return dataMap.success();
    }
}
