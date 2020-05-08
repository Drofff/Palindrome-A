package com.drofff.palindrome.view.holder.strategy;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.drofff.palindrome.dto.ViolationDto;
import com.drofff.palindrome.exception.PalindromeException;
import com.drofff.palindrome.type.Displayable;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;
import static com.drofff.palindrome.R.drawable.paid;
import static com.drofff.palindrome.R.id.date_time_value_input;
import static com.drofff.palindrome.R.id.location_value_view;
import static com.drofff.palindrome.R.id.status_value_view;
import static com.drofff.palindrome.R.id.violation_details;
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
        super.setRootView(view);
        context = view.getContext();
        statusIcon = view.findViewById(violation_status_icon);
        violationTypeView = view.findViewById(violation_type_view);
        statusView = view.findViewById(status_value_view);
        locationView = view.findViewById(location_value_view);
        dateTimeView = view.findViewById(date_time_value_input);
        detailsView = view.findViewById(violation_details);
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
        if(violationDto.isPaid()) {
            displayStatusPaid();
        } else {
            displayStatusUnpaid();
        }
        violationTypeView.setText(violationDto.getViolationType());
        locationView.setText(violationDto.getLocation());
        dateTimeView.setText(violationDto.getDateTime());
    }

    private void displayStatusPaid() {
        displayPaidStatusIcon();
        String paidText = context.getResources().getString(paid_text);
        statusView.setText(paidText);
        statusView.setTextColor(GREEN);
    }

    private void displayPaidStatusIcon() {
        Drawable paidIcon = context.getResources().getDrawable(paid, null);
        statusIcon.setImageDrawable(paidIcon);
        ColorStateList iconColorList = ColorStateList.valueOf(GREEN);
        statusIcon.setImageTintList(iconColorList);
    }

    private void displayStatusUnpaid() {
        String unpaidText = context.getResources().getString(unpaid_text);
        statusView.setText(unpaidText);
        statusView.setTextColor(RED);
    }

    @Override
    protected void updateCollapseView(int collapseState) {
        detailsView.setVisibility(collapseState);
    }

}
