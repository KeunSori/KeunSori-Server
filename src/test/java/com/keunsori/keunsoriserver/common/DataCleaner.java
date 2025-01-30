package com.keunsori.keunsoriserver.common;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@Profile("test")
public class DataCleaner implements InitializingBean {

    private List<String> tableNames;

    @PersistenceContext
    private EntityManager em;

    @PostConstruct
    public void findTableNames() {
        tableNames = new ArrayList<>();
        List<Object[]> tables = em.createNativeQuery("show tables").getResultList();
        for (Object[] table : tables) {
            String name = table[0].toString();
            if (name.equals("MEMBER")) {
                continue;
            }
            tableNames.add(name);
        }
        System.out.println(tableNames);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        findTableNames();
    }

    private void truncate() {
        em.createNativeQuery(String.format("SET FOREIGN_KEY_CHECKS = 0")).executeUpdate();
        for (String tableName : tableNames) {
            em.createNativeQuery("truncate table " + tableName).executeUpdate();
        }
        em.createNativeQuery(String.format("SET FOREIGN_KEY_CHECKS = 1")).executeUpdate();
    }

    @Transactional
    public void clear() {
        em.clear();
        truncate();
    }
}
