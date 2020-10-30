package ru.javawebinar.topjava.service;

import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(profiles = {"postgres", "datajpa"})
public class DataJpaTestMealService extends AbstractTestMealService {
}
