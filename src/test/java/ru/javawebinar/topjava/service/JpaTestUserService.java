package ru.javawebinar.topjava.service;

import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(profiles = {"postgres", "jpa"})
public class JpaTestUserService extends AbstractTestUserService {
}
