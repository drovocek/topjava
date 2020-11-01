package ru.javawebinar.topjava.service;

import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(profiles = {"hsqldb", "jpa"})
public class JpaMealServiceTest extends AbstractTestMealService {
}
