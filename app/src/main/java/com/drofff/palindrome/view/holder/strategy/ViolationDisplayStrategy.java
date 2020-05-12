package com.drofff.palindrome.view.holder.strategy;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.drofff.palindrome.dto.ViolationDto;
import com.drofff.palindrome.entity.ViolationType;
import com.drofff.palindrome.exception.PalindromeException;
import com.drofff.palindrome.type.Displayable;

import static android.graphics.Color.RED;
import static com.drofff.palindrome.R.color.success;
import static com.drofff.palindrome.R.drawable.paid;
import static com.drofff.palindrome.R.id.date_time_value_input;
import static com.drofff.palindrome.R.id.location_value_view;
import static com.drofff.palindrome.R.id.status_value_view;
import static com.drofff.palindrome.R.id.violation_details_view;
import static com.drofff.palindrome.R.id.violation_status_icon;
import static com.drofff.palindrome.R.id.violation_type_view;
import static com.drofff.palindrome.R.string.paid_text;
import static com.drofff.palindrome.R.string.unpaid_text;

public class ViolationDisplayStrategy extends CollapseDisplayStrategy {

    private Context context;

    private ImageView statusIcon;
    private TextView violationTypeView;

    private TextView statusView;
    private TextView locationView;
    private TextView dateTimeView;

    private View detailsView;

    @Override
    public void setRootView(View view) {
        context = view.getContext();
        statusIcon = view.findViewById(violation_status_icon);
        violationTypeView = view.findViewById(violation_type_view);
        statusView = view.findViewById(status_value_view);
        locationView = view.findViewById(location_value_view);
        dateTimeView = view.findViewById(date_time_value_input);
        detailsView = view.findViewById(violation_details_view);
        super.setRootView(view); // should have detailsView set
    }

    @Override
    public void display(Displayable displayable) {
        ViolationDto violationDto = toViolationDto(displayable);
        displayViolationDto(violationDto);
    }

    private ViolationDto toViolationDto(Displayable displayable) {
        if(isNotViolationDto(displayable)) {
            throw new PalindromeException("Only ViolationDto instance can be displayed by this strategy");
        }
        return (ViolationDto) displayable;
    }

    private boolean isNotViolationDto(Displayable displayable) {
        return !isViolationDto(displayable);
    }

    private boolean isViolationDto(Displayable displayable) {
        return displayable instanceof ViolationDto;
    }

    private void displayViolationDto(ViolationDto violationDto) {
        displayViolationStatus(violationDto);
        String violationTypeName = getViolationTypeName(violationDto);
        violationTypeView.setText(violationTypeName);
        locationView.setText(violationDto.getLocation());
        dateTimeView.setText(violationDto.getDateTime());
    }

    private void displayViolationStatus(ViolationDto violationDto) {
        if(violationDto.isPaid()) {
            displayStatusPaid();
        } else {
            displayStatusUnpaid();
        }
    }

    private void displayStatusPaid() {
        displayPaidStatusIcon();
        String paidText = context.getResources().getString(paid_text);
        statusView.setText(paidText);
        int colorSuccess = getColorSuccess();
        statusView.setTextColor(colorSuccess);
    }

    private void displayPaidStatusIcon() {
        Drawable paidIcon = context.getResources().getDrawable(paid, null);
        statusIcon.setImageDrawable(paidIcon);
        int colorSuccess = getColorSuccess();
        ColorStateList iconColorList = ColorStateList.valueOf(colorSuccess);
        statusIcon.setImageTintList(iconColorList);
    }

    private int getColorSuccess() {
        return context.getResources()
                .getColor(success, null);
    }

    private void displayStatusUnpaid() {
        String unpaidText = context.getResources().getString(unpaid_text);
        statusView.setText(unpaidText);
        statusView.setTextColor(RED);
    }

    private String getViolationTypeName(ViolationDto violationDto) {
        ViolationType violationType = violationDto.getViolationType();
        return violationType.getName();
    }

    @Override
    protected void updateCollapseView(int collapseState) {
        detailsView.setVisibility(collapseState);
    }

}
