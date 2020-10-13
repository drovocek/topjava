package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.util.DateTimeUtil.isBetweenDate;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);
    private static final Logger log = LoggerFactory.getLogger(InMemoryMealRepository.class);

    {
        MealsUtil.meals.forEach(meal -> save(1, meal));
    }

    @Override
    public Meal save(int userId, Meal meal) {
        log.info("save meal {} of userId={}", meal, userId);
        Map<Integer, Meal> userMeal = repository.computeIfAbsent(userId, id-> new HashMap<>());
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            userMeal.put(meal.getId(), meal);
            return meal;
        }
        return userMeal.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int userId, int mealId) {
        log.info("delete meal with id={} of userId={}", mealId, userId);
        Map<Integer, Meal> userMeal = repository.get(userId);
        return userMeal.remove(mealId) != null;
    }

    @Override
    public Meal get(int userId, int mealId) {
        log.info("get meal with id={} of userId={}", mealId, userId);
        Map<Integer, Meal> userMeal = repository.get(userId);
        return userMeal.get(mealId);
    }

    @Override
    public List<Meal> getAll(int userId) {
        log.info("getAll meals of userId={}", userId);
        return getAllSortedByDate(userId);
    }

    @Override
    public List<Meal> getFilteredByDate(int userId, LocalDate startDate, LocalDate endDate) {
        log.info("getFiltered meals of userId={}", userId);
        return getAllSortedByDate(userId).stream()
                .filter(meal -> isBetweenDate(meal.getDate(), startDate, endDate))
                .collect(Collectors.toList());
    }

    public List<Meal> getAllSortedByDate(int userId) {
        Map<Integer, Meal> userMeal = repository.get(userId);
        return userMeal.values().stream()
                .sorted(Comparator.comparing(Meal::getDate, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

}

