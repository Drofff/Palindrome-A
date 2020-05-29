package com.drofff.palindrome.service;

import android.app.Activity;
import android.os.Build;
import android.util.Log;

import com.drofff.palindrome.R;
import com.drofff.palindrome.exception.PalindromeException;
import com.drofff.palindrome.exception.RequestException;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.os.Build.MANUFACTURER;
import static com.drofff.palindrome.R.string.update_registration_token_url;
import static com.drofff.palindrome.constants.JsonConstants.LABEL_KEY;
import static com.drofff.palindrome.constants.JsonConstants.MAC_ADDRESS_KEY;
import static com.drofff.palindrome.constants.JsonConstants.REGISTRATION_TOKEN_KEY;
import static com.drofff.palindrome.utils.AuthenticationUtils.getCurrentUser;
import static com.drofff.palindrome.utils.FormattingUtils.resolveStringParams;
import static com.drofff.palindrome.utils.HttpUtils.postToServer;
import static com.drofff.palindrome.utils.HttpUtils.postToServerWithJsonBody;
import static com.drofff.palindrome.utils.NetUtils.getMacAddress;
import static com.drofff.palindrome.utils.StringUtils.upperCaseFirstChar;

public class TwoStepAuthServiceImpl implements TwoStepAuthService {

    private static final Executor SERVICE_EXECUTOR = Executors.newFixedThreadPool(3);

    private final Activity contextActivity;

    private Boolean twoStepAuthEnabled;

    public TwoStepAuthServiceImpl(Activity contextActivity) {
        this.contextActivity = contextActivity;
    }

    @Override
    public void enableTwoStepAuth() {
        registerDeviceAsync();
        enableTwoStepAuthForPoliceAsync();
        twoStepAuthEnabled = true;
    }

    private void registerDeviceAsync() {
        SERVICE_EXECUTOR.execute(this::registerDevice);
    }

    private void registerDevice() {
        try {
            String registerDeviceUrl = contextActivity.getResources()
                    .getString(R.string.register_device_url);
            JSONObject userDeviceInfo = getUserDeviceInfo();
            postToServerWithJsonBody(registerDeviceUrl, userDeviceInfo);
        } catch(RequestException e) {
            logRequestException(e);
        }
    }

    private JSONObject getUserDeviceInfo() {
        try {
            return getCurrentDeviceInfo();
        } catch(JSONException e) {
            throw new PalindromeException(e.getMessage());
        }
    }

    private JSONObject getCurrentDeviceInfo() throws JSONException {
        JSONObject userDeviceInfo = new JSONObject();
        userDeviceInfo.put(LABEL_KEY, getDeviceName());
        userDeviceInfo.put(MAC_ADDRESS_KEY, getMacAddress());
        userDeviceInfo.put(REGISTRATION_TOKEN_KEY, getRegistrationToken());
        return userDeviceInfo;
    }

    private String getDeviceName() {
        String manufacturer = upperCaseFirstChar(MANUFACTURER);
        return manufacturer + " " + Build.MODEL;
    }

    private String getRegistrationToken() {
        CompletableFuture<String> tokenFuture = new CompletableFuture<>();
        FirebaseInstanceId firebaseInstanceId = FirebaseInstanceId.getInstance();
        firebaseInstanceId.getInstanceId()
                .addOnSuccessListener(task -> {
                    String token = task.getToken();
                    tokenFuture.complete(token);
                })
                .addOnFailureListener(e -> tokenFuture.cancel(false));
        return tokenFuture.join();
    }

    private void enableTwoStepAuthForPoliceAsync() {
        SERVICE_EXECUTOR.execute(this::enableTwoStepAuthForPolice);
    }

    private void enableTwoStepAuthForPolice() {
        try {
            String enableTwoStepAuthUrl = contextActivity.getResources()
                    .getString(R.string.enable_two_step_auth_url);
            postToServer(enableTwoStepAuthUrl);
        } catch(RequestException e) {
            logRequestException(e);
        }
    }

    @Override
    public void disableTwoStepAuth() {
        disableTwoStepAuthForPoliceAsync();
        twoStepAuthEnabled = false;
    }

    private void disableTwoStepAuthForPoliceAsync() {
        SERVICE_EXECUTOR.execute(this::disableTwoStepAuthForPolice);
    }

    private void disableTwoStepAuthForPolice() {
        try {
            String disableTwoStepAuthUrl = contextActivity.getResources()
                    .getString(R.string.disable_two_step_auth_url);
            postToServer(disableTwoStepAuthUrl);
        } catch(RequestException e) {
            logRequestException(e);
        }
    }

    private void logRequestException(RequestException e) {
        String tag = TwoStepAuthServiceImpl.class.getName();
        String message = e.getMessage() != null ? e.getMessage() :
                "HTTP request error";
        Log.e(tag, message);
    }

    @Override
    public boolean isTwoStepAuthEnabled() {
        if(twoStepAuthEnabled == null) {
            twoStepAuthEnabled = getCurrentUser().isTwoStepAuthEnabled();
        }
        return twoStepAuthEnabled;
    }

    @Override
    public void updateRegistrationToken(String token) {
        String updateRegistrationTokenUrl = contextActivity.getResources()
                .getString(update_registration_token_url);
        Map<String, String> updateParams = updateRegistrationTokenParams(token);
        String updateRegistrationTokenUrlWithParams = resolveStringParams(updateRegistrationTokenUrl,
                updateParams);
        postToServer(updateRegistrationTokenUrlWithParams);
    }

    private Map<String, String> updateRegistrationTokenParams(String token) {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put(REGISTRATION_TOKEN_KEY, token);
        requestParams.put(MAC_ADDRESS_KEY, getMacAddress());
        return requestParams;
    }

}
