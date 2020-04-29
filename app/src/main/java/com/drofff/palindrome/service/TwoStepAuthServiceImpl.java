package com.drofff.palindrome.service;

import android.app.Activity;
import android.os.Build;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.drofff.palindrome.R;
import com.drofff.palindrome.exception.PalindromeException;
import com.drofff.palindrome.exception.RequestException;
import com.drofff.palindrome.worker.DeviceRequestWorker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.os.Build.MANUFACTURER;
import static androidx.work.ExistingPeriodicWorkPolicy.REPLACE;
import static androidx.work.NetworkType.CONNECTED;
import static com.drofff.palindrome.constants.JsonConstants.LABEL_KEY;
import static com.drofff.palindrome.constants.JsonConstants.MAC_ADDRESS_KEY;
import static com.drofff.palindrome.utils.AuthenticationUtils.getCurrentUser;
import static com.drofff.palindrome.utils.HttpUtils.postToServer;
import static com.drofff.palindrome.utils.HttpUtils.postToServerWithJsonBody;
import static com.drofff.palindrome.utils.NetUtils.getMacAddress;
import static com.drofff.palindrome.utils.StringUtils.upperCaseFirstChar;
import static java.util.concurrent.TimeUnit.SECONDS;

public class TwoStepAuthServiceImpl implements TwoStepAuthService {

    private static final String DEVICE_REQUEST_LISTENER_TAG = "device_request";

    private static final String PERIODIC_WORK_NAME = "two_step_auth_work";

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
        runDeviceRequestListener();
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
        return userDeviceInfo;
    }

    private String getDeviceName() {
        String manufacturer = upperCaseFirstChar(MANUFACTURER);
        return manufacturer + " " + Build.MODEL;
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

    private void runDeviceRequestListener() {
        PeriodicWorkRequest deviceRequestListener = getDeviceRequestListener();
        WorkManager.getInstance(contextActivity)
                .enqueueUniquePeriodicWork(PERIODIC_WORK_NAME, REPLACE, deviceRequestListener);
    }

    private PeriodicWorkRequest getDeviceRequestListener() {
        return new PeriodicWorkRequest.Builder(DeviceRequestWorker.class, 10, SECONDS)
                .addTag(DEVICE_REQUEST_LISTENER_TAG)
                .setConstraints(getRequireNetworkConstraint())
                .build();
    }

    private Constraints getRequireNetworkConstraint() {
        return new Constraints.Builder()
                .setRequiredNetworkType(CONNECTED)
                .build();
    }

    @Override
    public void disableTwoStepAuth() {
        disableTwoStepAuthForPoliceAsync();
        stopDeviceRequestListener();
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

    private void stopDeviceRequestListener() {
        WorkManager.getInstance(contextActivity)
                .cancelAllWorkByTag(DEVICE_REQUEST_LISTENER_TAG);
    }

    @Override
    public boolean isTwoStepAuthEnabled() {
        if(twoStepAuthEnabled == null) {
            twoStepAuthEnabled = getCurrentUser().isTwoStepAuthEnabled();
        }
        return twoStepAuthEnabled;
    }

}
