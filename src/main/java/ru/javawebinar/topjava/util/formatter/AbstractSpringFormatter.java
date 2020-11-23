package ru.javawebinar.topjava.util.formatter;

import org.springframework.format.Formatter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

abstract class AbstractSpringFormatter<T> implements Formatter<T> {

    private String pattern;

    public AbstractSpringFormatter(String pattern) {
        this.pattern = pattern;
    }

    public String print(T temporalUnit, Locale locale) {
        if (temporalUnit == null) {
            return "";
        }
        return getDateFormat(locale).format((TemporalAccessor) temporalUnit);
    }

    protected DateTimeFormatter getDateFormat(Locale locale) {
        return DateTimeFormatter.ofPattern(this.pattern, locale);
    }

    private LocalDate parseToLocalDate(String formatted, Locale locale) {
        return LocalDate.parse(formatted, getDateFormat(locale));
    }

    private LocalTime parseToLocalTime(String formatted, Locale locale) {
        return LocalTime.parse(formatted, getDateFormat(locale));
    }
}
