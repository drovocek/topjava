package ru.javawebinar.topjava.service.jpa;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.service.AbstractMealServiceTest;

import static ru.javawebinar.topjava.Profiles.DATAJPA;
import static ru.javawebinar.topjava.Profiles.TEST;

@ActiveProfiles({DATAJPA, TEST})
public class JpaMealServiceTest extends AbstractMealServiceTest {
}