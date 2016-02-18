package com.ithinkrok.util.time;

import org.apache.commons.lang.Validate;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

/**
 * Created by paul on 18/02/16.
 */
public class TimeUtils {

    /**
     * @param str The string to parse
     * @return The parsed Instant
     */
    public static Instant addTimeToInstant(String str, Instant start) throws DateTimeParseException {
        Validate.notNull(str, "str cannot be null");
        Validate.notNull(start, "start cannot be null");

        StringBuilder numberPart = new StringBuilder();
        StringBuilder unitPart = new StringBuilder();

        for (int index = 0; index < str.length(); ++index) {
            char c = Character.toLowerCase(str.charAt(index));

            if (Character.isDigit(c)) {
                if (unitPart.length() != 0) {
                    start = addTime(start, numberPart.toString(), unitPart.toString());

                    numberPart = new StringBuilder();
                    unitPart = new StringBuilder();
                }
                numberPart.append(c);
            } else if (Character.isLetter(c)) {
                if (numberPart.length() == 0) {
                    throw new DateTimeParseException("No number before unit", str, index);
                }

                unitPart.append(c);
            }
        }

        if(numberPart.length() > 0) {
            if(unitPart.length() < 0) {
                throw new DateTimeParseException("No unit for final number", str, str.length());
            }

            start = addTime(start, numberPart.toString(), unitPart.toString());
        }

        return start;
    }

    private static Instant addTime(Instant instant, String number, String unit) {
        if (number.isEmpty()) return null;

        long secondsMultiplier;

        switch (unit) {
            case "y":
                secondsMultiplier = ChronoUnit.YEARS.getDuration().getSeconds();
                break;
            case "mo":
                secondsMultiplier = ChronoUnit.MONTHS.getDuration().getSeconds();
                break;
            case "w":
                secondsMultiplier = ChronoUnit.WEEKS.getDuration().getSeconds();
                break;
            case "d":
                secondsMultiplier = ChronoUnit.DAYS.getDuration().getSeconds();
                break;
            case "h":
                secondsMultiplier = 3600L;
                break;
            case "m":
                secondsMultiplier = 60L;
                break;
            case "s":
                secondsMultiplier = 1;
                break;
            default:
                throw new DateTimeParseException("Invalid unit", unit, -1);
        }

        long seconds;
        try {
            seconds = Long.parseLong(number) * secondsMultiplier;
        } catch (NumberFormatException ignored) {
            throw new DateTimeParseException("Invalid number", number, -1);
        }

        return instant.plusSeconds(seconds);
    }
}
