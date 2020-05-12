package com.drofff.palindrome.view.holder.strategy;

import android.view.View;
import android.widget.TextView;

import com.drofff.palindrome.dto.CarDto;
import com.drofff.palindrome.exception.PalindromeException;
import com.drofff.palindrome.type.Displayable;

import static com.drofff.palindrome.R.id.body_type_value;
import static com.drofff.palindrome.R.id.car_details;
import static com.drofff.palindrome.R.id.car_model;
import static com.drofff.palindrome.R.id.engine_value;
import static com.drofff.palindrome.utils.StringUtils.joinNonNullPartsWith;
import static com.drofff.palindrome.utils.UiUtils.putMappedTextViewValuesIntoView;

public class CarDisplayStrategy extends CollapseDisplayStrategy {

    @Override
    public void display(Displayable displayable) {
        CarDto carDto = toCarDto(displayable);
        putMappedTextViewValuesIntoView(carDto, getRootView());
        displayLabelOfCar(carDto);
        displayBodyTypeOfCar(carDto);
        displayEngineOfCar(carDto);
    }

    private CarDto toCarDto(Displayable displayable) {
        if(isNotCarDto(displayable)) {
            throw new PalindromeException("Only CarDto instance can be displayed by this implementation");
        }
        return (CarDto) displayable;
    }

    private boolean isNotCarDto(Displayable displayable) {
        return !isCarDto(displayable);
    }

    private boolean isCarDto(Displayable displayable) {
        return displayable instanceof CarDto;
    }

    private void displayLabelOfCar(CarDto carDto) {
        String carLabel = getCarLabel(carDto);
        setTextIntoViewWithId(carLabel, car_model);
    }

    private String getCarLabel(CarDto carDto) {
        String brandName = carDto.getBrand().getName();
        String model = carDto.getModel();
        return joinNonNullPartsWith(" ", brandName, model);
    }

    private void displayBodyTypeOfCar(CarDto carDto) {
        String bodyType = getCarBodyType(carDto);
        setTextIntoViewWithId(bodyType, body_type_value);
    }

    private String getCarBodyType(CarDto carDto) {
        String bodyTypeName = carDto.getBodyType().getName();
        String licenceCategoryName = carDto.getLicenceCategory().getName();
        return bodyTypeName + " (" + licenceCategoryName + ")";
    }

    private void displayEngineOfCar(CarDto carDto) {
        String carEngine = getCarEngine(carDto);
        setTextIntoViewWithId(carEngine, engine_value);
    }

    private String getCarEngine(CarDto carDto) {
        String engineTypeName = carDto.getEngineType().getName();
        String engineVolume = carDto.getEngineVolume().toString();
        return joinNonNullPartsWith(" ", engineTypeName, engineVolume);
    }

    private void setTextIntoViewWithId(String text, int id) {
        TextView view = getRootView().findViewById(id);
        view.setText(text);
    }

    @Override
    protected void updateCollapseView(int collapseState) {
        View detailsView = getRootView().findViewById(car_details);
        detailsView.setVisibility(collapseState);
    }

}
