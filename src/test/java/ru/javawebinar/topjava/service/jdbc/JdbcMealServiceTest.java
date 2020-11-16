package ru.javawebinar.topjava.service.jdbc;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.service.AbstractMealServiceTest;

import static ru.javawebinar.topjava.Profiles.JDBC;
import static ru.javawebinar.topjava.Profiles.TESTS;

@ActiveProfiles({JDBC, TESTS})
public class JdbcMealServiceTest extends AbstractMealServiceTest {
}