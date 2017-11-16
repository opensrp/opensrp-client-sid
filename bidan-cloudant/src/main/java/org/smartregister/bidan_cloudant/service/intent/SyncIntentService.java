package org.smartregister.bidan_cloudant.service.intent;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.AllConstants;
import org.smartregister.bidan_cloudant.application.BidanApplication;
import org.smartregister.bidan_cloudant.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.bidan_cloudant.sync.BidanAfterFetchListener;
import org.smartregister.bidan_cloudant.sync.BidanClientProcessor;
import org.smartregister.bidan_cloudant.sync.ECSyncUpdater;
import org.smartregister.domain.DownloadStatus;
import org.smartregister.domain.FetchStatus;
import org.smartregister.service.ActionService;
import org.smartregister.service.AllFormVersionSyncService;
import org.smartregister.util.Utils;

import java.util.Calendar;

import util.NetworkUtils;

/**
 * Created by sid-tech on 11/14/17.
 */

public class SyncIntentService extends IntentService {

    private Context context;
    private ActionService actionService;
    private BidanAfterFetchListener bidanAfterFetchListener;
    private AllFormVersionSyncService allFormVersionSyncService;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public SyncIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        sendSyncStatusBroadcastMessage(context, FetchStatus.fetchStarted);
        if (BidanApplication.getInstance().context().IsUserLoggedOut()) {
            drishtiLogInfo("Not updating from server as user is not logged in.");
            return;
        }

        FetchStatus fetchStatus = doSync();

        if (fetchStatus.equals(FetchStatus.nothingFetched) || fetchStatus.equals(FetchStatus.fetched)) {
            ECSyncUpdater ecSyncUpdater = ECSyncUpdater.getInstance(context);
            ecSyncUpdater.updateLastCheckTimeStamp(Calendar.getInstance().getTimeInMillis());
        }
        bidanAfterFetchListener.afterFetch(fetchStatus);
        sendSyncStatusBroadcastMessage(context, fetchStatus);
    }

    private void sendSyncStatusBroadcastMessage(Context context, FetchStatus fetchStatus) {
        Intent intent = new Intent();
        intent.setAction(SyncStatusBroadcastReceiver.ACTION_SYNC_STATUS);
        intent.putExtra(SyncStatusBroadcastReceiver.EXTRA_FETCH_STATUS, fetchStatus);
        context.sendBroadcast(intent);
    }

    private void drishtiLogInfo(String message) {
        org.smartregister.util.Log.logInfo(message);
    }

    private FetchStatus doSync() {
        if (NetworkUtils.isNetworkAvailable()) {
            FetchStatus fetchStatusForForms = sync();
            FetchStatus fetchStatusForActions = actionService.fetchNewActions();
            bidanAfterFetchListener.partialFetch(fetchStatusForActions);

            if (BidanApplication.getInstance().context().configuration().shouldSyncForm()) {

                allFormVersionSyncService.verifyFormsInFolder();
                FetchStatus fetchVersionStatus = allFormVersionSyncService.pullFormDefinitionFromServer();
                DownloadStatus downloadStatus = allFormVersionSyncService.downloadAllPendingFormFromServer();

                if (downloadStatus == DownloadStatus.downloaded) {
                    allFormVersionSyncService.unzipAllDownloadedFormFile();
                }

                if (fetchVersionStatus == FetchStatus.fetched || downloadStatus == DownloadStatus.downloaded) {
                    return FetchStatus.fetched;
                }
            }

            return (fetchStatusForForms == FetchStatus.fetched) ? fetchStatusForActions : fetchStatusForForms;

        }

        return FetchStatus.noConnection;
    }

    private FetchStatus sync() {

        try {
            // Fetch locations
//            String locations = Utils.getPreference(context, LocationPickerView.PREF_TEAM_LOCATIONS, "");
            String locations = Utils.getPreference(context, "PREF_TEAM_LOCATIONS", "");

            if (StringUtils.isBlank(locations)) {
                return FetchStatus.fetchedFailed;
            }

            pushToServer();
            FetchStatus formActionsFetctStatus = pullFormAndActionsFromServer(locations);
//            pullStockFromServer();

            return formActionsFetctStatus;
        } catch (Exception e) {
            Log.e(getClass().getName(), "", e);
            return FetchStatus.fetchedFailed;
        }

    }

    private void pushToServer() {
//        pushECToServer();
//        pushReportsToServer();
//        pushStockToServer();
//        startSyncValidation();
    }

    private FetchStatus pullFormAndActionsFromServer(String locations) throws Exception {
        int totalCount = 0;
        ECSyncUpdater ecUpdater = ECSyncUpdater.getInstance(context);

        while (true) {
            long startSyncTimeStamp = ecUpdater.getLastSyncTimeStamp();
            int eCount = ecUpdater.fetchAllClientsAndEvents(AllConstants.SyncFilters.FILTER_LOCATION_ID, locations);
            totalCount += eCount;
            if (eCount < 0) {
                return FetchStatus.fetchedFailed;
            } else if (eCount == 0) {
                break;
            }

            long lastSyncTimeStamp = ecUpdater.getLastSyncTimeStamp();
            BidanClientProcessor.getInstance(context).processClient(ecUpdater.allEvents(startSyncTimeStamp, lastSyncTimeStamp));
            Log.i(getClass().getName(), "Sync count:  " + eCount);
            bidanAfterFetchListener.partialFetch(FetchStatus.fetched);
        }


        if (totalCount == 0) {
            return FetchStatus.nothingFetched;
        } else if (totalCount < 0) {
            return FetchStatus.fetchedFailed;
        } else {
            return FetchStatus.fetched;
        }
    }
    

}
