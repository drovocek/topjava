package ru.javawebinar.topjava.web.meal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;

@Controller("/meals")
public class JspMealController extends AbstractMealController {

    @PostMapping(value = "/meals", params = {"id", "dateTime", "description", "calories"})
    public String saveMeal(
            @RequestParam(name = "id", required = false) Integer id,
            @RequestParam(name = "dateTime") String dateTime,
            @RequestParam(name = "description") String description,
            @RequestParam(name = "calories") Integer calories
    ) {
        Meal meal = new Meal(LocalDateTime.parse(dateTime), description, calories);
        meal.setId(id);

        if (id == null) {
            create(meal);
        } else {
            update(meal, id);
        }

        return "redirect:meals";
    }

    @PostMapping(value = "/meals")
    public String getMealForm(
            @RequestParam(name = "id", required = false) Integer id,
            Model model
    ) {
        Meal meal = (id == null) ? new Meal() : get(id);

        model.addAttribute("meal", meal);
        return "mealForm";
    }

    @GetMapping(value = "/meals", params = "id")
    public String deleteMeal(@RequestParam(name = "id") Integer id) {
        delete(id);
        return "redirect:meals";
    }

    @GetMapping(value = "/meals", params = {"startDate", "endDate", "startTime", "endTime"})
    public String filterMeals(
            @RequestParam(name = "startDate") String startFilterDate,
            @RequestParam(name = "endDate") String endFilterDate,
            @RequestParam(name = "startTime") String startFilterTime,
            @RequestParam(name = "endTime") String endFilterTime,
            Model model
    ) {
        LocalDate startDate = parseLocalDate(startFilterDate);
        LocalDate endDate = parseLocalDate(endFilterDate);
        LocalTime startTime = parseLocalTime(startFilterTime);
        LocalTime endTime = parseLocalTime(endFilterTime);

        List<MealTo> meals = getBetween(startDate, startTime, endDate, endTime);

        model.addAttribute("meals", meals);
        return "meals";
    }

    @GetMapping(value = "/meals")
    public String getMeals(Model model) {
        model.addAttribute("meals", getAll());
        return "meals";
    }
}
