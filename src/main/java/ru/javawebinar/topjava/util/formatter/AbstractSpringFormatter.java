package ru.javawebinar.topjava.util.formatter;

import org.springframework.format.Formatter;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

abstract class AbstractSpringFormatter<T> implements Formatter<T> {

    private String pattern;
    private DateTimeFormatter dateTimeFormatter;

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
        if (dateTimeFormatter == null) {
            synchronized (AbstractSpringFormatter.class) {
                if (dateTimeFormatter == null) {
                    dateTimeFormatter = DateTimeFormatter.ofPattern(pattern, locale);
                }
            }
        }
        return dateTimeFormatter;
    }
}
