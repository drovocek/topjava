package ru.javawebinar.topjava.service.jpa;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.service.AbstractTestMealService;

@ActiveProfiles(profiles = {Profiles.HSQL_DB, Profiles.DATAJPA})
public class JpaMealServiceTest extends AbstractTestMealService {
}
