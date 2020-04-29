package com.drofff.palindrome;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.drofff.palindrome.context.BeanContext;
import com.drofff.palindrome.service.TwoStepAuthService;

import static com.drofff.palindrome.R.string.settings_item_title;
import static com.drofff.palindrome.utils.ValidationUtils.validateNotNull;

public class SettingsActivity extends AppCompatActivity {

    private TwoStepAuthService twoStepAuthService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        String title = getResources().getString(settings_item_title);
        setTitle(title);
        enableHomeButton();
        CheckBox twoStepAuthEnabledCheckBox = findViewById(R.id.two_step_auth_enabled);
        twoStepAuthService = getTwoStepAuthService();
        boolean twoStepAuthEnabled = twoStepAuthService.isTwoStepAuthEnabled();
        twoStepAuthEnabledCheckBox.setChecked(twoStepAuthEnabled);
        registerTwoStepAuthStatusChangeListenerAt(twoStepAuthEnabledCheckBox);
    }

    private void enableHomeButton() {
        ActionBar actionBar = getSupportActionBar();
        validateNotNull(actionBar, "Action bar is not reachable");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private TwoStepAuthService getTwoStepAuthService() {
        return BeanContext.getBeanOfClass(TwoStepAuthService.class);
    }

    private void registerTwoStepAuthStatusChangeListenerAt(CheckBox checkBox) {
        checkBox.setOnCheckedChangeListener((view, isChecked) -> {
            if(isChecked) {
                twoStepAuthService.enableTwoStepAuth();
            } else {
                twoStepAuthService.disableTwoStepAuth();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        return true;
    }

}
