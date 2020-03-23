package com.drofff.palindrome.ui.violation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.drofff.palindrome.R;
import com.drofff.palindrome.entity.Violation;
import com.drofff.palindrome.entity.ViolationType;

import java.util.ArrayList;
import java.util.List;

import static com.drofff.palindrome.utils.HttpUtils.postAtUrlWithJsonBody;
import static com.drofff.palindrome.utils.ValidationUtils.validateNotNull;

public class AddViolationFragment extends Fragment {

    private View root;

    private String violationTypeId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_add_violation, container,false);
        Button addViolationButton = root.findViewById(R.id.add_violation_button);
        registerAddViolationListenerAt(addViolationButton);
        return root;
    }

    private List<ViolationType> loadViolationTypes() {
        return new ArrayList<>(); //TODO: load all violation types from backend
    }

    private void registerAddViolationListenerAt(Button button) {
        button.setOnClickListener(view -> sendAddViolationRequest());
    }

    private void sendAddViolationRequest() {
        Violation violation = getInputAsViolation();
        validateViolation(violation);
        String addViolationUrl = getResources().getString(R.string.add_violation_url);
        postAtUrlWithJsonBody(addViolationUrl, violation.toJSONObject());
    }

    private Violation getInputAsViolation() {
       String carNumber = getTextFromViewWithId(R.id.car_number);
       String location = getTextFromViewWithId(R.id.location);
       return new Violation(carNumber, location, violationTypeId);
    }

    private String getTextFromViewWithId(int id) {
        TextView view = root.findViewById(id);
        return view.getText().toString();
    }

    private void validateViolation(Violation violation) {
        validateNotNull(violation.getCarNumber(), "Car number is required");
        validateNotNull(violation.getLocation(), "Location should be provided");
        validateNotNull(violation.getViolationTypeId(), "Violation type should be selected");
    }

}
