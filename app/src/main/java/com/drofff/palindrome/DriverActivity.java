package com.drofff.palindrome;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.drofff.palindrome.adapter.ArrayViewAdapter;
import com.drofff.palindrome.dto.CarDto;
import com.drofff.palindrome.dto.ViolationDto;
import com.drofff.palindrome.entity.Driver;
import com.drofff.palindrome.listener.DriverDetailsTabListener;
import com.drofff.palindrome.view.holder.strategy.CarDisplayStrategy;
import com.drofff.palindrome.view.holder.strategy.ViolationDisplayStrategy;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;
import static com.bumptech.glide.Glide.with;
import static com.drofff.palindrome.R.id.details_view;
import static com.drofff.palindrome.R.id.driver_full_name_value;
import static com.drofff.palindrome.R.id.driver_image_value;
import static com.drofff.palindrome.R.id.driver_licence_number_value;
import static com.drofff.palindrome.R.id.driver_loader;
import static com.drofff.palindrome.R.id.driver_tabs;
import static com.drofff.palindrome.R.id.no_details_text_view;
import static com.drofff.palindrome.R.layout.car_view;
import static com.drofff.palindrome.R.layout.violation_view;
import static com.drofff.palindrome.R.string.get_driver_cars_url;
import static com.drofff.palindrome.R.string.get_driver_violations_url;
import static com.drofff.palindrome.constants.JsonConstants.LIST_RESPONSE_PAYLOAD_KEY;
import static com.drofff.palindrome.constants.ParameterConstants.DRIVER;
import static com.drofff.palindrome.constants.ParameterConstants.ID;
import static com.drofff.palindrome.constants.ParameterConstants.SEARCH_QUERY;
import static com.drofff.palindrome.utils.FormattingUtils.resolveStringParams;
import static com.drofff.palindrome.utils.HttpUtils.getFromServer;
import static com.drofff.palindrome.utils.JsonUtils.getListFromJsonByKey;
import static com.drofff.palindrome.utils.JsonUtils.parseObjectOfClassFromJson;
import static com.drofff.palindrome.utils.StringUtils.joinNonNullPartsWith;
import static com.drofff.palindrome.utils.UiUtils.isHomeButton;
import static com.drofff.palindrome.utils.ValidationUtils.validateNotNull;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toList;

public class DriverActivity extends AppCompatActivity {

    private static final Executor ACTIVITY_EXECUTOR = Executors.newSingleThreadExecutor();

    private final ArrayViewAdapter<CarDto> carsViewAdapter = new ArrayViewAdapter<>(car_view, CarDisplayStrategy.class);
    private final ArrayViewAdapter<ViolationDto> violationsViewAdapter = new ArrayViewAdapter<>(violation_view, ViolationDisplayStrategy.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        setTitle("");
        enableHomeButton();
        initDetailsView();
        Driver driver = getDriverFromIntent();
        displayDriver(driver);
        loadDriverDetailsAsync(driver);
        initDetailsViewTab();
    }

    private void enableHomeButton() {
        ActionBar actionBar = getSupportActionBar();
        validateNotNull(actionBar, "Action bar is not reachable");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initDetailsView() {
        RecyclerView detailsView = findViewById(details_view);
        detailsView.setHasFixedSize(true);
        detailsView.setLayoutManager(getVerticalLinearLayoutManager());
        detailsView.addItemDecoration(verticalDividerItemDecoration());
        detailsView.setAdapter(carsViewAdapter);
    }

    private LinearLayoutManager getVerticalLinearLayoutManager() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        return linearLayoutManager;
    }

    private RecyclerView.ItemDecoration verticalDividerItemDecoration() {
        return new DividerItemDecoration(this, VERTICAL);
    }

    private Driver getDriverFromIntent() {
        Intent intent = getIntent();
        return (Driver) intent.getSerializableExtra(DRIVER);
    }

    private void displayDriver(Driver driver) {
        String fullName = getDriverFullName(driver);
        setTextToViewWithId(fullName, driver_full_name_value);
        setTextToViewWithId(driver.getLicenceNumber(), driver_licence_number_value);
        ImageView driverImageView = findViewById(driver_image_value);
        with(this).load(driver.getPhotoUrl())
                .into(driverImageView);
    }

    private String getDriverFullName(Driver driver) {
        String firstName = driver.getFirstName();
        String lastName = driver.getLastName();
        String middleName = driver.getMiddleName();
        return joinNonNullPartsWith(" ", firstName, lastName, middleName);
    }

    private void setTextToViewWithId(String text, int viewId) {
        TextView textView = findViewById(viewId);
        textView.setText(text);
    }

    private void loadDriverDetailsAsync(Driver driver) {
        ACTIVITY_EXECUTOR.execute(() -> loadDriverDetails(driver));
    }

    private void loadDriverDetails(Driver driver) {
        runOnUiThread(this::showProgressBar);
        loadDriverCars(driver);
        loadDriverViolations(driver);
        runOnUiThread(this::hideProgressBar);
    }

    private void showProgressBar() {
        ProgressBar progressBar = findViewById(driver_loader);
        progressBar.setVisibility(VISIBLE);
    }

    private void loadDriverCars(Driver driver) {
        String getDriverCarsUrl = getUrlWithDriverIdByResourceId(driver, get_driver_cars_url);
        JSONObject response = getFromServer(getDriverCarsUrl);
        List<CarDto> carDtos = parseListOfClassFromJson(CarDto.class, response);
        runOnUiThread(() -> updateCarsList(carDtos));
    }

    private void updateCarsList(List<CarDto> cars) {
        carsViewAdapter.updateDisplayedElementsList(cars);
        int visibility = cars.isEmpty() ? VISIBLE : INVISIBLE;
        TextView noDetailsView = findViewById(no_details_text_view);
        noDetailsView.setVisibility(visibility);
    }

    private void loadDriverViolations(Driver driver) {
        String getDriverViolationsUrl = getUrlWithDriverIdByResourceId(driver, get_driver_violations_url);
        JSONObject response = getFromServer(getDriverViolationsUrl);
        List<ViolationDto> violationDtos = parseListOfClassFromJson(ViolationDto.class, response);
        runOnUiThread(() -> violationsViewAdapter.updateDisplayedElementsList(violationDtos));
    }

    private String getUrlWithDriverIdByResourceId(Driver driver, int resourceId) {
        String getDriverCarsUrl = getResources().getString(resourceId);
        Map<String, String> driverIdParam = singletonMap(ID, driver.getId());
        return resolveStringParams(getDriverCarsUrl, driverIdParam);
    }

    private <T> List<T> parseListOfClassFromJson(Class<T> clazz, JSONObject jsonObject) {
        return getListFromJsonByKey(jsonObject, LIST_RESPONSE_PAYLOAD_KEY).stream()
                .map(json -> parseObjectOfClassFromJson(clazz, json))
                .collect(toList());
    }

    private void hideProgressBar() {
        ProgressBar progressBar = findViewById(driver_loader);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void initDetailsViewTab() {
        TabLayout detailsViewTab = findViewById(driver_tabs);
        detailsViewTab.addOnTabSelectedListener(getDriverDetailsTabListener());
    }

    private DriverDetailsTabListener getDriverDetailsTabListener() {
        RecyclerView detailsView = findViewById(details_view);
        return new DriverDetailsTabListener(detailsView, carsViewAdapter, violationsViewAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(isHomeButton(item)) {
            redirectToSearchActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void redirectToSearchActivity() {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(SEARCH_QUERY, getSearchQuery());
        startActivity(intent);
    }

    private String getSearchQuery() {
        Intent activityIntent = getIntent();
        String query = activityIntent.getStringExtra(SEARCH_QUERY);
        validateNotNull(query, "Search query should be provided");
        return query;
    }

}
