package ru.javawebinar.topjava.service.jpa;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.service.AbstractCashableUserServiceTest;

import static ru.javawebinar.topjava.Profiles.*;

@ActiveProfiles({DATAJPA,TEST})
public class JpaUserServiceServiceTest extends AbstractCashableUserServiceTest {
}