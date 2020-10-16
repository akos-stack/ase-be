package com.bloxico.userservice.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateUtil {

    //TODO I will only use LocalDateTime for this situation until we make sure that LocalDateTime will be used instead of Date for all situations
    public static Date addMinutesToGivenDate(LocalDateTime date, int minutes) {

        LocalDateTime newLocalDateTime = date.plusMinutes(minutes);

        return Date.from(newLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date addDaysToGivenDate(LocalDateTime date, int days) {

        LocalDateTime newLocalDateTime = date.plusDays(days);

        return Date.from(newLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
