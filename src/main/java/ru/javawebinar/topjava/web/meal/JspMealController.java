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
import java.time.temporal.ChronoUnit;
import java.util.List;

@RequestMapping(value = "/meals")
@Controller
public class JspMealController extends AbstractMealController {

    @PostMapping(value = "/save")
    public String save(
            @RequestParam(name = "id", required = false) Integer id,
            @RequestParam(name = "dateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime,
            @RequestParam(name = "description") String description,
            @RequestParam(name = "calories") Integer calories
    ) {
        Meal meal = new Meal(dateTime, description, calories);
        meal.setId(id);

        if (meal.isNew()) {
            create(meal);
        } else {
            update(meal, id);
        }

        return "redirect:/meals";
    }

    @GetMapping(value = "/mealform")
    public String getForm(
            @RequestParam(name = "id", required = false) Integer id,
            Model model
    ) {
        Meal meal = (id == null) ?
                new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000) :
                get(id);
        model.addAttribute("meal", meal);
        return "mealForm";
    }

    @GetMapping(value = "/delete")
    public String erase(@RequestParam(name = "id") Integer id) {
        delete(id);
        return "redirect:/meals";
    }

    @GetMapping(value = "/filter")
    public String filter(
            @RequestParam(name = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "startTime", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam(name = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(name = "endTime", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
            Model model
    ) {
        List<MealTo> meals = getBetween(startDate, startTime, endDate, endTime);
        model.addAttribute("meals", meals);
        return "/meals";
    }

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("meals", getAll());
        return "meals";
    }
}
