package ru.javawebinar.topjava.web.meal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.exception.NotFoundException;
import ru.javawebinar.topjava.web.AbstractControllerTest;
import ru.javawebinar.topjava.web.SecurityUtil;
import ru.javawebinar.topjava.web.json.JsonUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.TestUtil.readFromJson;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

class MealRestControllerTest extends AbstractControllerTest {

    private static final String REST_URL = MealRestController.REST_URL + '/';

    @Autowired
    private MealService mealService;

    @Test
    void getOne() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + MEAL1_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEAL_TO_MATCHER.contentJson(createTestTo(meal1)));
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + MEAL1_ID))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> mealService.get(MEAL1_ID, USER_ID));
    }

    @Test
    void update() throws Exception {
        Meal updated = MealTestData.getUpdated();
        perform(MockMvcRequestBuilders.put(REST_URL + updated.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isNoContent());

        MEAL_MATCHER.assertMatch(mealService.get(MEAL1_ID, USER_ID), updated);
    }

    @Test
    void createWithLocation() throws Exception {
        Meal newMeal = MealTestData.getNew();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newMeal)))
                .andExpect(status().isCreated());

        MealTo created = readFromJson(action, MealTo.class);
        int newId = created.getId();
        newMeal.setId(newId);
        MEAL_TO_MATCHER.assertMatch(created, createTestTo(newMeal));
        MEAL_TO_MATCHER.assertMatch(createTestTo(mealService.get(newId, USER_ID)), createTestTo(newMeal));
    }

    @Test
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEAL_TO_MATCHER.contentJson(
                        MealsUtil.getTos(
                                meals,
                                SecurityUtil.authUserCaloriesPerDay())
                        )
                );
    }

    @Test
    void getBetween() throws Exception {
        LocalDate startDate = LocalDate.of(2020, 01, 30);
        LocalDate endDate = startDate;
        LocalTime startDayTime = LocalTime.MIN;
        LocalTime endDayTime = LocalTime.MAX;
        LocalDateTime startDateTime = LocalDateTime.of(startDate, startDayTime);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, endDayTime);

        String uriDataParamString = getUriDataParamString(startDateTime, endDateTime);

        getBetween(uriDataParamString, List.of(meal3, meal2, meal1));
    }

    private void getBetween(String uriDataParamString, List<Meal> expectedResponseMeals) throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "filter" + uriDataParamString))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEAL_TO_MATCHER.contentJson(
                        MealsUtil.getTos(
                                expectedResponseMeals,
                                SecurityUtil.authUserCaloriesPerDay())
                        )
                );
    }

    private String getUriDataParamString(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return new StringBuilder("?")
                .append("startDateTime=")
                .append(startDateTime)
                .append("&")
                .append("endDateTime=")
                .append(endDateTime)
                .toString();
    }
}