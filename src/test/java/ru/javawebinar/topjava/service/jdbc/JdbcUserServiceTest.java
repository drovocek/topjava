package ru.javawebinar.topjava.service.jdbc;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.AbstractUserServiceTest;

import java.util.List;

import static ru.javawebinar.topjava.Profiles.JDBC;
import static ru.javawebinar.topjava.Profiles.TESTS;
import static ru.javawebinar.topjava.UserTestData.*;

@ActiveProfiles({JDBC, TESTS})
public class JdbcUserServiceTest extends AbstractUserServiceTest {

    @Test
    public void a1update() {
        User updated = getUpdated();
        service.update(updated);
        USER_MATCHER.assertMatch(service.getAll(), getUpdated(), user); //важно: тут выполняем вызов service.getAll
    }

    @Test
    public void a2getAll() {
        List<User> all = service.getAll();
        USER_MATCHER.assertMatch(all, admin, user);
    }
}