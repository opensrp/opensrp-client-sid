package org.smartregister.bidan.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.bidan.R;
import org.smartregister.bidan.activity.LoginActivity;
import org.smartregister.bidan.application.BidanApplication;
import org.smartregister.bidan.facial.domain.ProfileImage;
import org.smartregister.bidan.facial.repository.ImageRepository;
import org.smartregister.bidan.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.bidan.repository.IndonesiaECRepository;
import org.smartregister.bidan.sync.ECSyncUpdater;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.Response;
import org.smartregister.service.HTTPAgent;
import org.smartregister.sync.ClientProcessor;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import utils.NetworkUtils;

/**
 * Created by sid-tech on 5/22/18
 */

public class SyncService extends Service {
    private static final String TAG = SyncService.class.getName();
    public static final int EVENT_PULL_LIMIT = 100;
    private static final String EVENTS_SYNC_PATH = "/rest/event/add";
    private static final String PHOTO_UPLOAD_PATH = "/multimedia/upload";
    private static final String PHOTO_DOWNLOAD_PATH = "/multimedia/profileimage";
    private static final int EVENT_PUSH_LIMIT = 50;
    private Context context;
    private HTTPAgent httpAgent;
    private volatile HandlerThread mHandlerThread;
    private ServiceHandler mServiceHandler;
    private List<Observable<?>> observables;
    private Long startSync = 0L;
    private Long endSync = 0L;

    @Override
    public void onCreate() {
        super.onCreate();
        mHandlerThread = new HandlerThread("SyncService.HandlerThread", Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();

        mServiceHandler = new ServiceHandler(mHandlerThread.getLooper());

        context = getBaseContext();
        httpAgent = BidanApplication.getInstance().context().getHttpAgent();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mHandlerThread.quit();
    }

    protected void handleSync() {
        sendSyncStatusBroadcastMessage(FetchStatus.fetchStarted);
        if (BidanApplication.getInstance().context().IsUserLoggedOut()) {
            drishtiLogInfo("Not updating from server as user is not logged in.");
            return;
        }

        doSync();
    }

    private void doSync() {
        if (!NetworkUtils.isNetworkAvailable()) {
            sendSyncStatusBroadcastMessage(FetchStatus.noConnection, true);
            return;
        }

        try {
            pushECToServer();
            pushPhotoToServer();
            pullECFromServer();

        } catch (Exception e) {
            Log.e(getClass().getName(), "", e);
            sendSyncStatusBroadcastMessage(FetchStatus.fetchedFailed, true);
        }
    }

    private void pushECToServer() {
        IndonesiaECRepository db = BidanApplication.getInstance().indonesiaECRepository();
        boolean keepSyncing = true;

        while (keepSyncing) {
            try {
                Map<String, Object> pendingEvents = db.getUnSyncedEvents(EVENT_PUSH_LIMIT);

                if (pendingEvents.isEmpty()) {
                    return;
                }

                String baseUrl = BidanApplication.getInstance().context().configuration().dristhiBaseURL();
                if (baseUrl.endsWith(context.getString(R.string.url_separator))) {
                    baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(context.getString(R.string.url_separator)));
                }
                // create request body
                JSONObject request = new JSONObject();
                if (pendingEvents.containsKey(context.getString(R.string.clients_key))) {
                    request.put(context.getString(R.string.clients_key), pendingEvents.get(context.getString(R.string.clients_key)));
                }
                if (pendingEvents.containsKey(context.getString(R.string.events_key))) {
                    request.put(context.getString(R.string.events_key), pendingEvents.get(context.getString(R.string.events_key)));
                }

                String jsonPayload = request.toString();
                Log.e(TAG, "pushECToServer: "+ jsonPayload );
                Response<String> response = httpAgent.post(
                        MessageFormat.format("{0}/{1}", baseUrl, EVENTS_SYNC_PATH), jsonPayload);

                if (response.isFailure()) {
                    Log.e(getClass().getName(), "Events sync failed.");
                    return;
                }
                db.markEventsAsSynced(pendingEvents);
                Log.i(getClass().getName(), "Events synced successfully.");
            } catch (Exception e) {
                Log.e(getClass().getName(), e.getMessage());
            }
        }

    }

    private void pushPhotoToServer(){
        String baseUrl = BidanApplication.getInstance().context().configuration().dristhiBaseURL();
        if (baseUrl.endsWith(context.getString(R.string.url_separator))) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(context.getString(R.string.url_separator)));
        }
        List<ProfileImage> pendingImages = BidanApplication.getInstance().imageRepository().getAllUnsyncImages();
        String path = DrishtiApplication.getAppDir();
        for (ProfileImage image : pendingImages){
            String fullPath = path + File.separator + image.getBaseEntityId() + ".jpg";
            Log.d(TAG, "pushPhotoToServer: baseEntityId="+image.getBaseEntityId());
            Log.d(TAG, "pushPhotoToServer: path="+fullPath);
            org.smartregister.domain.ProfileImage image1 =
                    new org.smartregister.domain.ProfileImage(
                            String.valueOf(image.getId()),
                            "",
                            image.getBaseEntityId(),
                            image.getContenttype(),
                            fullPath,
                            image.getSyncStatus(),
                            image.getFilecategory());
            image1.setAnmId(BidanApplication.getInstance().context().allSharedPreferences().fetchRegisteredANM());
            String response = httpAgent.httpImagePost(MessageFormat.format("{0}/{1}", baseUrl, PHOTO_UPLOAD_PATH),image1);
            Log.d(TAG, "pushPhotoToServer: response="+response);
            if(response.equals("\"fail\"")){
                Log.d(TAG, "pushPhotoToServer: failed");
                return;
            }

            image.setSyncStatus(ImageRepository.TYPE_Synced);
            BidanApplication.getInstance().imageRepository().add(image);
        }
    }

    private void pullECFromServer() {
        Log.e(TAG, "pullECFromServer: start" );
        final ECSyncUpdater ecUpdater = ECSyncUpdater.getInstance(context);

        // Fetch locations
        String locations = Utils.getPreference(context, LoginActivity.PREF_TEAM_LOCATIONS, "");
        if (StringUtils.isBlank(locations)) {
            sendSyncStatusBroadcastMessage(FetchStatus.fetchedFailed, true);
            return;
        }

        Observable.just(locations)
                .observeOn(AndroidSchedulers.from(mHandlerThread.getLooper()))
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<String, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(@NonNull String locations) throws Exception {

                        JSONObject jsonObject = fetchRetry(locations, 0);
                        if (jsonObject == null) {
                            return Observable.just(FetchStatus.fetchedFailed);
                        } else {
                            final String NO_OF_EVENTS = "no_of_events";
                            int eCount = jsonObject.has(NO_OF_EVENTS) ? jsonObject.getInt(NO_OF_EVENTS) : 0;
                            if (eCount < 0) {
                                return Observable.just(FetchStatus.fetchedFailed);
                            } else if (eCount == 0) {
                                return Observable.just(FetchStatus.fetched);
                            } else {
                                Pair<Long, Long> serverVersionPair = getMinMaxServerVersions(jsonObject);
                                long lastServerVersion = serverVersionPair.second - 1;
                                if (eCount < EVENT_PULL_LIMIT) {
                                    lastServerVersion = serverVersionPair.second;
                                }

                                ecUpdater.updateLastSyncTimeStamp(lastServerVersion);
                                return Observable.just(new ResponseParcel(jsonObject, serverVersionPair));
                            }
                        }
                    }
                })
                .subscribe(new Consumer<Object>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void accept(Object o) throws Exception {
                        if (o != null) {
                            if (o instanceof ResponseParcel) {
                                ResponseParcel responseParcel = (ResponseParcel) o;
                                saveResponseParcel(responseParcel);
                            } else if (o instanceof FetchStatus) {
                                final FetchStatus fetchStatus = (FetchStatus) o;
                                if (observables != null && !observables.isEmpty()) {
                                    Observable.zip(observables, new Function<Object[], Object>() {
                                        @Override
                                        public Object apply(@NonNull Object[] objects) throws Exception {
                                            return FetchStatus.fetched;
                                        }
                                    }).subscribe(new Consumer<Object>() {
                                        @Override
                                        public void accept(Object o) throws Exception {
                                            complete(fetchStatus);
                                        }
                                    });
                                } else {
                                    complete(fetchStatus);
                                }

                            }
                        }
                    }
                });
    }

    private void pullPhotoFromServer(){
        ImageRepository imageRepo = BidanApplication.getInstance().imageRepository();
        String baseUrl = BidanApplication.getInstance().context().configuration().dristhiBaseURL();
        if (baseUrl.endsWith(context.getString(R.string.url_separator))) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(context.getString(R.string.url_separator)));
        }
        final ECSyncUpdater ecUpdater = ECSyncUpdater.getInstance(context);
        List<JSONObject> events = ecUpdater.allEvents(startSync, endSync);
        try {
            for (JSONObject event : events){
                if(event.getString("eventType").equals("Update Photo")){
                    Log.d(TAG, "pullPhotoFromServer: event="+event);
                    Long version = event.getLong("version");
                    JSONArray jsonDataSegment = event.getJSONArray("obs");
                    int len = jsonDataSegment.length();
                    String faceVector = "[]";
                    for (int i = 0; i < len; i++) {
                        JSONObject seg = jsonDataSegment.getJSONObject(i);
                        if(seg.getString("fieldCode").equals("face_vector")){
                            JSONArray fcValue = seg.getJSONArray("values");
                            faceVector = fcValue.getString(0);
                            Log.d(TAG, "pullPhotoFromServer: faceVector="+faceVector);
                            break;
                        }
                    }
                    Log.d(TAG, "pullPhotoFromServer: jsonDataSegment"+jsonDataSegment);
                    String baseEntityId = event.getString("baseEntityId");
                    int responseCode = downloadPhoto(MessageFormat.format("{0}/{1}/{2}", baseUrl, PHOTO_DOWNLOAD_PATH, baseEntityId),DrishtiApplication.getAppDir());
                    Log.d(TAG, "pullPhotoFromServer: responseCode="+responseCode);
                    if (responseCode == 200) {
                        ProfileImage profileImage = imageRepo.findByBaseEntityId(baseEntityId);
                        if(profileImage==null){
                            profileImage = new ProfileImage();
                        }
                        profileImage.setBaseEntityId(baseEntityId);
                        profileImage.setContenttype("jpeg");
                        profileImage.setFilecategory("profilepic");
                        profileImage.setFilevector(faceVector);
                        profileImage.setCreatedAt(version);
                        profileImage.setUpdatedAt(version);
                        profileImage.setSyncStatus(String.valueOf(ImageRepository.TYPE_Synced));
                        imageRepo.add(profileImage);
                    }
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void saveResponseParcel(final ResponseParcel responseParcel) {
        Log.e(TAG, "saveResponseParcel: " );
        final ECSyncUpdater ecUpdater = ECSyncUpdater.getInstance(context);
        final Observable<FetchStatus> observable = Observable.just(responseParcel)
                .observeOn(AndroidSchedulers.from(mHandlerThread.getLooper()))
                .subscribeOn(Schedulers.io()).
                        flatMap(new Function<ResponseParcel, ObservableSource<FetchStatus>>() {
                            @Override
                            public ObservableSource<FetchStatus> apply(@NonNull ResponseParcel responseParcel) throws Exception {
                                JSONObject jsonObject = responseParcel.getJsonObject();
                                ecUpdater.saveAllClientsAndEvents(jsonObject);
                                return Observable.
                                        just(responseParcel.getServerVersionPair())
                                        .observeOn(AndroidSchedulers.from(mHandlerThread.getLooper()))
                                        .subscribeOn(Schedulers.io())
                                        .map(new Function<Pair<Long, Long>, FetchStatus>() {
                                            @Override
                                            public FetchStatus apply(@NonNull Pair<Long, Long> serverVersionPair) throws Exception {
                                                startSync = serverVersionPair.first - 1;
                                                endSync = serverVersionPair.second;
                                                ClientProcessor.getInstance(context).processClient(ecUpdater.allEvents(serverVersionPair.first - 1, serverVersionPair.second));
                                                return FetchStatus.fetched;
                                            }
                                        });

                            }
                        });

        observable.subscribe(new Consumer<FetchStatus>() {
            @Override
            public void accept(FetchStatus fetchStatus) throws Exception {
                sendSyncStatusBroadcastMessage(FetchStatus.fetched);
                observables.remove(observable);
            }
        });

        observables.add(observable);

        pullECFromServer();

    }

    private JSONObject fetchRetry(String locations, int count) throws Exception {
        // Request spacing
        try {
            final int ONE_SECOND = 1000;
            Thread.sleep(ONE_SECOND);
        } catch (InterruptedException ie) {
            Log.e(getClass().getName(), ie.getMessage());
        }

        final ECSyncUpdater ecUpdater = ECSyncUpdater.getInstance(context);

        try {
            return ecUpdater.fetchAsJsonObject(AllConstants.SyncFilters.FILTER_LOCATION_ID, locations);

        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage());
            if (count >= 2) {
                return null;
            } else {
                int newCount = count + 1;
                return fetchRetry(locations, newCount);
            }

        }
    }

    private void complete(FetchStatus fetchStatus) {
        if (fetchStatus.equals(FetchStatus.fetched)) {
            ECSyncUpdater ecSyncUpdater = ECSyncUpdater.getInstance(context);
            ecSyncUpdater.updateLastCheckTimeStamp(Calendar.getInstance().getTimeInMillis());
        }
        pullPhotoFromServer();
        sendSyncStatusBroadcastMessage(fetchStatus, true);
    }

    private void sendSyncStatusBroadcastMessage(FetchStatus fetchStatus, boolean isComplete) {
        Intent intent = new Intent();
        intent.setAction(SyncStatusBroadcastReceiver.ACTION_SYNC_STATUS);
        intent.putExtra(SyncStatusBroadcastReceiver.EXTRA_FETCH_STATUS, fetchStatus);
        intent.putExtra(SyncStatusBroadcastReceiver.EXTRA_COMPLETE_STATUS, isComplete);
        sendBroadcast(intent);

        if (isComplete) {
            stopSelf();
        }
    }

    private void sendSyncStatusBroadcastMessage(FetchStatus fetchStatus) {
        sendSyncStatusBroadcastMessage(fetchStatus, false);
    }

    private void drishtiLogInfo(String message) {
        org.smartregister.util.Log.logInfo(message);
    }

    private Pair<Long, Long> getMinMaxServerVersions(JSONObject jsonObject) {
        final String EVENTS = "events";
        final String SERVER_VERSION = "serverVersion";
        try {
            if (jsonObject != null && jsonObject.has(EVENTS)) {
                JSONArray events = jsonObject.getJSONArray(EVENTS);

                long maxServerVersion = Long.MIN_VALUE;
                long minServerVersion = Long.MAX_VALUE;

                for (int i = 0; i < events.length(); i++) {
                    Object o = events.get(i);
                    if (o instanceof JSONObject) {
                        JSONObject jo = (JSONObject) o;
                        if (jo.has(SERVER_VERSION)) {
                            long serverVersion = jo.getLong(SERVER_VERSION);
                            if (serverVersion > maxServerVersion) {
                                maxServerVersion = serverVersion;
                            }

                            if (serverVersion < minServerVersion) {
                                minServerVersion = serverVersion;
                            }
                        }
                    }
                }
                return Pair.create(minServerVersion, maxServerVersion);
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage());
        }
        return Pair.create(0L, 0L);
    }


    // inner classes
    private final class ServiceHandler extends Handler {
        private ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message message) {
            observables = new ArrayList<>();
            handleSync();
        }
    }

    private class ResponseParcel {
        private JSONObject jsonObject;
        private Pair<Long, Long> serverVersionPair;

        private ResponseParcel(JSONObject jsonObject, Pair<Long, Long> serverVersionPair) {
            this.jsonObject = jsonObject;
            this.serverVersionPair = serverVersionPair;
            Log.e(TAG, "ResponseParcel: "+ jsonObject.toString() );
        }

        private JSONObject getJsonObject() {
            return jsonObject;
        }

        private Pair<Long, Long> getServerVersionPair() {
            return serverVersionPair;
        }
    }

    private int downloadPhoto(String fileURL, String saveDir) throws Exception {
        final int BUFFER_SIZE = 4096;

        URL obj = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        httpConn.setRequestMethod("GET");

        //add request header
        httpConn.setRequestProperty("username",BidanApplication.getInstance().context().allSharedPreferences().fetchRegisteredANM());
        httpConn.setRequestProperty("password",BidanApplication.getInstance().context().allSettings().fetchANMPassword());

        int responseCode = httpConn.getResponseCode();
        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();

            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10,
                            disposition.length() - 1);
                }
            } else {
                // extracts file name from URL
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length());
            }

            Log.d(TAG, "sendGet: Content-Type = " + contentType);
            Log.d(TAG, "sendGet: Content-Disposition = " + disposition);
            Log.d(TAG, "sendGet: contentLength = " + contentLength);
            Log.d(TAG, "sendGet: fileName = " + fileName);

            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = saveDir + File.separator + fileName;

            Log.d(TAG, "sendGet: saveFilePath = " + saveFilePath);

            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            Log.d(TAG, "sendGet: File downloaded");
        } else {
            Log.e(TAG, "sendGet: No file to download. Server replied HTTP code: +"+ responseCode);
        }
        httpConn.disconnect();
        return responseCode;
    }

}
