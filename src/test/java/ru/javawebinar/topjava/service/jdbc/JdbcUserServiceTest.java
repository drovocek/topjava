package ru.javawebinar.topjava.service.jdbc;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.service.AbstractUserServiceTest;

import static ru.javawebinar.topjava.Profiles.JDBC;
import static ru.javawebinar.topjava.Profiles.TEST;

@ActiveProfiles({JDBC,TEST})
public class JdbcUserServiceTest extends AbstractUserServiceTest {
}