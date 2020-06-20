package cn.edu.hunnu.acm.model;

import java.util.List;

public class Ranking {
    public String userId;

    public int rank;

    public int accept;

    public int penalty;

    public List<Info> progress;

    public static class Info {
        public int acceptTime;
        public int tryTime;
        public Short status;
    }

    public static class rankStatus {
        public static final short notTry = 0;
        public static final short firstAC = 1;
        public static final short Accept = 2;
        public static final short Attempted = 3;
    }
}
