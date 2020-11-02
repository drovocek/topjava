package ru.javawebinar.topjava.service.jpa;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.service.AbstractTestUserService;

@ActiveProfiles(profiles = Profiles.JPA)
public class JpaUserServiceTest extends AbstractTestUserService {
}
