package ru.javawebinar.topjava.web.meal;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RequestMapping(value = "/meals")
@Controller
public class JspMealController extends AbstractMealController {

    @PostMapping(params = {"id", "dateTime", "description", "calories"})
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

    @GetMapping(value = "/mealform")
    public String getMealForm(
            @RequestParam(name = "id", required = false) Integer id,
            Model model
    ) {
        Meal meal = (id == null) ? new Meal() : get(id);
        model.addAttribute("meal", meal);
        return "mealForm";
    }

    @GetMapping(params = "id")
    public String deleteMeal(@RequestParam(name = "id") Integer id) {
        delete(id);
        return "redirect:meals";
    }

    @GetMapping(params = {"startDate", "endDate", "startTime", "endTime"})
    public String filterMeals(
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(name = "startTime", required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime startTime,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(name = "endTime", required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime endTime,
            Model model
    ) {
        List<MealTo> meals = getBetween(startDate, startTime, endDate, endTime);
        model.addAttribute("meals", meals);
        return "meals";
    }

    @GetMapping
    public String getMeals(Model model) {
        model.addAttribute("meals", getAll());
        return "meals";
    }
}
