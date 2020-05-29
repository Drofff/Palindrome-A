package com.drofff.palindrome;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.drofff.palindrome.context.BeanContext;
import com.drofff.palindrome.exception.PalindromeException;
import com.drofff.palindrome.service.TwoStepAuthService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.drofff.palindrome.R.string.token_cache_file;
import static com.drofff.palindrome.utils.IOUtils.readAllAsString;
import static com.drofff.palindrome.utils.ValidationUtils.validateIsTrue;

public class MainActivity extends AppCompatActivity {

    private static final Executor ACTIVITY_EXECUTOR = Executors.newSingleThreadExecutor();

    private static final String LOG_TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initNavBar();
        updateRegistrationTokenIfPostponed();
    }

    private void initNavBar() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_documents_check, R.id.navigation_violation_add, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    private void updateRegistrationTokenIfPostponed() {
        if(hasPostponedToken()) {
            Log.d(LOG_TAG, "Postponed registration token found");
            String token = getPostponedToken();
            updateRegistrationToken(token);
            clearTokenCache();
        }
    }

    private boolean hasPostponedToken() {
        String tokenCacheFilePath = getTokenCacheFilePath();
        return new File(tokenCacheFilePath).exists();
    }

    private String getPostponedToken() {
        try {
            String tokenCacheFilePath = getTokenCacheFilePath();
            InputStream inputStream = new FileInputStream(tokenCacheFilePath);
            return readAllAsString(inputStream);
        } catch(IOException e) {
            throw new PalindromeException(e.getMessage());
        }
    }

    private void updateRegistrationToken(String token) {
        ACTIVITY_EXECUTOR.execute(() -> {
            Log.d(LOG_TAG, "Updating device registration token using a postponed token " + token);
            TwoStepAuthService twoStepAuthService = BeanContext.getBeanOfClass(TwoStepAuthService.class);
            twoStepAuthService.updateRegistrationToken(token);
        });
    }

    private void clearTokenCache() {
        String tokenCacheFilePath = getTokenCacheFilePath();
        boolean deleted = new File(tokenCacheFilePath).delete();
        validateIsTrue(deleted, "Can not delete a token cache file");
    }

    private String getTokenCacheFilePath() {
        String tokenCacheFile = getResources().getString(token_cache_file);
        return getFilesDir().getAbsolutePath() + "/" + tokenCacheFile;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.settings_item) {
            startSettingsActivity();
            return true;
        }
        return false;
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

}
