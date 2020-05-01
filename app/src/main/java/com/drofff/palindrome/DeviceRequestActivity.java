package com.drofff.palindrome;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.view.View.VISIBLE;
import static com.drofff.palindrome.R.id.confirm_two_step;
import static com.drofff.palindrome.R.id.device_request_loader;
import static com.drofff.palindrome.R.id.reject_two_step;
import static com.drofff.palindrome.R.string.complete_two_step_auth_url;
import static com.drofff.palindrome.constants.JsonConstants.OPTION_ID_KEY;
import static com.drofff.palindrome.constants.JsonConstants.TOKEN_KEY;
import static com.drofff.palindrome.utils.HttpUtils.postToServerWithJsonBody;
import static com.drofff.palindrome.utils.ValidationUtils.validateNotNull;

public class DeviceRequestActivity extends AppCompatActivity {

    private static final Executor ACTIVITY_EXECUTOR = Executors.newSingleThreadExecutor();

    private String token;

    private String optionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_request);
        setTitle("");
        initRequestInfo();
        Button confirmButton = findViewById(confirm_two_step);
        registerConfirmTwoStepAuthListenerAt(confirmButton);
        Button rejectButton = findViewById(reject_two_step);
        redirectToMainActivityAt(rejectButton);
    }

    private void initRequestInfo() {
        Intent intent = getIntent();
        token = intent.getStringExtra(TOKEN_KEY);
        validateNotNull(token, "Token is required");
        optionId = intent.getStringExtra(OPTION_ID_KEY);
        validateNotNull(optionId, "Option id should not be null");
    }

    private void registerConfirmTwoStepAuthListenerAt(Button button) {
        button.setOnClickListener(view -> confirmTwoStepAuthAsync());
    }

    private void confirmTwoStepAuthAsync() {
        ACTIVITY_EXECUTOR.execute(this::confirmTwoStepAuth);
    }

    private void confirmTwoStepAuth() {
        runOnUiThread(this::startProgressBar);
        String completeTwoStepAuthUrl = getResources().getString(complete_two_step_auth_url);
        JSONObject completeTwoStepAuthBodyJson = completeTwoStepAuthParamsJson();
        postToServerWithJsonBody(completeTwoStepAuthUrl, completeTwoStepAuthBodyJson);
        runOnUiThread(this::stopProgressBar);
        redirectToMainActivity();
    }

    private void startProgressBar() {
        ProgressBar progressBar = findViewById(device_request_loader);
        progressBar.setVisibility(VISIBLE);
    }

    private JSONObject completeTwoStepAuthParamsJson() {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put(TOKEN_KEY, token);
        requestParams.put(OPTION_ID_KEY, optionId);
        return new JSONObject(requestParams);
    }

    private void stopProgressBar() {
        ProgressBar progressBar = findViewById(device_request_loader);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void redirectToMainActivityAt(Button button) {
        button.setOnClickListener(view -> redirectToMainActivity());
    }

    private void redirectToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
