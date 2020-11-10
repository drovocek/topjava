package ru.javawebinar.topjava.service.jpa;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.service.AbstractMealServiceTest;

import static ru.javawebinar.topjava.Profiles.JPA;
import static ru.javawebinar.topjava.Profiles.TEST;

@ActiveProfiles({JPA, TEST})
public class JpaMealServiceTest extends AbstractMealServiceTest {
}