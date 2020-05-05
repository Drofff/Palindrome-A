package com.drofff.palindrome;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.drofff.palindrome.adapter.DriversViewAdapter;
import com.drofff.palindrome.entity.Driver;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static androidx.recyclerview.widget.LinearLayoutManager.VERTICAL;
import static com.drofff.palindrome.R.id.drivers_view;
import static com.drofff.palindrome.R.id.search_spinner;
import static com.drofff.palindrome.R.string.find_drivers_url;
import static com.drofff.palindrome.constants.JsonConstants.LIST_RESPONSE_PAYLOAD_KEY;
import static com.drofff.palindrome.constants.ParameterConstants.NAME;
import static com.drofff.palindrome.constants.ParameterConstants.SEARCH_QUERY;
import static com.drofff.palindrome.utils.FormattingUtils.resolveStringParams;
import static com.drofff.palindrome.utils.HttpUtils.getFromServer;
import static com.drofff.palindrome.utils.JsonUtils.getListFromJsonByKey;
import static com.drofff.palindrome.utils.ValidationUtils.validateNotNull;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toList;

public class SearchActivity extends AppCompatActivity {

    private static final Executor SEARCH_EXECUTOR = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        showProgressBar();
        String query = getSearchQuery();
        displayDriversWithName(query);
    }

    private void showProgressBar() {
        ProgressBar progressBar = findViewById(search_spinner);
        progressBar.setVisibility(VISIBLE);
    }

    private String getSearchQuery() {
        Intent intent = getIntent();
        String query = intent.getStringExtra(SEARCH_QUERY);
        validateNotNull(query, "Search query should not be null");
        return query;
    }

    private void displayDriversWithName(String name) {
        SEARCH_EXECUTOR.execute(() -> {
            List<Driver> drivers = findDriversByName(name);
            displayDrivers(drivers);
        });
    }

    private List<Driver> findDriversByName(String name) {
        String findDriversUrl = getResources().getString(find_drivers_url);
        Map<String, String> nameParam = singletonMap(NAME, name);
        String findDriversUrlWithParam = resolveStringParams(findDriversUrl, nameParam);
        JSONObject response = getFromServer(findDriversUrlWithParam);
        return getListFromJsonByKey(response, LIST_RESPONSE_PAYLOAD_KEY).stream()
                .map(Driver::fromJSONObject)
                .collect(toList());
    }

    private void displayDrivers(List<Driver> drivers) {
        RecyclerView driversView = findViewById(drivers_view);
        driversView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = getVerticalLayoutManager();
        driversView.setLayoutManager(layoutManager);
        DriversViewAdapter driversViewAdapter = new DriversViewAdapter(drivers);
        driversView.setAdapter(driversViewAdapter);
        runOnUiThread(this::hideProgressBar);
    }

    private RecyclerView.LayoutManager getVerticalLayoutManager() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(VERTICAL);
        return linearLayoutManager;
    }

    private void hideProgressBar() {
        ProgressBar progressBar = findViewById(search_spinner);
        progressBar.setVisibility(INVISIBLE);
    }

}
