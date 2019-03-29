package com.afterpay.util;

import org.apache.commons.lang.time.DateUtils;
import org.joda.money.Money;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import static org.joda.money.CurrencyUnit.USD;

/**
 * Provide utility methods to support conversion between data formats
 * Required by transaction validators for easy and accurate data comparison
 */
public class Util {

    private static DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    private static String SPACE = " ";

    public static DateTime stringToDate(String date) throws Exception {
        if(null == date) throw new Exception("Date conversion failed, input date is null");

        //the input date string is formatted to be able to pull the 'day' for comparison.
        date = date.replaceFirst("T", SPACE).trim();

        //Get formatted String
        return DATE_FORMATTER.parseDateTime(date);
    }

    public static Money stringToMoney(String money) throws Exception{
        if(null == money) Money.zero(USD);

        //default Money formatter is 'dollars.cents' upto 2 decimal.
        //For example, {@code parse("USD 25")} creates the instance {@code USD 25.00}
        return Money.parse(USD + SPACE + money);
    }

    public static boolean isSameDay(DateTime date1, DateTime date2) {
        if(null == date1 && null == date2) return true;
        if(null == date1 || null == date2) return false;

        //To compare if its the same day, we check the date portion and ignore the timestamp value.
        return DateUtils.isSameDay(date1.toDate(), date2.toDate());
    }

}
