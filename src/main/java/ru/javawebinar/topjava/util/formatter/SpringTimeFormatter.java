package ru.javawebinar.topjava.util.formatter;

import java.time.LocalTime;
import java.util.Locale;

public final class SpringTimeFormatter extends AbstractSpringFormatter<LocalTime> {

    public SpringTimeFormatter(String pattern) {
        super(pattern);
    }

    public LocalTime parse(String formatted, Locale locale) {
        if (formatted.length() == 0) {
            return null;
        }
        return LocalTime.parse(formatted, getDateFormat(locale));
    }
}
