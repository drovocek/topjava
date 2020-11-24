package ru.javawebinar.topjava.web.meal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.exception.NotFoundException;
import ru.javawebinar.topjava.web.AbstractControllerTest;
import ru.javawebinar.topjava.web.json.JsonUtil;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.TestUtil.readFromJson;
import static ru.javawebinar.topjava.UserTestData.USER_ID;
import static ru.javawebinar.topjava.UserTestData.user;

class MealRestControllerTest extends AbstractControllerTest {

    private static final String REST_URL = MealRestController.REST_URL + '/';
    private static final String URI_DATA_PARAM_MODEL = "?startDate=%s&startTime=%s&endDate=%s&endTime=%s";

    @Autowired
    private MealService mealService;

    @Test
    void getOne() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + MEAL1_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEAL_MATCHER.contentJson(meal1));
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

        Meal created = readFromJson(action, Meal.class);
        int newId = created.getId();
        newMeal.setId(newId);

        MEAL_MATCHER.assertMatch(created, newMeal);
        MEAL_MATCHER.assertMatch(mealService.get(newId, USER_ID), newMeal);
    }

    @Test
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEAL_TO_MATCHER.contentJson(
                        MealsUtil.getTos(
                                meals,
                                user.getCaloriesPerDay())
                        )
                );
    }

    @Test
    void getBetweenInclusive() throws Exception {
        String startDate = "2020-01-30";
        String endDate = startDate;

        String uriDataParamString = String.format(
                URI_DATA_PARAM_MODEL,
                startDate, "",
                endDate, "");

        getBetween(uriDataParamString, List.of(meal3, meal2, meal1));
    }

    @Test
    void getBetweenWithNullDates() throws Exception {
        String startDate = "";
        String endDate = startDate;

        String uriDataParamString = String.format(
                URI_DATA_PARAM_MODEL,
                startDate, "",
                endDate, "");

        getBetween(uriDataParamString, meals);
    }

    @Test
    void getBetweenTime() throws Exception {
        String startDate = "2020-01-30";
        String startTime = "00:00:00";
        String endDate = startDate;
        String endTime = "12:00:00";

        String uriDataParamString = String.format(
                URI_DATA_PARAM_MODEL,
                startDate, startTime,
                endDate, endTime);

        getBetween(uriDataParamString, List.of(meal1));
    }

    private void getBetween(String uriDataParamString, List<Meal> expectedResponseMeals) throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "filter" + uriDataParamString))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEAL_TO_MATCHER.contentJson(
                        MealsUtil.getTos(
                                expectedResponseMeals,
                                user.getCaloriesPerDay())
                        )
                );
    }
}