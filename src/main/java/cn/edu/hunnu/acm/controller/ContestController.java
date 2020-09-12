package cn.edu.hunnu.acm.controller;

import cn.edu.hunnu.acm.framework.annotation.Controller;
import cn.edu.hunnu.acm.framework.annotation.GetMapping;
import cn.edu.hunnu.acm.framework.annotation.Qualifier;
import cn.edu.hunnu.acm.framework.annotation.RequestParam;
import cn.edu.hunnu.acm.model.*;
import cn.edu.hunnu.acm.service.AnnouncementService;
import cn.edu.hunnu.acm.service.ContestService;
import cn.edu.hunnu.acm.service.ProblemService;
import cn.edu.hunnu.acm.service.SubmissionService;
import cn.edu.hunnu.acm.util.Constants;
import cn.edu.hunnu.acm.util.DataMap;
import cn.edu.hunnu.acm.util.TextUtils;
import com.alibaba.fastjson.JSONArray;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ContestController {
    @Qualifier
    private ContestService contestService;

    @Qualifier
    private ProblemService problemService;

    @Qualifier
    private SubmissionService submissionService;

    @Qualifier
    private AnnouncementService announcementService;


    private String checkContestViewPermission(String userId, String userType, Integer contestId) {
        // 如果是管理员用户, 则无论什么时候都拥有查看权限
        boolean isAdmin = Constants.userType.SuperAdmin.equals(userType) || Constants.userType.Admin.equals(userType);

        Contest contest = contestService.queryContestById(contestId);

        if(contest == null) return Constants.errorMessage.contest_not_found;
        else if(isAdmin) return null;
        else if(contest.getStartTime().compareTo(TextUtils.getFormatLocalDateTime()) > 0) return Constants.errorMessage.contest_not_start;
        else if(contest.getType().equals(Constants.contestType.PRIVATE) && userId != null && !JSONArray.parseArray(contest.getUserPrivilege()).contains(userId)) return Constants.errorMessage.have_not_permission;
        else return null;
    }

    @GetMapping(value = "/contestList", produces = "application/json;charset=utf-8")
    public DataMap getContestList(@RequestParam(value = "description", required = false) String keyword,
                                  @RequestParam(value = "page", defaultValue = "1") Integer page) {
        int limit = Constants.pagination.contestLimit;
        int offset = (page - 1) * limit;

        DataMap dataMap = new DataMap();

        Map<String, Object> contests = contestService.queryContestList(keyword, offset, limit);
        Integer contestCount = (Integer) contests.get("contestCount");
        List<Contest> contestList = (List<Contest>) contests.get("contestList");

        dataMap.set("contestCount", contestCount);

        if(contestList != null) {
            List<Object> contestArray = new ArrayList<>();

            String now = TextUtils.getFormatLocalDateTime();

            for(Contest contest : contestList) {
                Map<String, Object> ct = new HashMap<>();
                ct.put("contestId", contest.getContestId());
                ct.put("title", contest.getTitle());
                ct.put("startTime", contest.getStartTime().substring(0, 19));
                ct.put("endTime", contest.getEndTime().substring(0, 19));
                ct.put("type", contest.getType());

                if(now.compareTo(contest.getStartTime()) < 0) {
                    ct.put("status", Constants.contestStatus.Pending);
                } else if(now.compareTo(contest.getEndTime()) > 0) {
                    ct.put("status", Constants.contestStatus.Ending);
                } else {
                    ct.put("status", Constants.contestStatus.Running);
                }
                contestArray.add(ct);
            }
            dataMap.set("contestList", contestArray);
        }
        return dataMap.success();
    }

    @GetMapping(value = "/contestDetail", produces = "application/json;charset=utf-8")
    public DataMap getContestDetail(@RequestParam("contestId") Integer contestId) {
        DataMap dataMap = new DataMap();
        if(contestId <= 0) {
            dataMap.setErrorInfo(Constants.errorMessage.contest_not_found);
            return dataMap.fail();
        }
        Contest contest = contestService.queryContestById(contestId);
        if(contest != null) {
            dataMap.set("contestId", contest.getContestId());
            dataMap.set("title", contest.getTitle());
            dataMap.set("type", contest.getType());
            dataMap.set("startTime", contest.getStartTime().substring(0, 19));
            dataMap.set("endTime", contest.getEndTime().substring(0, 19));
            dataMap.set("ext", contest.getExt());
            dataMap.set("userPrivilege", contest.getUserPrivilege());
            return dataMap.success();
        } else {
            dataMap.setErrorInfo(Constants.errorMessage.contest_not_found);
            return dataMap.fail();
        }
    }

    @GetMapping(value = "/contest/problem", produces = "application/json;charset=utf-8")
    public DataMap getContestProblem(HttpSession session,
                                     @RequestParam("contestId") Integer contestId) {
        DataMap dataMap = new DataMap();
        if(contestId <= 0) {
            dataMap.setErrorInfo(Constants.errorMessage.contest_not_found);
            return dataMap.fail();
        }
        String userId = (String) session.getAttribute("userId");
        String userType = (String) session.getAttribute("userType");
        String errorMessage = checkContestViewPermission(userId, userType, contestId);
        if(errorMessage != null) {
            dataMap.setErrorInfo(errorMessage);
            return dataMap.fail();
        }
        Map<String, Object> problems = problemService.queryContestProblemList(contestId, userId);
        List<Problem> problemList = (List<Problem>) problems.get("problemList");
        List<Short> statusList = (List<Short>) problems.get("statusList");

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
                problemArray.add(pb);
            }
            dataMap.set("problemList", problemArray);
        }
        return dataMap.success();
    }

    @GetMapping(value = "/contest/problemDetail", produces = "application/json;charset=utf-8")
    public DataMap getContestProblemDetail(HttpSession session,
                                           @RequestParam("problemId") Integer problemId,
                                           @RequestParam("contestId") Integer contestId) {
        DataMap dataMap = new DataMap();
        if(contestId <= 0) {
            dataMap.setErrorInfo(Constants.errorMessage.contest_not_found);
            return dataMap.fail();
        }
        String userId = (String) session.getAttribute("userId");
        String userType = (String) session.getAttribute("userType");
        String errorMessage = checkContestViewPermission(userId, userType, contestId);
        if(errorMessage != null) {
            dataMap.setErrorInfo(errorMessage);
            return dataMap.fail();
        }
        Problem problem = problemService.queryProblemById(problemId, contestId);

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
        } else {
            dataMap.setErrorInfo(Constants.errorMessage.problem_not_found);
            return dataMap.fail();
        }
    }

    @GetMapping(value = "/contest/announcement", produces = "application/json;charset=utf-8")
    public DataMap getContestAnnouncement(@RequestParam("contestId") Integer contestId) {
        DataMap dataMap = new DataMap();

        if(contestId <= 0) {
            dataMap.setErrorInfo(Constants.errorMessage.contest_not_found);
            return dataMap.fail();
        }

        List<Announcement> announcements = announcementService.queryContestAnnouncementList(contestId);

        if(announcements != null) {
            List<Object> announceArray = new ArrayList<>();
            for(Announcement announcement : announcements) {
                Map<String, Object> ca = new HashMap<>();
                ca.put("newsId", announcement.getNewsId());
                ca.put("title", announcement.getTitle());
                ca.put("creator", announcement.getCreator());
                ca.put("createTime", announcement.getCreateTime().substring(0, 19));
                ca.put("updateTime",announcement.getUpdateTime().substring(0, 19));
                ca.put("content", announcement.getContent());
                announceArray.add(ca);
            }
            dataMap.set("announcementList", announceArray);
        }
        return dataMap.success();
    }

    @GetMapping(value = "/contest/submission", produces = "application/json;charset=utf-8")
    public DataMap getContestSubmissionList(HttpSession session,
                                            Submission query,
                                            @RequestParam(value = "page", defaultValue = "1") Integer page) {
        if(query.getResult() != null && query.getResult() == -1) query.setResult(null);
        if(query.getLanguage() != null && query.getLanguage() == -1) query.setLanguage(null);

        DataMap dataMap = new DataMap();

        if(query.getContestId() <= 0) {
            dataMap.setErrorInfo(Constants.errorMessage.contest_not_found);
            return dataMap.fail();
        }
        int limit = Constants.pagination.submissionLimit;
        int offset = (page - 1) * limit;

        String userId = (String) session.getAttribute("userId");
        String userType = (String) session.getAttribute("userType");

        boolean adminRole = Constants.userType.SuperAdmin.equals(userType) || Constants.userType.Admin.equals(userType);

        Map<String, Object> submissions = submissionService.querySubmissionList(query, offset, limit);
        Integer submissionCount = (Integer) submissions.get("submissionCount");
        List<Submission> submissionList = (List<Submission>) submissions.get("submissionList");
        dataMap.set("submissionCount", submissionCount);

        if(submissionList != null) {
            List<Object> submissionArray = new ArrayList<>();
            for(Submission submission : submissionList) {
                Map<String, Object> sb = new HashMap<>();
                sb.put("submissionId", submission.getRunId());
                sb.put("problemId", submission.getProblemId());
                sb.put("when", submission.getSubmitTime().substring(0, 19));
                sb.put("status", submission.getResult());
                sb.put("timeUsed", submission.getTimeUsed());
                sb.put("memoryUsed", submission.getMemoryUsed());
                sb.put("language", submission.getLanguage());
                sb.put("length", submission.getLength());
                sb.put("userId", submission.getUserId());

                if(adminRole || submission.getUserId().equals(userId)) {
                    sb.put("permission", true);
                } else {
                    sb.put("permission", false);
                }

                submissionArray.add(sb);
            }
            dataMap.set("submissionList", submissionArray);
        }
        return dataMap.success();
    }

    @GetMapping(value = "/contest/rankList", produces = "application/json;charset=utf-8")
    public DataMap getContestRankList(@RequestParam("contestId") Integer contestId) {
        DataMap dataMap = new DataMap();

        if(contestId <= 0) {
            dataMap.setErrorInfo(Constants.errorMessage.contest_not_found);
            return dataMap.fail();
        }

        Map<String, Object> rankings = contestService.queryContestRankList(contestId);
        List<Problem> contestProblemList = (List<Problem>) rankings.get("contestProblemList");
        List<Ranking> rankingList = (List<Ranking>) rankings.get("rankingList");

        if(contestProblemList != null) {
            List<Integer> problemSet = new ArrayList<>();
            for(Problem problem : contestProblemList) {
                problemSet.add(problem.getProblemId());
            }
            dataMap.set("problemSet", problemSet);
        }

        if(rankingList != null) {
            List<Object> rankList = new ArrayList<>();
            for(Ranking ranking : rankingList) {
                Map<String, Object> rk = new HashMap<>();
                rk.put("userId", ranking.userId);
                rk.put("rank", ranking.rank);
                rk.put("accept", ranking.accept);
                rk.put("penalty", ranking.penalty);
                List<Object> progressArray = new ArrayList<>();
                for(Ranking.Info progress : ranking.progress) {
                    Map<String, Object> ps = new HashMap<>();
                    ps.put("rankStatus", progress.status);
                    ps.put("tryTime", progress.tryTime);
                    ps.put("acceptTime", progress.acceptTime);
                    progressArray.add(ps);
                }
                rk.put("progress", progressArray);

                rankList.add(rk);
            }
            dataMap.set("rankList", rankList);
        }
        return dataMap.success();
    }
}
