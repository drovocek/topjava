package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.util.TimeUtil.*;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000).forEach(System.out::println);
        System.out.println("-----");
        filteredByCyclesOptional(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000).forEach(System.out::println);
        System.out.println("-----");
        filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000).forEach(System.out::println);
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMealWithExcess> filteredList = new LinkedList<>();
        Map<LocalDate, Integer> caloryByDate = new HashMap();

        for (UserMeal meal : meals) {
            caloryByDate.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum);
        }

        for (UserMeal meal : meals) {
            if (isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                Boolean[] excess = new Boolean[]{
                        caloryByDate.get(meal.getDateTime().toLocalDate()) > caloriesPerDay
                };
                filteredList.add(
                        new UserMealWithExcess(
                                meal.getDateTime(),
                                meal.getDescription(),
                                meal.getCalories(),
                                excess)
                );
            }
        }

        return filteredList;
    }

    public static List<UserMealWithExcess> filteredByCyclesOptional(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMealWithExcess> filteredList = new LinkedList<>();
        Map<LocalDate, Integer> caloryByDate = new HashMap();
        Map<LocalDate, Boolean[]> excessByDate = new HashMap();
        LocalDateTime mealDateTime;
        LocalDate mealDate;
        int calories;

        for (UserMeal meal : meals) {
            mealDateTime = meal.getDateTime();
            mealDate = mealDateTime.toLocalDate();
            calories = meal.getCalories();

            caloryByDate.merge(mealDate, calories, Integer::sum);
            excessByDate.putIfAbsent(mealDate, new Boolean[]{false});

            if (caloryByDate.get(mealDate) > caloriesPerDay) excessByDate.get(mealDate)[0] = true;

            if (isBetweenHalfOpen(mealDateTime.toLocalTime(), startTime, endTime)) {
                filteredList.add(
                        new UserMealWithExcess(
                                mealDateTime,
                                meal.getDescription(),
                                calories,
                                excessByDate.get(mealDate))
                );
            }
        }

        return filteredList;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloryByDate = meals.stream()
                .collect(Collectors.groupingBy(
                        x -> LocalDate.from(x.getDateTime()),
                        Collectors.summingInt(y -> y.getCalories())
                ));

        Predicate<UserMeal> itInBounds = x -> isBetweenHalfOpen(x.getDateTime().toLocalTime(), startTime, endTime);
        Function<UserMeal, Boolean[]> getExcess = x -> new Boolean[]{caloryByDate.get(LocalDate.from(x.getDateTime())) > caloriesPerDay};

        List<UserMealWithExcess> filteredList = meals.stream()
                .filter(itInBounds)
                .map(x -> new UserMealWithExcess(
                        x.getDateTime(),
                        x.getDescription(),
                        x.getCalories(),
                        getExcess.apply(x)))
                .collect(LinkedList::new,LinkedList::add,LinkedList::addAll);

        return filteredList;
    }
}
