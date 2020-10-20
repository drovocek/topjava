package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.USER_ID;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml",
        "classpath:spring/spring-jdbc.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Test
    public void get() {
        assertMatch(service.get(USER_FIRST_MEAL_ID, USER_ID), getUserFirst());
    }

    @Test
    public void getAlien() {
        assertThrows(NotFoundException.class, () -> service.get(USER_FIRST_MEAL_ID, ADMIN_ID));
    }

    @Test
    public void getNonExistent() {
        assertThrows(NotFoundException.class, () -> service.get(NON_EXISTENT_MEAL_ID, ADMIN_ID));
    }

    @Test
    public void delete() {
        service.delete(USER_FIRST_MEAL_ID, USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(USER_FIRST_MEAL_ID, USER_ID));
    }

    @Test
    public void deleteAlien() {
        assertThrows(NotFoundException.class, () -> service.delete(USER_FIRST_MEAL_ID, ADMIN_ID));
    }

    @Test
    public void deleteNonExistent() {
        assertThrows(NotFoundException.class, () -> service.delete(NON_EXISTENT_MEAL_ID, ADMIN_ID));
    }

    @Test
    public void update() {
        Meal updated = getUserUpdated();
        service.update(updated, USER_ID);
        assertMatch(service.get(USER_FIRST_MEAL_ID, USER_ID), updated);
    }

    @Test
    public void updateAlien() {
        Meal updated = getUserUpdated();
        assertThrows(NotFoundException.class, () -> service.update(updated, ADMIN_ID));
    }

    @Test
    public void updateNonExistent() {
        Meal updated = getUserUpdated();
        updated.setId(NON_EXISTENT_MEAL_ID);
        assertThrows(NotFoundException.class, () -> service.update(updated, ADMIN_ID));
    }

    @Test
    public void create() {
        Meal newMeal = getNew();
        Meal created = service.create(newMeal, USER_ID);
        Integer newId = created.getId();
        newMeal.setId(newId);
        assertMatch(created, newMeal);
        assertMatch(service.get(newId, USER_ID), newMeal);
    }

    @Test
    public void createWithRepeatDateTime() {
        Meal newMeal = getNew();
        newMeal.setDateTime(userMeal1.getDateTime());
        assertThrows(DuplicateKeyException.class, () -> service.create(newMeal, USER_ID));
    }

    @Test
    public void getAll() {
        List<Meal> all = service.getAll(USER_ID);
        assertMatch(all, getUserMealList(meal -> true));
    }

    @Test
    public void getBetweenInclusive() {
        List<Meal> actual = service.getBetweenInclusive(BORDER_DATE, BORDER_DATE, USER_ID);
        assertMatch(actual, getUserMealList(meal -> meal.getDate().equals(BORDER_DATE)));
    }

    @Test
    public void getBetweenInclusiveForFreeBoarders() {
        List<Meal> actual = service.getBetweenInclusive(null, null, USER_ID);
        assertMatch(actual, getUserMealList(meal -> true));
    }

    public static List<Meal> getUserMealList(Predicate<Meal> filter) {
        return Stream.of(userMeal1, userMeal2, userMeal3, userMeal4, userMeal5, userMeal6, userMeal7)
                .filter(filter)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }
}