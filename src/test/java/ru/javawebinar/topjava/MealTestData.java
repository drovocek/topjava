package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {
    public static final int USER_FIRST_MEAL_ID = START_SEQ;
    public static final LocalDate BORDER_DATE = LocalDate.of(2020, Month.JANUARY, 30);
    public static final List<Meal> userMeals = Arrays.asList(
            new Meal(USER_FIRST_MEAL_ID, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
            new Meal(USER_FIRST_MEAL_ID + 1, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
            new Meal(USER_FIRST_MEAL_ID + 2, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
            new Meal(USER_FIRST_MEAL_ID + 3, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
            new Meal(USER_FIRST_MEAL_ID + 4, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
            new Meal(USER_FIRST_MEAL_ID + 5, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
            new Meal(USER_FIRST_MEAL_ID + 6, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
    );

    public static Meal getNew() {
        return new Meal(null, LocalDateTime.of(2020, Month.OCTOBER, 19, 10, 0), "NewMeal", 777);
    }

    public static Meal getUserUpdated() {
        Meal updated = new Meal(userMeals.get(0));
        updated.setId(USER_FIRST_MEAL_ID);
        updated.setDescription("UpdatedDescription");
        updated.setCalories(999);
        updated.setDateTime(LocalDateTime.of(2020, Month.OCTOBER, 19, 10, 0));
        return updated;
    }

    public static Meal getUserFirst() {
        Meal firstUserMeal = new Meal(userMeals.get(0));
        firstUserMeal.setId(USER_FIRST_MEAL_ID);
        return firstUserMeal;
    }

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingFieldByFieldElementComparator().containsAll(expected);
    }

    public static List<Meal> getUserMealList(Predicate<Meal> filter) {
        return userMeals.stream()
                .filter(filter)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }
}
