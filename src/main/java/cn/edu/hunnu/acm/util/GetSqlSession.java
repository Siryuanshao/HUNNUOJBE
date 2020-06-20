package cn.edu.hunnu.acm.util;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetSqlSession {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetSqlSession.class);
    private static ThreadLocal<SqlSession> tl = new ThreadLocal<SqlSession>();

    public static SqlSession getSqlSession() {
        SqlSession sqlSession = tl.get();
        if (sqlSession == null) {
            sqlSession = GetSqlSessionFactory.getSqlSessionFactory().openSession();
            tl.set(sqlSession);
        }
        LOGGER.info("Get SqlSession hashCode : {}." , sqlSession.hashCode());
        return sqlSession;
    }

    public static void commit() {
        if (tl.get() != null) {
            tl.get().commit();
            LOGGER.info("SqlSession commit.");
        }
    }

    public static void rollback() {
        if (tl.get() != null) {
            tl.get().rollback();
            LOGGER.info("SqlSession rollback.");
        }
    }

    public static void close() {
        if(tl.get() != null) {
            tl.get().close();
            tl.set(null);
            LOGGER.info("SqlSession close.");
        }
    }
}