package cn.edu.hunnu.acm.controller;

import cn.edu.hunnu.acm.aspect.annotation.AdminRequired;
import cn.edu.hunnu.acm.aspect.annotation.SuperAdminRequired;
import cn.edu.hunnu.acm.framework.annotation.Controller;
import cn.edu.hunnu.acm.framework.annotation.PostMapping;
import cn.edu.hunnu.acm.framework.annotation.Qualifier;
import cn.edu.hunnu.acm.framework.annotation.RequestParam;
import cn.edu.hunnu.acm.model.*;
import cn.edu.hunnu.acm.service.*;
import cn.edu.hunnu.acm.util.Constants;
import cn.edu.hunnu.acm.util.DataMap;
import cn.edu.hunnu.acm.util.TextUtils;
import cn.edu.hunnu.acm.util.ValidateChecker;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

@Controller
public class AdminController {
    @Qualifier
    private AnnouncementService announcementService;

    @Qualifier
    private ContestService contestService;

    @Qualifier
    private ProblemService problemService;

    @Qualifier
    private SubmissionService submissionService;

    @Qualifier
    private UserService userService;

    @Qualifier
    private UDPServerNotify udpServerNotify;

    @AdminRequired
    @PostMapping(value = "/admin/createAnnouncement", produces = "application/json;charset=utf-8")
    public DataMap createAnnouncement(HttpSession session,
                                      @RequestParam("newsType") String type,
                                      @RequestParam(value = "contestId", required = false) Integer contestId,
                                      @RequestParam("title") String title,
                                      @RequestParam("content") String content) {
        Short newsType = null;
        switch (type)  {
            case "report" :
                newsType = Constants.announcementType.ReportType;
                break;
            case "solution" :
                newsType = Constants.announcementType.SolutionType;
                break;
            case "contest" :
                newsType = Constants.announcementType.ContestType;
                break;
            default:
                newsType = -1;
        }
        DataMap dataMap = new DataMap();
        String userId = (String) session.getAttribute("userId");

        if(userId == null) {
            dataMap.setErrorInfo(Constants.errorMessage.invalid_session);
            return dataMap.fail();
        }

        if(newsType == Constants.announcementType.ContestType) {
            if(contestId == null) {
                dataMap.setErrorInfo(Constants.errorMessage.invalid_parameter);
                return dataMap.fail();
            }
        } else {
            contestId = null;
        }

        Announcement announcement = new Announcement();
        announcement.setNewsType(newsType);
        announcement.setContestId(contestId);
        announcement.setCreator(userId);
        announcement.setTitle(title);
        announcement.setContent(content);

        if(ValidateChecker.checkAnnouncementValidate(announcement)) {
            announcementService.createAnnouncement(announcement);
            return dataMap.success();
        } else {
            dataMap.setErrorInfo(Constants.errorMessage.invalid_parameter);
            return dataMap.fail();
        }
    }

    @AdminRequired
    @PostMapping(value = "/admin/updateAnnouncement", produces = "application/json;charset=utf-8")
    public DataMap updateAnnouncement(@RequestParam("newsId") Integer newsId,
                                      @RequestParam("title") String title,
                                      @RequestParam("content") String content) {
        Announcement announcement = new Announcement();
        announcement.setNewsId(newsId);
        announcement.setTitle(title);
        announcement.setContent(content);

        DataMap dataMap = new DataMap();

        announcementService.updateAnnouncement(announcement);
        return dataMap.success();
    }

    @AdminRequired
    @PostMapping(value = "/admin/createContest", produces = "application/json;charset=utf-8")
    public DataMap createContest(Contest contest) {
        DataMap dataMap = new DataMap();
        if(ValidateChecker.checkContestValidate(contest)) {
            contestService.createContest(contest);
            return dataMap.success();
        } else {
            dataMap.setErrorInfo(Constants.errorMessage.invalid_parameter);
            return dataMap.fail();
        }
    }

    @AdminRequired
    @PostMapping(value = "/admin/updateContest", produces = "application/json;charset=utf-8")
    public DataMap updateContest(Contest contest) {
        DataMap dataMap = new DataMap();
        if(ValidateChecker.checkContestValidate(contest) && contest.getContestId() != null) {
            contestService.updateContest(contest);
            return dataMap.success();
        } else {
            dataMap.setErrorInfo(Constants.errorMessage.invalid_parameter);
            return dataMap.fail();
        }
    }

    @AdminRequired
    @PostMapping(value = "/admin/createProblem", produces = "application/json;charset=utf-8")
    public DataMap createProblem(Problem problem) {
        DataMap dataMap = new DataMap();
        if(ValidateChecker.checkProblemValidate(problem)) {
            problemService.createProblem(problem);
            return dataMap.success();
        } else {
            dataMap.setErrorInfo(Constants.errorMessage.invalid_parameter);
            return dataMap.fail();
        }
    }

    @AdminRequired
    @PostMapping(value = "/admin/addProblemFromPublic", produces = "application/json;charset=utf-8")
    public DataMap addProblemFromPublic(@RequestParam("problemId") Integer problemId,
                                        @RequestParam("contestId") Integer contestId,
                                        @RequestParam("mappingId") Integer mappingId,
                                        @RequestParam("aliasTitle") String aliasTitle) {
        DataMap dataMap = new DataMap();
        if(contestId <= 0) {
            dataMap.setErrorInfo(Constants.errorMessage.contest_not_found);
            return dataMap.fail();
        }

        Problem problem = problemService.queryProblemById(problemId, -1);
        problem.setProblemId(mappingId);
        problem.setContestId(contestId);
        problem.setTitle(aliasTitle);

        if(ValidateChecker.checkProblemValidate(problem)) {
            problemService.createProblem(problem);
            return dataMap.success();
        } else {
            dataMap.setErrorInfo(Constants.errorMessage.invalid_parameter);
            return dataMap.fail();
        }
    }

    @AdminRequired
    @PostMapping(value = "/admin/updateProblem", produces = "application/json;charset=utf-8")
    public DataMap updateProblem(Problem problem) {
        DataMap dataMap = new DataMap();
        if(ValidateChecker.checkProblemValidate(problem)) {
            problemService.updateProblem(problem);
            return dataMap.success();
        } else {
            dataMap.setErrorInfo(Constants.errorMessage.invalid_parameter);
            return dataMap.fail();
        }
    }

    @AdminRequired
    @PostMapping(value = "/admin/uploadTestCase", produces = "application/json;charset=utf-8")
    public DataMap uploadTestCase(HttpServletRequest request) {
        DataMap dataMap = new DataMap();
        dataMap.setErrorInfo(Constants.errorMessage.action_not_support);
        return dataMap.fail();
    }

    @AdminRequired
    @PostMapping(value = "/admin/updateSubmission", produces = "application/json;charset=utf-8")
    public DataMap updateSubmission(Submission submission) {
        DataMap dataMap = new DataMap();
        if(submission.getRunId() == null || submission.getResult() == null) {
            dataMap.setErrorInfo(Constants.errorMessage.invalid_parameter);
            return dataMap.fail();
        }
        submissionService.updateSubmission(submission);
        return dataMap.success();
    }

    @AdminRequired
    @PostMapping(value = "/admin/uploadImage", produces = "application/json;charset=utf-8")
    public DataMap uploadImage(HttpServletRequest request) {
        DataMap dataMap = new DataMap();
        try {
            Part part = request.getPart("image");
            String ext = TextUtils.getFileExt(part.getSubmittedFileName());

            String fileName = String.format("%s.%s", TextUtils.getRandomString(20), ext);
            String fullPath = TextUtils.pathJoin(Constants.image_absolute_path, fileName);

            part.write(fullPath);
            TextUtils.modifyPermission(fullPath);
            dataMap.set("path", TextUtils.pathJoin(Constants.image_relative_path, fileName));
            return dataMap.success();
        } catch (Exception e) {
            e.printStackTrace();
            dataMap.setErrorInfo(Constants.errorMessage.disk_access_error);
            return dataMap.fail();
        }
    }

    @SuperAdminRequired
    @PostMapping(value = "/admin/rejudge", produces = "application/json;charset=utf-8")
    public DataMap rejudgeSubmission(@RequestParam("runId") Integer runId) {
        DataMap dataMap = new DataMap();
        Submission submission = submissionService.querySubmissionById(runId);

        Problem simpleProblem = null;

        simpleProblem = problemService.checkProblemExist(submission.getProblemId(), submission.getContestId());

        if(simpleProblem != null) {
            submission.setResult(Constants.result.Pending);
            submission.setTimeUsed(-1);
            submission.setMemoryUsed(-1);
            submission.setExt("");

            submissionService.updateSubmission(submission);

            RunRequest runRequest = new RunRequest();
            runRequest.submissionId = runId;
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

    @SuperAdminRequired
    @PostMapping(value = "/admin/updateUserRole", produces = "application/json;charset=utf-8")
    public DataMap updateUserRole(@RequestParam("userId") String userId,
                                  @RequestParam("userType") String userType) {
        DataMap dataMap = new DataMap();

        if(!TextUtils.isEmpty(userId) && (Constants.userType.Admin.equals(userType) || Constants.userType.Regular.equals(userType))) {
            if(userService.updateUserRole(userId, userType)) {
                return dataMap.success();
            }
        }
        dataMap.setErrorInfo(Constants.errorMessage.invalid_parameter);
        return dataMap.fail();
    }
}
