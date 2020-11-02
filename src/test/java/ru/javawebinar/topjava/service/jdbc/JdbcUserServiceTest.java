package ru.javawebinar.topjava.service.jdbc;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.service.AbstractTestUserService;

@ActiveProfiles(profiles = {Profiles.HSQL_DB, Profiles.DATAJPA})
public class JdbcUserServiceTest extends AbstractTestUserService {
}
