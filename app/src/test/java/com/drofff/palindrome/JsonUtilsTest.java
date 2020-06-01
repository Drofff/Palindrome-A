package com.drofff.palindrome;

import com.drofff.palindrome.dto.CarDto;
import com.drofff.palindrome.dto.ViolationDto;
import com.drofff.palindrome.entity.Violation;
import com.drofff.palindrome.entity.ViolationType;
import com.drofff.palindrome.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class JsonUtilsTest {

    @Test
    public void parseObjectOfClassFromJsonTest() {
        String testLocation = UUID.randomUUID().toString();
        Violation testViolation = new Violation();
        testViolation.setLocation(testLocation);

        JSONObject jsonObject = (JSONObject) JSONObject.wrap(testViolation);

        Violation resultObject = JsonUtils.parseObjectOfClassFromJson(Violation.class, jsonObject);

        assertEquals(testLocation, resultObject.getLocation());
    }

    @Test
    public void parseObjectOfClassFromJsonNestedTest() {
        String violationTypeName = UUID.randomUUID().toString();
        ViolationType testViolationType = getViolationTypeWithName(violationTypeName);
        ViolationDto testViolation = new ViolationDto();
        testViolation.setViolationType(testViolationType);

        JSONObject jsonObject = (JSONObject) JSONObject.wrap(testViolation);

        ViolationDto resultViolation = JsonUtils.parseObjectOfClassFromJson(ViolationDto.class, jsonObject);

        String resultViolationTypeName = resultViolation.getViolationType().getName();
        assertEquals(violationTypeName, resultViolationTypeName);
    }

    private ViolationType getViolationTypeWithName(String name) {
        ViolationType violationType = new ViolationType();
        String id = UUID.randomUUID().toString();
        violationType.setId(id);
        violationType.setName(name);
        return violationType;
    }

    @Test
    public void parseObjectOfClassFromJsonNestedNullTest() {
        ViolationDto testViolation = new ViolationDto();
        String testLocation = UUID.randomUUID().toString();
        testViolation.setLocation(testLocation);

        JSONObject jsonObject = (JSONObject) JSONObject.wrap(testViolation);

        ViolationDto violationDto = JsonUtils.parseObjectOfClassFromJson(ViolationDto.class, jsonObject);

        assertNotNull(violationDto.getLocation());
        assertNull(violationDto.getViolationType());
    }

    @Test
    public void parseObjectOfClassFromJsonDateTimeTest() throws JSONException {
        Integer[] dateArray = { 2020, 12, 12 };
        JSONArray testDateTime = new JSONArray(dateArray);
        JSONObject testJson = new JSONObject();
        testJson.put("dateTime", testDateTime);

        ViolationDto resultViolation = JsonUtils.parseObjectOfClassFromJson(ViolationDto.class, testJson);

        Date expectedDate = new Date(120, 11, 12);
        assertEquals(dateToStr(expectedDate), resultViolation.getDateTime());
    }

    private String dateToStr(Date date) {
        return new SimpleDateFormat("kk:mm dd.MM.yyyy")
                .format(date);
    }

    @Test
    public void parseObjectOfClassFromJsonNumericTest() {
        Double expectedEngineVolume = 44.;
        CarDto testCar = new CarDto();
        testCar.setEngineVolume(expectedEngineVolume);

        JSONObject jsonObject = (JSONObject) JSONObject.wrap(testCar);

        CarDto resultCar = JsonUtils.parseObjectOfClassFromJson(CarDto.class, jsonObject);

        assertEquals(expectedEngineVolume, resultCar.getEngineVolume());
    }

}
