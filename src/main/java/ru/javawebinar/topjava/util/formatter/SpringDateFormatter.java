package ru.javawebinar.topjava.util.formatter;

import java.time.LocalDate;
import java.util.Locale;

public final class SpringDateFormatter extends AbstractSpringFormatter<LocalDate> {

    public SpringDateFormatter(String pattern) {
        super(pattern);
    }

    public LocalDate parse(String formatted, Locale locale) {
        if (formatted.length() == 0) {
            return null;
        }
        return LocalDate.parse(formatted, getDateFormat(locale));
    }
}

