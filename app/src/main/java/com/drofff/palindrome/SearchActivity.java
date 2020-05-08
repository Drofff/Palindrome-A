package com.drofff.palindrome;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
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
import static com.drofff.palindrome.R.id.no_results_text_search;
import static com.drofff.palindrome.R.id.search_spinner;
import static com.drofff.palindrome.R.menu.search_menu;
import static com.drofff.palindrome.R.string.find_drivers_url;
import static com.drofff.palindrome.constants.JsonConstants.LIST_RESPONSE_PAYLOAD_KEY;
import static com.drofff.palindrome.constants.ParameterConstants.NAME;
import static com.drofff.palindrome.constants.ParameterConstants.SEARCH_QUERY;
import static com.drofff.palindrome.utils.FormattingUtils.resolveStringParams;
import static com.drofff.palindrome.utils.HttpUtils.getFromServer;
import static com.drofff.palindrome.utils.JsonUtils.getListFromJsonByKey;
import static com.drofff.palindrome.utils.JsonUtils.parseObjectOfClassFromJson;
import static com.drofff.palindrome.utils.UiUtils.hideKeyboard;
import static com.drofff.palindrome.utils.UiUtils.isHomeButton;
import static com.drofff.palindrome.utils.ValidationUtils.validateNotNull;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toList;

public class SearchActivity extends AppCompatActivity {

    private static final Executor SEARCH_EXECUTOR = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle("");
        enableHomeButton();
        showProgressBar();
        String query = getSearchQuery();
        displayDriversWithName(query);
    }

    private void enableHomeButton() {
        ActionBar actionBar = getSupportActionBar();
        validateNotNull(actionBar, "Can not reach an action bar");
        actionBar.setDisplayHomeAsUpEnabled(true);
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
            Runnable displayResultRunnable = drivers.isEmpty() ? this::displayNoResultsText :
                    () -> displayDrivers(drivers);
            runOnUiThread(displayResultRunnable);
        });
    }

    private List<Driver> findDriversByName(String name) {
        String findDriversUrl = getResources().getString(find_drivers_url);
        Map<String, String> nameParam = singletonMap(NAME, name);
        String findDriversUrlWithParam = resolveStringParams(findDriversUrl, nameParam);
        JSONObject response = getFromServer(findDriversUrlWithParam);
        return getListFromJsonByKey(response, LIST_RESPONSE_PAYLOAD_KEY).stream()
                .map(json -> parseObjectOfClassFromJson(Driver.class, json))
                .collect(toList());
    }

    private void displayDrivers(List<Driver> drivers) {
        RecyclerView driversView = findViewById(drivers_view);
        driversView.setHasFixedSize(true);
        driversView.setLayoutManager(getVerticalLayoutManager());
        DriversViewAdapter driversViewAdapter = viewAdapterOfDrivers(drivers);
        driversView.setAdapter(driversViewAdapter);
        focusAt(driversView);
        hideProgressBar();
    }

    private RecyclerView.LayoutManager getVerticalLayoutManager() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(VERTICAL);
        return linearLayoutManager;
    }

    private DriversViewAdapter viewAdapterOfDrivers(List<Driver> drivers) {
        String query = getSearchQuery();
        return new DriversViewAdapter(drivers, query);
    }

    private void displayNoResultsText() {
        hideProgressBar();
        TextView noResultsTextView = findViewById(no_results_text_search);
        noResultsTextView.setVisibility(VISIBLE);
    }

    private void hideProgressBar() {
        ProgressBar progressBar = findViewById(search_spinner);
        progressBar.setVisibility(INVISIBLE);
    }

    private void focusAt(View view) {
        view.requestFocus();
        hideKeyboard(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(search_menu, menu);
        SearchView searchView = getSearchViewFromMenu(menu);
        searchView.setSearchableInfo(getSearchableInfo());
        searchView.setQuery(getSearchQuery(), false);
        searchView.setIconified(false);
        return true;
    }

    private SearchView getSearchViewFromMenu(Menu menu) {
        return (SearchView) menu.findItem(R.id.app_bar_search)
                .getActionView();
    }

    private SearchableInfo getSearchableInfo() {
        ComponentName componentName = getComponentName();
        SearchManager searchManager = getSearchManager();
        return searchManager.getSearchableInfo(componentName);
    }

    private SearchManager getSearchManager() {
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        validateNotNull(searchManager, "Unable to reach a search manager");
        return searchManager;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(isHomeButton(item)) {
            redirectToMainActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void redirectToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}