package cn.edu.hunnu.acm.util;

import cn.edu.hunnu.acm.model.*;

public class ValidateChecker {
    public static boolean checkAnnouncementValidate(Announcement announcement) {
        // required
        if(announcement.getNewsType() < Constants.announcement_range.LRange || announcement.getNewsType() > Constants.announcement_range.RRange) return false;
        if(announcement.getNewsType() == Constants.announcementType.ContestType) {
            if(announcement.getContestId() == null) return false;
        }
        else announcement.setContestId(null);
        if(TextUtils.isEmpty(announcement.getCreator())) return false;
        if(TextUtils.isEmpty(announcement.getTitle())) return false;
        if(announcement.getContent() == null) return false;

        return true;
    }

    public static boolean checkContestValidate(Contest contest) {
        // required
        if(TextUtils.isEmpty(contest.getTitle())) return false;
        if(!TextUtils.checkDateFormat(contest.getStartTime())) return false;
        if(!TextUtils.checkDateFormat(contest.getEndTime())) return false;
        if(contest.getStartTime().compareTo(contest.getEndTime()) > 0) return false;
        if(contest.getType() != null && (contest.getType() < Constants.contest_range.LRange || contest.getType() > Constants.contest_range.RRange)) return false;

        // with default
        if(contest.getType() == null) contest.setType(Constants.contestType.PUBLIC);
        if(contest.getExt() == null) contest.setExt("");

        return true;
    }

    public static boolean checkProblemValidate(Problem problem) {
        // required
        if(problem.getProblemId() == null) return false;
        if(TextUtils.isEmpty(problem.getTitle())) return false;
        if(problem.getDescription() == null) return false;
        if(problem.getInputDesc() == null) return false;
        if(problem.getOutputDesc() == null) return false;
        if(problem.getInputSample() == null) return false;
        if(problem.getOutputSample() == null) return false;

        // with default
        if(problem.getContestId() == null) problem.setContestId(-1);
        if(problem.getTimeLimit() == null) problem.setTimeLimit(1000);
        if(problem.getMemoryLimit() == null) problem.setMemoryLimit(32768);
        if(problem.getHint() == null) problem.setHint("");
        if(problem.getSource() == null) problem.setSource("");
        if(problem.getSpj() == null) problem.setSpj(false);

        return true;
    }

    public static boolean checkSubmissionValidate(Submission submission) {
        // required
        if(submission.getProblemId() == null) return false;
        if(submission.getLanguage() == null) return false;
        if(TextUtils.isEmpty(submission.getSourcecode())) return false;

        if(submission.getLanguage() < Constants.language_range.LRange || submission.getLanguage() > Constants.language_range.RRange)
            return false;

        // with default
        if(submission.getContestId() == null) submission.setContestId(-1);

        return true;
    }

    public static boolean checkUserValidate(User user) {
        // required
        if(TextUtils.isEmpty(user.getUserId())) return false;

        // with default
        if(TextUtils.isEmpty(user.getUserName())) user.setUserName(null);
        if(TextUtils.isEmpty(user.getSchool())) user.setSchool(null);
        if(TextUtils.isEmpty(user.getGrade())) user.setGrade(null);
        if(TextUtils.isEmpty(user.getWhatUp())) user.setWhatUp(null);
        if(user.getSex() != null && user.getSex() != 0 && user.getSex() != 1) user.setSex((short)-1);

        return true;
    }
}
