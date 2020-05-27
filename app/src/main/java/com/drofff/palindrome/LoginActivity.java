package com.drofff.palindrome;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.drofff.palindrome.context.BeanContext;
import com.drofff.palindrome.context.BeanManager;
import com.drofff.palindrome.dto.UserDto;
import com.drofff.palindrome.entity.ApiTokens;
import com.drofff.palindrome.exception.RequestException;
import com.drofff.palindrome.service.AuthenticationService;
import com.drofff.palindrome.service.AuthorizationTokenService;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.graphics.Color.RED;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.drofff.palindrome.constants.JsonConstants.MAC_ADDRESS_KEY;
import static com.drofff.palindrome.constants.JsonConstants.MESSAGE_KEY;
import static com.drofff.palindrome.constants.JsonConstants.OPTION_ID_KEY;
import static com.drofff.palindrome.constants.JsonConstants.TOKEN_KEY;
import static com.drofff.palindrome.constants.SecurityConstants.MESSAGE_TYPE;
import static com.drofff.palindrome.constants.SecurityConstants.TWO_STEP_AUTH_REQUEST;
import static com.drofff.palindrome.constants.SecurityConstants.USER_ID;
import static com.drofff.palindrome.utils.HttpUtils.postAtUrlWithJsonBody;
import static com.drofff.palindrome.utils.JsonUtils.parseObjectOfClassFromJson;
import static com.drofff.palindrome.utils.NetUtils.getMacAddress;
import static com.drofff.palindrome.utils.UiUtils.hideKeyboard;

public class LoginActivity extends AppCompatActivity {

    private static final Executor LOGIN_EXECUTOR = Executors.newSingleThreadExecutor();

    private static boolean hasRegisteredBeans = false;

    private AuthenticationService authenticationService;
    private AuthorizationTokenService authorizationTokenService;

    private Button loginButton;

    private static synchronized void beansRegistered() {
        hasRegisteredBeans = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        focusOnEmailField();
        if(shouldRegisterBeans()) {
            registerBeans();
        }
        authenticationService = getAuthenticationService();
        authorizationTokenService = getAuthorizationTokenService();
        loginButton = findViewById(R.id.login_button);
        redirectToMainActivityIfAuthenticated();
        registerLoginListenerAt(loginButton);
    }

    private boolean shouldRegisterBeans() {
        return !hasRegisteredBeans;
    }

    private void registerBeans() {
        BeanManager beanManager = new BeanManager(this);
        beanManager.registerBeans();
        beansRegistered();
    }

    private AuthenticationService getAuthenticationService() {
        return BeanContext.getBeanOfClass(AuthenticationService.class);
    }

    private AuthorizationTokenService getAuthorizationTokenService() {
        return BeanContext.getBeanOfClass(AuthorizationTokenService.class);
    }

    private void redirectToMainActivityIfAuthenticated() {
        if(isAuthenticated()) {
            redirectToNextActivity();
        }
    }

    private boolean isAuthenticated() {
        return authorizationTokenService.getAuthorizationTokenIfPresent().isPresent();
    }

    private void focusOnEmailField() {
        EditText emailInput = findViewById(R.id.email_value);
        emailInput.requestFocus();
    }

    private void registerLoginListenerAt(Button button) {
        button.setOnClickListener(view -> login());
    }

    private void login() {
        hideKeyboard(this);
        showProgressBar();
        String email = getTextFromInputWithId(R.id.email_value);
        String password = getTextFromInputWithId(R.id.password_value);
        String authenticationUrl = getResources().getString(R.string.authentication_url);
        UserDto userDto = new UserDto(email, password);
        LOGIN_EXECUTOR.execute(() -> sendAuthenticationRequest(authenticationUrl, userDto));
    }

    private void showProgressBar() {
        ProgressBar progressBar = findViewById(R.id.login_bar);
        progressBar.setVisibility(VISIBLE);
    }

    private String getTextFromInputWithId(int id) {
        EditText editText = findViewById(id);
        return editText.getText().toString();
    }

    private void sendAuthenticationRequest(String authenticationUrl, UserDto userDto) {
        try {
            JSONObject credentialsJson = new JSONObject(userDto.toJsonStr());
            JSONObject response = postAtUrlWithJsonBody(authenticationUrl, credentialsJson);
            acceptAuthenticationResponse(response);
        } catch(RequestException | JSONException e) {
            runOnUiThread(this::displayInputError);
        } finally {
            runOnUiThread(this::hideProgressBar);
        }
    }

    private void acceptAuthenticationResponse(JSONObject response) throws JSONException {
        if(isSecondFactorNeeded(response)) {
            redirectToTwoStepAuthActivityWithJson(response);
        } else {
            ApiTokens apiTokens = parseObjectOfClassFromJson(ApiTokens.class, response);
            saveApiTokens(apiTokens);
            redirectToNextActivity();
        }
    }

    private boolean isSecondFactorNeeded(JSONObject jsonObject) {
        try {
            jsonObject.getString(MESSAGE_KEY);
            return true;
        } catch(JSONException e) {
            return false;
        }
    }

    private void redirectToTwoStepAuthActivityWithJson(JSONObject jsonObject) throws JSONException {
        String userId = jsonObject.getString(USER_ID);
        Intent intent = new Intent(this, TwoStepAuthActivity.class);
        intent.putExtra(USER_ID, userId);
        startActivity(intent);
    }

    private void saveApiTokens(ApiTokens apiTokens) {
        authenticationService.saveUserAuthentication(apiTokens.getUserId(), apiTokens.getAuthenticationToken());
        authorizationTokenService.saveAuthorizationToken(apiTokens.getAuthorizationToken());
    }

    private void redirectToNextActivity() {
        Intent intent = isTwoStepAuthRequestForCurrentDevice() ? deviceRequestActivityIntent() :
                new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private boolean isTwoStepAuthRequestForCurrentDevice() {
        return isTwoStepAuthRequest() && isRequestForCurrentDevice();
    }

    private boolean isTwoStepAuthRequest() {
        String messageType = getIntent().getStringExtra(MESSAGE_TYPE);
        return TWO_STEP_AUTH_REQUEST.equals(messageType);
    }

    private boolean isRequestForCurrentDevice() {
        String destinedMacAddress = getIntent().getStringExtra(MAC_ADDRESS_KEY);
        String originalMacAddress = getMacAddress();
        return originalMacAddress.equalsIgnoreCase(destinedMacAddress);
    }

    private Intent deviceRequestActivityIntent() {
        Intent contextIntent = getIntent();
        Intent deviceRequestIntent = new Intent(this, DeviceRequestActivity.class);
        String token = contextIntent.getStringExtra(TOKEN_KEY);
        deviceRequestIntent.putExtra(TOKEN_KEY, token);
        String optionId = contextIntent.getStringExtra(OPTION_ID_KEY);
        deviceRequestIntent.putExtra(OPTION_ID_KEY, optionId);
        return deviceRequestIntent;
    }

    private void displayInputError() {
        Snackbar.make(loginButton, "Неправильні авторизаційні дані", Snackbar.LENGTH_LONG)
                .show();
        markInputFieldWithIdAsRed(R.id.email_value);
        markInputFieldWithIdAsRed(R.id.password_value);
    }

    private void markInputFieldWithIdAsRed(int id) {
        EditText editText = findViewById(id);
        ColorStateList colorStateList = ColorStateList.valueOf(RED);
        editText.setBackgroundTintList(colorStateList);
    }

    private void hideProgressBar() {
        ProgressBar progressBar = findViewById(R.id.login_bar);
        progressBar.setVisibility(INVISIBLE);
    }

}