package cn.edu.hunnu.acm.util;

import cn.edu.hunnu.acm.model.Contest;
import cn.edu.hunnu.acm.model.Problem;
import cn.edu.hunnu.acm.model.Ranking;
import cn.edu.hunnu.acm.model.Submission;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AlgorithmUtils {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static class submissionSorter implements Comparator<Submission> {
        @Override
        public int compare(Submission o1, Submission o2) {
            String user1 = o1.getUserId();
            String user2 = o2.getUserId();

            Integer problemId1 = o1.getProblemId();
            Integer problemId2 = o2.getProblemId();

            String submitTime1 = o1.getSubmitTime();
            String submitTime2 = o2.getSubmitTime();

            if(!user1.equals(user2)) {
                return user1.compareTo(user2);
            } else if(!problemId1.equals(problemId2)) {
                return problemId1.compareTo(problemId2);
            } else if(!submitTime1.equals(submitTime2)) {
                return submitTime1.compareTo(submitTime2);
            }
            return 0;
        }
    }

    private static class RankingSorter implements Comparator<Ranking> {
        @Override
        public int compare(Ranking o1, Ranking o2) {
            int compareAC = Integer.compare(o2.accept, o1.accept);
            if(compareAC != 0) return compareAC;
            else return Integer.compare(o1.penalty, o2.penalty);
        }
    }

    public static List<Integer> calculateDiff(List<Integer> sub, List<Integer> total) {
        int index1 = 0, index2 = 0;
        int subSize = sub.size(), totalSize = total.size();
        List<Integer> diff = new ArrayList<>();
        while(index1 < subSize && index2 < totalSize) {
            while(index2 < totalSize && !sub.get(index1).equals(total.get(index2))) {
                diff.add(total.get(index2));
                index2++;
            }
            index1++;
            index2++;
        }
        while (index2 < totalSize) {
            diff.add(total.get(index2));
            index2++;
        }
        return diff;
    }

    private static long calculateMinutes(String startTime, String submitTime) {
       LocalDateTime start = LocalDateTime.parse(startTime, formatter);
       LocalDateTime submit = LocalDateTime.parse(submitTime, formatter);
       Duration duration = Duration.between(start, submit);
       return duration.toMinutes();
    }

    public static List<Ranking> calculateRankings(Contest contest, List<Problem> problemList, List<Submission> submissionList) {
        final int addPenalty = 20;

        int problemCount = problemList.size();

        submissionList.sort(new submissionSorter());
        List<Ranking> rankingList = new ArrayList<>();

        int index = 0;
        int totalSubmission = submissionList.size();

        Integer[] firstAccept = new Integer[problemList.size()];
        Integer[] who = new Integer[problemList.size()];

        while(index < totalSubmission) {
            int userSubmission = 1;
            while(index + userSubmission < totalSubmission && submissionList.get(index).getUserId().equals(submissionList.get(index + userSubmission).getUserId())) {
                userSubmission++;
            }

            Ranking ranking = new Ranking();
            ranking.userId = submissionList.get(index).getUserId();
            ranking.rank = -1;
            ranking.accept = 0;
            ranking.penalty = 0;
            ranking.progress = new ArrayList<>();

            int scanIndex = index;
            int endIndex = index + userSubmission;

            for(int i = 0; i < problemCount; i++) {
                Ranking.Info progress = new Ranking.Info();
                progress.status = Ranking.rankStatus.notTry;
                progress.tryTime = 0;
                progress.acceptTime = -1;

                Problem pb = problemList.get(i);

                boolean alreadyAC = false;

                while(scanIndex < endIndex && pb.getProblemId().equals(submissionList.get(scanIndex).getProblemId())) {
                    Submission sb = submissionList.get(scanIndex);
                    if(!alreadyAC) {
                        if(sb.getResult().equals(Constants.result.Accepted)) {
                            int acceptTime = (int)calculateMinutes(contest.getStartTime(), sb.getSubmitTime());
                            if(firstAccept[i] == null || firstAccept[i] > acceptTime) {
                                firstAccept[i] = acceptTime;
                                who[i] = rankingList.size();
                            }
                            progress.status = Ranking.rankStatus.Accept;
                            progress.acceptTime = acceptTime;
                            ranking.penalty += (progress.tryTime * addPenalty) + acceptTime;
                            ranking.accept++;
                            alreadyAC = true;
                        }else {
                            progress.status = Ranking.rankStatus.Attempted;
                            progress.tryTime++;
                        }
                    }
                    ++scanIndex;
                }
                ranking.progress.add(progress);
            }
            rankingList.add(ranking);
            index += userSubmission;
        }

        for(int i = 0; i < problemCount; i++) {
            if(firstAccept[i] != null) {
                rankingList.get(who[i]).progress.get(i).status = Ranking.rankStatus.firstAC;
            }
        }

        rankingList.sort(new RankingSorter());

        int userCount = rankingList.size();

        for(int i = 0; i < userCount; i++) {
            rankingList.get(i).rank = i + 1;
        }

        return rankingList;
    }
}
