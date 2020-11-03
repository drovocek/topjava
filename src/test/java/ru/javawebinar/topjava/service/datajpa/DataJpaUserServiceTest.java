package ru.javawebinar.topjava.service.datajpa;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.AbstractUserServiceTest;

import java.util.ArrayList;

import static ru.javawebinar.topjava.MealTestData.MEAL_MATCHER;
import static ru.javawebinar.topjava.UserTestData.*;

@ActiveProfiles(profiles = Profiles.DATAJPA)
public class DataJpaUserServiceTest extends AbstractUserServiceTest {

    @Test
    public void getWithAllMeal() {
        User user = service.getWithAllMeal(USER_ID);
        USER_MATCHER.assertMatch(user, UserTestData.user);
        MEAL_MATCHER.assertMatch(user.getMeals(), MealTestData.meals);
    }

    @Test
    public void getWithAllMealForUserWhithoutMeal() {
        User newUserWhithoutMeal = service.create(getNew());
        User userWhithoutMeal = service.getWithAllMeal(newUserWhithoutMeal.getId());
        USER_MATCHER.assertMatch(userWhithoutMeal, newUserWhithoutMeal);
        MEAL_MATCHER.assertMatch(userWhithoutMeal.getMeals(), new ArrayList<>());
    }
}
