package com.drofff.palindrome;

import com.drofff.palindrome.utils.DateUtils;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class DateUtilsTest {

    @Test
    public void dateOfTest() {
        Integer[] testDateArray = { 2020, 12, 5 };
        Date expectedDate = new Date(120, 11, 5);
        Date resultDate = DateUtils.dateOf(testDateArray);
        assertEquals(expectedDate.getTime(), resultDate.getTime());
    }

    @Test
    public void dateOfRoundTest() {
        Integer[] testDateArray = { 2020, 12, 5, 6 };
        Date expectedDate = new Date(120, 11, 5);
        Date resultDate = DateUtils.dateOf(testDateArray);
        assertEquals(expectedDate.getTime(), resultDate.getTime());
    }

}
