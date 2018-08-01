package com.auacm.database;

import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class MySQLConfig {

    public MySQLConfig() { }

//    @Bean
//    public SessionFactory getSessionFactory(EntityManagerFactory entityManagerFactory) {
//        if (entityManagerFactory.unwrap(SessionFactory.class) == null) {
//            throw new NullPointerException("factory is not a hibernate factory");
//        }
//        return entityManagerFactory.unwrap(SessionFactory.class);
//    }
}
