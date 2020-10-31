package ru.javawebinar.topjava.service;

import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(profiles = {"hsqldb", "jpa"})
public class JpaTestUserService extends AbstractTestUserService {
}
