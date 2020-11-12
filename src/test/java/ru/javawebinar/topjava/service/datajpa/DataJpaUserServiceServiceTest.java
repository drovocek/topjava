package ru.javawebinar.topjava.service.datajpa;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.AbstractUserServiceTest;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import static ru.javawebinar.topjava.Profiles.DATAJPA;
import static ru.javawebinar.topjava.UserTestData.*;
import static ru.javawebinar.topjava.MealTestData.adminMeals;

@ActiveProfiles(DATAJPA)
public class DataJpaUserServiceServiceTest extends AbstractUserServiceTest {
    @Test
    public void getWithMeals() {
        System.out.println(cacheManager);
        User user = service.getWithMeals(USER_ID);
        USER_MATCHER.assertMatch(user, UserTestData.user);
        MealTestData.MEAL_MATCHER.assertMatch(user.getMeals(), MealTestData.meals);
    }

    @Test
    public void getWithMealsForManyRolesUser() {
        System.out.println(cacheManager);

        User admin = service.getWithMeals(ADMIN_ID);

        System.out.println(admin);
        System.out.println(admin.getMeals());
        System.out.println(admin.getRoles());
        USER_MATCHER.assertMatch(admin, UserTestData.admin);



        MealTestData.MEAL_MATCHER.assertMatch(admin.getMeals(), adminMeals);
    }

    @Test
    public void getWithMealsNotFound() {
        Assert.assertThrows(NotFoundException.class,
                () -> service.getWithMeals(1));
    }
}