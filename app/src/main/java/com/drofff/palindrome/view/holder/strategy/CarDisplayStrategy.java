package com.drofff.palindrome.view.holder.strategy;

import android.view.View;
import android.widget.TextView;

import com.drofff.palindrome.dto.CarDto;
import com.drofff.palindrome.exception.PalindromeException;
import com.drofff.palindrome.type.Displayable;

import static com.drofff.palindrome.R.id.body_type_value;
import static com.drofff.palindrome.R.id.car_details;
import static com.drofff.palindrome.utils.UiUtils.putMappedTextViewValuesIntoView;

public class CarDisplayStrategy extends CollapseDisplayStrategy {

    @Override
    public void display(Displayable displayable) {
        CarDto carDto = toCarDto(displayable);
        putMappedTextViewValuesIntoView(carDto, getRootView());
        displayBodyTypeOfCar(carDto);
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

    private void displayBodyTypeOfCar(CarDto carDto) {
        String bodyType = getCarBodyType(carDto);
        TextView bodyTypeView = getRootView().findViewById(body_type_value);
        bodyTypeView.setText(bodyType);
    }

    private String getCarBodyType(CarDto carDto) {
        return carDto.getBodyType() + " (" + carDto.getLicenceCategory() + ")";
    }

    @Override
    protected void updateCollapseView(int collapseState) {
        View detailsView = getRootView().findViewById(car_details);
        detailsView.setVisibility(collapseState);
    }

}
