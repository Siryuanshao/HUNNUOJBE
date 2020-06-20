package cn.edu.hunnu.acm.model;

public class Problem {
    private Integer problemId;

    private Integer contestId;

    private String title;

    private Integer timeLimit;

    private Integer memoryLimit;

    private String description;

    private String inputDesc;

    private String outputDesc;

    private String inputSample;

    private String outputSample;

    private String hint;

    private String source;

    private Boolean isSpj;

    private Integer accept;

    private Integer submit;

    public Integer getProblemId() {
        return problemId;
    }

    public void setProblemId(Integer problemId) {
        this.problemId = problemId;
    }

    public Integer getContestId() {
        return contestId;
    }

    public void setContestId(Integer contestId) {
        this.contestId = contestId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Integer getMemoryLimit() {
        return memoryLimit;
    }

    public void setMemoryLimit(Integer memoryLimit) {
        this.memoryLimit = memoryLimit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInputDesc() {
        return inputDesc;
    }

    public void setInputDesc(String inputDesc) {
        this.inputDesc = inputDesc;
    }

    public String getOutputDesc() {
        return outputDesc;
    }

    public void setOutputDesc(String outputDesc) {
        this.outputDesc = outputDesc;
    }

    public String getInputSample() {
        return inputSample;
    }

    public void setInputSample(String inputSample) {
        this.inputSample = inputSample;
    }

    public String getOutputSample() {
        return outputSample;
    }

    public void setOutputSample(String outputSample) {
        this.outputSample = outputSample;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Boolean getSpj() {
        return isSpj;
    }

    public void setSpj(Boolean spj) {
        isSpj = spj;
    }

    public Integer getAccept() {
        return accept;
    }

    public void setAccept(Integer accept) {
        this.accept = accept;
    }

    public Integer getSubmit() {
        return submit;
    }

    public void setSubmit(Integer submit) {
        this.submit = submit;
    }
}
