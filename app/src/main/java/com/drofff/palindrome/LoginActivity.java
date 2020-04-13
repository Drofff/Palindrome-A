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
import com.drofff.palindrome.exception.RequestException;
import com.drofff.palindrome.service.AuthorizationTokenService;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.graphics.Color.RED;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.drofff.palindrome.constants.SecurityConstants.TOKEN_PARAM;
import static com.drofff.palindrome.utils.HttpUtils.postToServerWithJsonBody;
import static com.drofff.palindrome.utils.UiUtils.hideKeyboard;

public class LoginActivity extends AppCompatActivity {

    private static final Executor LOGIN_EXECUTOR = Executors.newSingleThreadExecutor();

    private static boolean hasRegisteredBeans = false;

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

    private AuthorizationTokenService getAuthorizationTokenService() {
        return BeanContext.getBeanOfClass(AuthorizationTokenService.class);
    }

    private void redirectToMainActivityIfAuthenticated() {
        if(isAuthenticated()) {
            redirectToMainActivity();
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
            JSONObject response = postToServerWithJsonBody(authenticationUrl, credentialsJson);
            String authorizationToken = response.getString(TOKEN_PARAM);
            authorizationTokenService.saveAuthorizationToken(authorizationToken);
            redirectToMainActivity();
        } catch(RequestException | JSONException e) {
            runOnUiThread(this::displayInputError);
        } finally {
            runOnUiThread(this::hideProgressBar);
        }
    }

    private void redirectToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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