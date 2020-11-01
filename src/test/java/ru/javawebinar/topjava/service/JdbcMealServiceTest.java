package ru.javawebinar.topjava.service;

import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(profiles = {"hsqldb", "jdbc"})
public class JdbcMealServiceTest extends AbstractTestMealService {
    static {
        System.setProperty("spring.profiles.active", "hsqldb, jdbc");
    }
}
