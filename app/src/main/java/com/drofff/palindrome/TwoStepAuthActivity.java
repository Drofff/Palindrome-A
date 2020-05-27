package com.drofff.palindrome;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.drofff.palindrome.context.BeanContext;
import com.drofff.palindrome.entity.ApiTokens;
import com.drofff.palindrome.exception.PalindromeException;
import com.drofff.palindrome.exception.RequestException;
import com.drofff.palindrome.service.AuthenticationService;
import com.drofff.palindrome.service.AuthorizationTokenService;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.graphics.Color.RED;
import static com.drofff.palindrome.R.id.complete_two_step_auth_button;
import static com.drofff.palindrome.R.id.two_step_auth_token_field;
import static com.drofff.palindrome.R.string.two_step_auth_url;
import static com.drofff.palindrome.constants.JsonConstants.TOKEN_KEY;
import static com.drofff.palindrome.constants.SecurityConstants.USER_ID;
import static com.drofff.palindrome.utils.HttpUtils.postAtUrlWithJsonBody;
import static com.drofff.palindrome.utils.JsonUtils.parseObjectOfClassFromJson;
import static com.drofff.palindrome.utils.StringUtils.isNotBlank;
import static com.drofff.palindrome.utils.ValidationUtils.validateNotNull;
import static com.google.android.material.snackbar.Snackbar.LENGTH_LONG;

public class TwoStepAuthActivity extends AppCompatActivity {

    private static final Executor ACTIVITY_EXECUTOR = Executors.newSingleThreadExecutor();

    private AuthenticationService authenticationService;
    private AuthorizationTokenService authorizationTokenService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_step_auth);
        wireDependentBeans();
        Button completeTwoStepAuthButton = findViewById(complete_two_step_auth_button);
        registerCompleteTwoStepAuthListenerAt(completeTwoStepAuthButton);
    }

    private void wireDependentBeans() {
        authenticationService = BeanContext.getBeanOfClass(AuthenticationService.class);
        authorizationTokenService = BeanContext.getBeanOfClass(AuthorizationTokenService.class);
    }

    private void registerCompleteTwoStepAuthListenerAt(Button button) {
        button.setOnClickListener(view -> ACTIVITY_EXECUTOR.execute(() -> {
                try {
                    completeTwoStepAuth();
                } catch(RequestException e) {
                    runOnUiThread(() -> displayTokenError(view));
                }
            }));
    }

    private void completeTwoStepAuth() {
        if(hasNonBlankToken()) {
            JSONObject response = sendCompleteTwoStepAuthRequest();
            saveApiTokensFromResponse(response);
            redirectToMainActivity();
        }
    }

    private boolean hasNonBlankToken() {
        EditText tokenField = findViewById(two_step_auth_token_field);
        String token = tokenField.getText().toString();
        return isNotBlank(token);
    }

    private JSONObject sendCompleteTwoStepAuthRequest() {
        String userId = getUserId();
        String token = getEnteredTwoStepAuthToken();
        JSONObject body = toTwoStepAuthRequestBody(userId, token);
        String twoStepAuthUrl = getResources().getString(two_step_auth_url);
        return postAtUrlWithJsonBody(twoStepAuthUrl, body);
    }

    private String getEnteredTwoStepAuthToken() {
        EditText twoStepAuthTokenField = findViewById(two_step_auth_token_field);
        return twoStepAuthTokenField.getText().toString();
    }

    private String getUserId() {
        String userId = getIntent().getStringExtra(USER_ID);
        validateNotNull(userId, "User id should not be null");
        return userId;
    }

    private JSONObject toTwoStepAuthRequestBody(String userId, String token) {
        try {
            return buildTwoStepAuthRequestBody(userId, token);
        } catch(JSONException e) {
            throw new PalindromeException(e.getMessage());
        }
    }

    private JSONObject buildTwoStepAuthRequestBody(String userId, String token) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(USER_ID, userId);
        jsonObject.put(TOKEN_KEY, token);
        return jsonObject;
    }

    private void saveApiTokensFromResponse(JSONObject response) {
        ApiTokens apiTokens = parseObjectOfClassFromJson(ApiTokens.class, response);
        authorizationTokenService.saveAuthorizationToken(apiTokens.getAuthorizationToken());
        authenticationService.saveUserAuthentication(apiTokens.getUserId(), apiTokens.getAuthenticationToken());
    }

    private void redirectToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void displayTokenError(View view) {
        Snackbar.make(view, "Помилковий код підтвердження", LENGTH_LONG).show();
        EditText tokenField = findViewById(two_step_auth_token_field);
        tokenField.setBackgroundTintList(redColorStateList());
        tokenField.setText("");
    }

    private ColorStateList redColorStateList() {
        return ColorStateList.valueOf(RED);
    }

}