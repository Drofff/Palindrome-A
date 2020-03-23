package com.drofff.palindrome;

import com.drofff.palindrome.dto.UserDto;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JSONObjectTest {

    @Test
    public void userDtoToJSONObjectTest() throws JSONException {
        String testUsernameValue = "admin";
        UserDto userDto = new UserDto(testUsernameValue, "123");
        JSONObject jsonObject = (JSONObject) JSONObject.wrap(userDto);
        assertNotNull(jsonObject);
        String jsonUsernameValue = jsonObject.getString("username");
        assertEquals(jsonUsernameValue, testUsernameValue);
    }

}
