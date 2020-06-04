package com.drofff.palindrome.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.drofff.palindrome.DeviceRequestActivity;
import com.drofff.palindrome.R;
import com.drofff.palindrome.context.BeanContext;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static androidx.core.app.NotificationCompat.PRIORITY_MAX;
import static com.drofff.palindrome.R.string.token_cache_file;
import static com.drofff.palindrome.constants.JsonConstants.MAC_ADDRESS_KEY;
import static com.drofff.palindrome.constants.JsonConstants.OPTION_ID_KEY;
import static com.drofff.palindrome.constants.JsonConstants.TOKEN_KEY;
import static com.drofff.palindrome.utils.FileUtils.createFileIfNotExists;
import static com.drofff.palindrome.utils.FileUtils.writeTextToFileAtPath;
import static com.drofff.palindrome.utils.NetUtils.getMacAddress;
import static com.drofff.palindrome.utils.ValidationUtils.validateNotNull;

public class MessagingService extends FirebaseMessagingService {

    private static final Executor SERVICE_EXECUTOR = Executors.newSingleThreadExecutor();

    private static final String LOG_TAG = MessagingService.class.getName();

    private static final String TWO_STEP_AUTH_NOTIFICATION_CHANNEL = "two-step-auth";

    private static final int NOTIFICATION_ID = 331;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        if(isMessageDestinedForCurrentDevice(remoteMessage)) {
            String token = getTokenFromMessage(remoteMessage);
            String optionId = getOptionIdFromMessage(remoteMessage);
            notifyTwoStepAuthRequest(token, optionId);
        }
    }

    private boolean isMessageDestinedForCurrentDevice(RemoteMessage message) {
        String destinationMacAddress = getMacAddressFromMessage(message);
        String currentDeviceMacAddress = getMacAddress();
        return currentDeviceMacAddress.equalsIgnoreCase(destinationMacAddress);
    }

    private String getMacAddressFromMessage(RemoteMessage message) {
        return getStrFromMessageByKey(message, MAC_ADDRESS_KEY);
    }

    private String getTokenFromMessage(RemoteMessage message) {
        return getStrFromMessageByKey(message, TOKEN_KEY);
    }

    private String getOptionIdFromMessage(RemoteMessage message) {
        return getStrFromMessageByKey(message, OPTION_ID_KEY);
    }

    private String getStrFromMessageByKey(RemoteMessage message, String key) {
        Map<String, String> data = message.getData();
        String strValue = data.get(key);
        validateNotNull(strValue, "Missing message data with key " + key);
        return strValue;
    }

    private void notifyTwoStepAuthRequest(String token, String optionId) {
        PendingIntent twoStepAuthIntent = getTwoStepAuthIntent(token, optionId);
        Notification twoStepAuthNotification = twoStepAuthNotificationForIntent(twoStepAuthIntent);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(NOTIFICATION_ID, twoStepAuthNotification);
    }

    private PendingIntent getTwoStepAuthIntent(String token, String optionId) {
        Intent intent = new Intent(getApplicationContext(), DeviceRequestActivity.class);
        intent.putExtra(TOKEN_KEY, token);
        intent.putExtra(OPTION_ID_KEY, optionId);
        return PendingIntent.getActivity(getApplicationContext(), 0, intent, FLAG_UPDATE_CURRENT);
    }

    private Notification twoStepAuthNotificationForIntent(PendingIntent pendingIntent) {
        return new NotificationCompat.Builder(getApplicationContext(), TWO_STEP_AUTH_NOTIFICATION_CHANNEL)
                .setContentTitle("Двох етапна ауторизація")
                .setContentText("Підтвердіть вхід з нового пристрою")
                .setSmallIcon(R.drawable.palindrome_icon)
                .setContentIntent(pendingIntent)
                .setPriority(PRIORITY_MAX)
                .setAutoCancel(true)
                .build();
    }

    @Override
    public void onNewToken(@NonNull String token) {
        SERVICE_EXECUTOR.execute(() -> {
            Log.d(LOG_TAG, "New messaging token has been received " + token);
            if(isAuthenticated()) {
                Log.d(LOG_TAG, "Updating device registration token");
                updateRegistrationToken(token);
            } else {
                Log.d(LOG_TAG, "Postponed a registration token update");
                postponeRegistrationTokenUpdate(token);
            }
        });
    }

    private boolean isAuthenticated() {
        AuthorizationTokenService authorizationTokenService = BeanContext.getBeanOfClass(AuthorizationTokenService.class);
        return authorizationTokenService.getAuthorizationTokenIfPresent().isPresent();
    }

    private void updateRegistrationToken(String token) {
        TwoStepAuthService twoStepAuthService = BeanContext.getBeanOfClass(TwoStepAuthService.class);
        twoStepAuthService.updateRegistrationToken(token);
    }

    private void postponeRegistrationTokenUpdate(String token) {
        String tokenCacheFile = getTokenCacheFilePath();
        createFileIfNotExists(tokenCacheFile);
        writeTextToFileAtPath(token, tokenCacheFile);
    }

    private String getTokenCacheFilePath() {
        Context appContext = getApplicationContext();
        File appDir = appContext.getFilesDir();
        String tokenCacheFile = appContext.getString(token_cache_file);
        return appDir.getAbsolutePath() + "/" + tokenCacheFile;
    }

}