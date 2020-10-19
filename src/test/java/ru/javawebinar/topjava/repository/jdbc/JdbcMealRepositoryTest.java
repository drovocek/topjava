package ru.javawebinar.topjava.repository.jdbc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;
import static ru.javawebinar.topjava.util.DateTimeUtil.*;
import static ru.javawebinar.topjava.util.Util.*;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class JdbcMealRepositoryTest {

    @Autowired
    private JdbcMealRepository repository;

    @Test
    public void get() {
        assertMatch(getUserFirst(), repository.get(USER_FIRST_MEAL_ID, USER_ID));
    }

    @Test
    public void getAlien() {
        assertNull(repository.get(USER_FIRST_MEAL_ID, ADMIN_ID));
    }

    @Test
    public void delete() {
        repository.delete(USER_FIRST_MEAL_ID, USER_ID);
        assertNull(repository.get(USER_FIRST_MEAL_ID, USER_ID));
    }

    @Test
    public void deleteAlien() {
        repository.delete(USER_FIRST_MEAL_ID, ADMIN_ID);
        assertNotNull(repository.get(USER_FIRST_MEAL_ID, USER_ID));
    }

    @Test
    public void update() {
        Meal updated = getUserUpdated();
        repository.save(updated, USER_ID);
        assertEquals(updated, repository.get(USER_FIRST_MEAL_ID, USER_ID));
    }

    @Test
    public void updateAlien() {
        Meal updated = getUserUpdated();
        assertNull(repository.save(updated, ADMIN_ID));
    }

    @Test
    public void create() {
        Meal newMeal = getNew();
        Meal created = repository.save(newMeal, USER_ID);
        Integer newId = created.getId();
        newMeal.setId(newId);
        assertEquals(created, newMeal);
        assertEquals(repository.get(newId, USER_ID), newMeal);
    }

    @Test
    public void getAll() {
        List<Meal> all = repository.getAll(USER_ID);
        assertMatch(all, getUserMealList(meal -> true));
    }

    @Test
    public void getBetweenHalfOpen() {
        LocalDateTime leftBorder = atStartOfDayOrMin(BORDER_DATE);
        LocalDateTime rightBorder = atStartOfNextDayOrMax(BORDER_DATE);
        List<Meal> actual = repository.getBetweenHalfOpen(leftBorder, rightBorder, USER_ID);
        assertMatch(actual, getUserMealList(meal -> isBetweenHalfOpen(meal.getDateTime(), leftBorder, rightBorder)));
    }
}