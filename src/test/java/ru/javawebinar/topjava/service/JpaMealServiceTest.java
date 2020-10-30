package ru.javawebinar.topjava.service;

import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.ActiveDbProfileResolver;

@ActiveProfiles(resolver = ActiveDbProfileResolver.class)
@Profile(value = "jpa")
public class JpaMealServiceTest extends AbstractMealServiceTest {
}
