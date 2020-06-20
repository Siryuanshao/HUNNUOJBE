package cn.edu.hunnu.acm.service;

import cn.edu.hunnu.acm.model.Submission;

import java.util.Map;

public interface SubmissionService {
    Submission querySubmissionById(Integer submissionId);
    Map<String, Object> querySubmissionList(Submission submission, int offset, int limit);
    Map<String, Object> queryProblemSummary(Integer problemId, Short language, int offset, int limit);
    Integer createSubmission(Submission submission);
    void updateSubmission(Submission submission);
}
