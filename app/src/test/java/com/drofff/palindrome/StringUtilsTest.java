package com.drofff.palindrome;

import com.drofff.palindrome.utils.StringUtils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringUtilsTest {

    @Test
    public void asHexStrTest() {
        String expectedHexStr = "8a";
        int octet = 0x8a;
        String resultHexStr = StringUtils.asHexStr(octet);
        assertEquals(expectedHexStr, resultHexStr);
    }

}
