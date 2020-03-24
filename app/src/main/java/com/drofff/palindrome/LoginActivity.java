package com.drofff.palindrome;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.drofff.palindrome.context.BeanContext;
import com.drofff.palindrome.context.UserContextHolder;
import com.drofff.palindrome.dto.UserDto;
import com.drofff.palindrome.exception.RequestException;
import com.drofff.palindrome.service.AuthorizationTokenService;
import com.drofff.palindrome.service.JsonFileAuthorizationTokenService;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.graphics.Color.RED;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.drofff.palindrome.constants.SecurityConstants.TOKEN_PARAM;
import static com.drofff.palindrome.utils.HttpUtils.postAtUrlWithJsonBody;
import static com.drofff.palindrome.utils.UiUtils.hideKeyboard;

public class LoginActivity extends AppCompatActivity {

    private static final Executor LOGIN_EXECUTOR = Executors.newSingleThreadExecutor();

    private AuthorizationTokenService authorizationTokenService;

    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        focusOnEmailField();
        authorizationTokenService = getAuthorizationTokenService();
        loginButton = findViewById(R.id.login_button);
        initUserContextIfNeeded();
        redirectToMainActivityIfAuthenticated();
        registerLoginListenerAt(loginButton);
    }

    private AuthorizationTokenService getAuthorizationTokenService() {
        if(BeanContext.haveBeanOfClass(AuthorizationTokenService.class)) {
            return BeanContext.getBeanOfClass(AuthorizationTokenService.class);
        }
        File rootDir = getFilesDir();
        AuthorizationTokenService tokenService = new JsonFileAuthorizationTokenService(rootDir);
        BeanContext.registerBean(tokenService);
        return tokenService;
    }

    private void initUserContextIfNeeded() {
        if(isContextNotInitialized()) {
            String policeInfoUrl = getResources().getString(R.string.police_info_url);
            UserContextHolder.initContext(policeInfoUrl);
        }
    }

    private boolean isContextNotInitialized() {
        return !UserContextHolder.isContextInitialized();
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
            JSONObject response = postAtUrlWithJsonBody(authenticationUrl, credentialsJson);
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