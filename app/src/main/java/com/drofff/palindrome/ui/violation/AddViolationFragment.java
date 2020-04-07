package com.drofff.palindrome.ui.violation;

import android.app.Activity;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.drofff.palindrome.R;
import com.drofff.palindrome.adapter.ViolationTypesSpinnerAdapter;
import com.drofff.palindrome.entity.Violation;
import com.drofff.palindrome.entity.ViolationType;
import com.drofff.palindrome.exception.PalindromeException;
import com.drofff.palindrome.exception.RequestException;
import com.drofff.palindrome.exception.ValidationException;
import com.drofff.palindrome.service.HttpLocationResolver;
import com.drofff.palindrome.service.LocationResolver;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.Context.LOCATION_SERVICE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static androidx.core.content.ContextCompat.checkSelfPermission;
import static com.drofff.palindrome.constants.JsonConstants.LIST_RESPONSE_PAYLOAD_KEY;
import static com.drofff.palindrome.utils.HttpUtils.getFromServer;
import static com.drofff.palindrome.utils.HttpUtils.postToServerWithJsonBody;
import static com.drofff.palindrome.utils.JsonUtils.getListFromJsonByKey;
import static com.drofff.palindrome.utils.ValidationUtils.validateNotBlank;
import static com.google.android.material.snackbar.Snackbar.LENGTH_LONG;

public class AddViolationFragment extends Fragment {

    private static final Executor ADD_VIOLATION_EXECUTOR = Executors.newFixedThreadPool(2);

    private static final int LOCATION_PERMISSION_CODE = 33;

    private LocationResolver locationResolver;

    private View root;

    private Spinner violationTypeSpinner;
    private EditText locationInput;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_add_violation, container,false);
        initDependentServices();
        violationTypeSpinner = root.findViewById(R.id.violation_types_spinner);
        ADD_VIOLATION_EXECUTOR.execute(() -> fillSpinnerWithViolationTypes(violationTypeSpinner));
        locationInput = root.findViewById(R.id.location);
        Button readLocationButton = root.findViewById(R.id.geo_button);
        registerReadLocationListenerAt(readLocationButton);
        Button addViolationButton = root.findViewById(R.id.add_violation_button);
        registerAddViolationListenerAt(addViolationButton);
        return root;
    }

    private void initDependentServices() {
        String getLocationUrl = getResources().getString(R.string.get_location_url);
        locationResolver = new HttpLocationResolver(getLocationUrl);
    }

    private void fillSpinnerWithViolationTypes(Spinner spinner) {
        List<ViolationType> violationTypes = loadViolationTypes();
        ViolationTypesSpinnerAdapter adapter = makeViolationTypesSpinnerAdapter(violationTypes);
        getParentActivity().runOnUiThread(() -> spinner.setAdapter(adapter));
    }

    private List<ViolationType> loadViolationTypes() {
        String violationTypesUrl = getResources().getString(R.string.violation_types_url);
        JSONObject violationTypesJson = getFromServer(violationTypesUrl);
        List<JSONObject> violationTypeJsonList = getListFromJsonByKey(violationTypesJson, LIST_RESPONSE_PAYLOAD_KEY);
        return violationTypeJsonList.stream()
                .map(ViolationType::fromJSONObject)
                .collect(Collectors.toList());
    }

    private ViolationTypesSpinnerAdapter makeViolationTypesSpinnerAdapter(List<ViolationType> violationTypes) {
        Activity activity = getParentActivity();
        return new ViolationTypesSpinnerAdapter(activity, violationTypes);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(isLocationPermissionRequest(requestCode)) {
            if(permissionGranted(grantResults)) {
                requestUserLocationAsync();
            } else {
                displayMessage("Доступ до геоданих необхідний для зчитки місцезнаходження", locationInput);
            }
        }
    }

    private boolean isLocationPermissionRequest(int requestCode) {
        return requestCode == LOCATION_PERMISSION_CODE;
    }

    private boolean permissionGranted(int[] grantResults) {
        return grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED;
    }

    private void registerReadLocationListenerAt(Button button) {
        button.setOnClickListener(view -> requestUserLocationAsync());
    }

    private void requestUserLocationAsync() {
        ADD_VIOLATION_EXECUTOR.execute(this::requestUserLocation);
    }

    private void requestUserLocation() {
        if(isPermittedToReadLocation()) {
            readLocation();
        } else {
            requestLocationPermission();
        }
    }

    private boolean isPermittedToReadLocation() {
        return isFineLocationPermitted() && isCoarseLocationPermitted();
    }

    private boolean isFineLocationPermitted() {
        return hasPermission(ACCESS_FINE_LOCATION);
    }

    private boolean isCoarseLocationPermitted() {
        return hasPermission(ACCESS_COARSE_LOCATION);
    }

    private boolean hasPermission(String permission) {
        Activity activity = getParentActivity();
        return checkSelfPermission(activity, permission) == PERMISSION_GRANTED;
    }

    private void readLocation() throws SecurityException {
        Activity activity = getParentActivity();
        activity.runOnUiThread(this::showProgressBar);
        try {
            Location location = getCurrentLocation();
            String resolvedLocation = locationResolver.resolveLocation(location);
            activity.runOnUiThread(() -> locationInput.setText(resolvedLocation));
        } catch(PalindromeException e) {
            activity.runOnUiThread(() -> displayMessage(e.getMessage(), locationInput));
        } finally {
            activity.runOnUiThread(this::hideProgressBar);
        }
    }

    private void showProgressBar() {
        ProgressBar progressBar = root.findViewById(R.id.add_violation_loader);
        progressBar.setVisibility(VISIBLE);
    }

    private Location getCurrentLocation() throws SecurityException {
        LocationManager locationManager = getLocationManager();
        validateGpsProviderEnabled(locationManager);
        Location gpsLocation = getGPSLocation(locationManager);
        if(gpsLocation == null) {
            Location networkLocation = getNetworkLocation(locationManager);
            if(networkLocation == null) {
                throw new PalindromeException("Неможливо визначити місцезнаходження");
            }
            return networkLocation;
        }
        return gpsLocation;
    }

    private LocationManager getLocationManager() {
        Activity activity = getParentActivity();
        return (LocationManager) activity.getSystemService(LOCATION_SERVICE);
    }

    private void validateGpsProviderEnabled(LocationManager locationManager) {
        if(isGpsProviderDisabled(locationManager)) {
            throw new PalindromeException("Увімкніть GPS для зчитки місцеположення");
        }
    }

    private boolean isGpsProviderDisabled(LocationManager locationManager) {
        return !locationManager.isProviderEnabled(GPS_PROVIDER);
    }

    private Location getGPSLocation(LocationManager locationManager) throws SecurityException {
        return locationManager.getLastKnownLocation(GPS_PROVIDER);
    }

    private Location getNetworkLocation(LocationManager locationManager) throws SecurityException {
        return locationManager.getLastKnownLocation(NETWORK_PROVIDER);
    }

    private void hideProgressBar() {
        ProgressBar progressBar = root.findViewById(R.id.add_violation_loader);
        progressBar.setVisibility(INVISIBLE);
    }

    private void requestLocationPermission() {
        Activity activity = getParentActivity();
        String[] permissions = { ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION };
        ActivityCompat.requestPermissions(activity, permissions, LOCATION_PERMISSION_CODE);
    }

    private Activity getParentActivity() {
        return Optional.ofNullable(getActivity())
                .orElseThrow(() -> new PalindromeException("Missing parent activity"));
    }

    private void registerAddViolationListenerAt(Button button) {
        button.setOnClickListener(this::addViolation);
    }

    private void addViolation(View view) {
        try {
            sendAddViolationRequest();
        } catch(ValidationException e) {
            displayMessage(e.getMessage(), view);
        }
    }

    private void sendAddViolationRequest() {
        Violation violation = getInputAsViolation();
        validateViolation(violation);
        String addViolationUrl = getResources().getString(R.string.add_violation_url);
        ADD_VIOLATION_EXECUTOR.execute(() -> createViolation(addViolationUrl, violation));
    }

    private Violation getInputAsViolation() {
       String carNumber = getTextFromViewWithId(R.id.car_number);
       String location = getTextFromViewWithId(R.id.location);
       String violationTypeId = getIdOfSelectedViolationType();
       return new Violation(carNumber, location, violationTypeId);
    }

    private String getTextFromViewWithId(int id) {
        TextView view = root.findViewById(id);
        return view.getText().toString();
    }

    private String getIdOfSelectedViolationType() {
        ViolationType violationType = (ViolationType) violationTypeSpinner.getSelectedItem();
        return violationType.getId();
    }

    private void validateViolation(Violation violation) {
        validateNotBlank(violation.getCarNumber(), "Car number is required");
        validateNotBlank(violation.getLocation(), "Location should be provided");
        validateNotBlank(violation.getViolationTypeId(), "Violation type should be selected");
    }

    private void createViolation(String addViolationUrl, Violation violation) {
        try {
            postToServerWithJsonBody(addViolationUrl, violation.toJSONObject());
        } catch(RequestException e) {
            throw new ValidationException("Car with such number doesn't exist");
        }
    }

    private void displayMessage(String message, View view) {
        Snackbar snackbar = Snackbar.make(view, message, LENGTH_LONG);
        snackbar.show();
    }

}
