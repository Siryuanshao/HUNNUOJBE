package cn.edu.hunnu.acm.util;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;


public class GetSqlSessionFactory {
    private volatile static SqlSessionFactory sqlSessionFactory = null;

    private GetSqlSessionFactory() {

    }

    static SqlSessionFactory getSqlSessionFactory() {
        if (sqlSessionFactory == null) {
           synchronized (GetSqlSessionFactory.class) {
               if(sqlSessionFactory == null) {
                   String resource = "mybatis-config.xml";
                   InputStream inputStream = null;
                   try {
                       inputStream = Resources.getResourceAsStream(resource);
                       sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
                   } catch (IOException e) {
                       e.printStackTrace();
                   } finally {
                       if(inputStream != null) {
                           try {
                               inputStream.close();
                           } catch (IOException e) {
                               e.printStackTrace();
                           }
                       }
                   }
               }
           }
        }
        return sqlSessionFactory;
    }
}