package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
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
        Meal expected = MealsUtil.meals.get(0);
        expected.setId(START_SEQ);
        Meal actual = service.get(START_SEQ, START_SEQ);
        assertEquals(expected, actual);
    }

    @Test
    public void delete() {
        service.delete(START_SEQ, START_SEQ);
        assertThrows(NotFoundException.class, () -> service.get(START_SEQ, START_SEQ));
    }

    @Test
    public void getBetweenInclusive() {
        AtomicInteger newId = new AtomicInteger(START_SEQ);
        LocalDate borderDate = LocalDate.of(2020, Month.JANUARY, 30);
        List<Meal> actual = service.getBetweenInclusive(borderDate, borderDate, START_SEQ);

        assertArrayEquals(actual.toArray(),
                MealsUtil.meals.stream()
                        .peek(meal -> meal.setId(newId.getAndIncrement()))
                        .filter(meal -> meal.getDate().equals(borderDate))
                        .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                        .peek(System.out::println).toArray());
    }

    @Test
    public void getAll() {
        AtomicInteger newId = new AtomicInteger(START_SEQ);
        List<Meal> actual = service.getAll(START_SEQ);
        assertArrayEquals(actual.toArray(),
                MealsUtil.meals.stream()
                        .peek(meal -> meal.setId(newId.getAndIncrement())).toArray());
    }

    @Test
    public void update() {
        Meal updated = new Meal(START_SEQ, LocalDateTime.of(2020, Month.OCTOBER, 18, 10, 0), "updatableMeal", 777);
        service.update(updated, START_SEQ);
        assertEquals(updated, service.get(START_SEQ, START_SEQ));
    }

    @Test
    public void create() {
        Meal newMeal = new Meal(LocalDateTime.of(2020, Month.OCTOBER, 18, 10, 0), "newMeal", 777);
        Meal created = service.create(newMeal, START_SEQ);
        Integer newId = created.getId();
        newMeal.setId(newId);
        assertEquals(created, newMeal);
        assertEquals(service.get(newId, START_SEQ), newMeal);
    }
}