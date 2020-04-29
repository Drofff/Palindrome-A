package com.drofff.palindrome.worker;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.drofff.palindrome.DeviceRequestActivity;
import com.drofff.palindrome.R;
import com.drofff.palindrome.entity.DeviceRequest;
import com.drofff.palindrome.exception.RequestException;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static androidx.core.app.NotificationCompat.PRIORITY_MAX;
import static androidx.work.ListenableWorker.Result.success;
import static com.drofff.palindrome.constants.JsonConstants.LIST_RESPONSE_PAYLOAD_KEY;
import static com.drofff.palindrome.constants.JsonConstants.MAC_ADDRESS_KEY;
import static com.drofff.palindrome.constants.JsonConstants.OPTION_ID_KEY;
import static com.drofff.palindrome.constants.JsonConstants.TOKEN_KEY;
import static com.drofff.palindrome.utils.FormattingUtils.resolveStringParams;
import static com.drofff.palindrome.utils.HttpUtils.getFromServer;
import static com.drofff.palindrome.utils.JsonUtils.getListFromJsonByKey;
import static com.drofff.palindrome.utils.NetUtils.getMacAddress;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toList;

public class DeviceRequestWorker extends Worker {

    private static final String LOG_TAG = DeviceRequestWorker.class.getName();

    private static final String DEVICE_REQUEST_NOTIFICATIONS_CHANNEL_ID = "device_requests";
    private static final int NOTIFICATION_ID = 331;

    private static final Executor WORKER_EXECUTOR = Executors.newSingleThreadExecutor();

    private final Context context;

    public DeviceRequestWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        String macAddress = getMacAddress();
        CompletableFuture<List<DeviceRequest>> resultFuture = new CompletableFuture<>();
        getRequestsForDeviceWithMacAddressAsync(macAddress, resultFuture);
        resultFuture.join().forEach(this::processDeviceRequest);
        return success();
    }

    private void getRequestsForDeviceWithMacAddressAsync(String macAddress, CompletableFuture<List<DeviceRequest>> resultFuture) {
        WORKER_EXECUTOR.execute(() -> {
            try {
                List<DeviceRequest> deviceRequests = getRequestsForDeviceWithMacAddress(macAddress);
                resultFuture.complete(deviceRequests);
            } catch(RequestException e) {
                logRequestException(e);
                resultFuture.complete(emptyList());
            }
        });
    }

    private List<DeviceRequest> getRequestsForDeviceWithMacAddress(String macAddress) {
        String getDeviceRequestsUrl = getDeviceRequestsUrlWithMacAddress(macAddress);
        JSONObject response = getFromServer(getDeviceRequestsUrl);
        return getListFromJsonByKey(response, LIST_RESPONSE_PAYLOAD_KEY).stream()
                .map(DeviceRequest::fromJSONObject)
                .collect(toList());
    }

    private String getDeviceRequestsUrlWithMacAddress(String macAddress) {
        String getDeviceRequestsUrl = context.getResources()
                .getString(R.string.get_device_requests_url);
        Map<String, String> macAddressParam = singletonMap(MAC_ADDRESS_KEY, macAddress);
        return resolveStringParams(getDeviceRequestsUrl, macAddressParam);
    }

    private void processDeviceRequest(DeviceRequest deviceRequest) {
        PendingIntent deviceRequestIntent = getDeviceRequestIntent(deviceRequest);
        Notification requestNotification = deviceRequestNotificationForIntent(deviceRequestIntent);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(NOTIFICATION_ID, requestNotification);
    }

    private PendingIntent getDeviceRequestIntent(DeviceRequest deviceRequest) {
        Intent intent = new Intent(context, DeviceRequestActivity.class);
        intent.putExtra(TOKEN_KEY, deviceRequest.getToken());
        intent.putExtra(OPTION_ID_KEY, deviceRequest.getOptionId());
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK);
        return PendingIntent.getActivity(context, 0, intent, 0);
    }

    private Notification deviceRequestNotificationForIntent(PendingIntent pendingIntent) {
        return new NotificationCompat.Builder(context, DEVICE_REQUEST_NOTIFICATIONS_CHANNEL_ID)
                .setContentTitle("Двох етапна ауторизація")
                .setContentText("Підтвердіть вхід з нового пристрою")
                .setSmallIcon(R.drawable.palindrome_icon)
                .setContentIntent(pendingIntent)
                .setPriority(PRIORITY_MAX)
                .setAutoCancel(true)
                .build();
    }

    private void logRequestException(RequestException e) {
        String message = Optional.ofNullable(e.getMessage())
                .orElse("Error while loading device requests from server");
        Log.e(LOG_TAG, message);
    }

}
