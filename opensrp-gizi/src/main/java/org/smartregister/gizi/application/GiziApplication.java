package org.smartregister.gizi.application;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.configurableviews.helper.ConfigurableViewsHelper;
import org.smartregister.configurableviews.helper.JsonSpecHelper;
import org.smartregister.configurableviews.model.MainConfig;
import org.smartregister.configurableviews.repository.ConfigurableViewsRepository;
import org.smartregister.configurableviews.service.PullConfigurableViewsIntentService;
import org.smartregister.configurableviews.util.Constants;
import org.smartregister.gizi.GiziEventBusIndex;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.Repository;
import org.smartregister.sync.DrishtiSyncScheduler;
import org.smartregister.gizi.activity.LoginActivity;
import org.smartregister.gizi.event.LanguageConfigurationEvent;
import org.smartregister.gizi.event.TriggerSyncEvent;
import org.smartregister.gizi.event.ViewConfigurationSyncCompleteEvent;
import org.smartregister.gizi.receiver.GiziSyncBroadcastReceiver;
import org.smartregister.gizi.repository.BMIRepository;
import org.smartregister.gizi.repository.ResultDetailsRepository;
import org.smartregister.gizi.repository.ResultsRepository;
import org.smartregister.gizi.repository.GiziRepository;
import org.smartregister.gizi.service.SyncService;
import org.smartregister.gizi.util.Utils;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.receiver.TimeChangedBroadcastReceiver;

import util.ServiceTools;
import util.GiziConstants;
import util.GiziConstants.KEY;

import static org.smartregister.util.Log.logError;
import static org.smartregister.util.Log.logInfo;

/**
 * Created by keyman on 23/08/2017.
 */
public class GiziApplication extends DrishtiApplication {

    private static JsonSpecHelper jsonSpecHelper;

    private ConfigurableViewsRepository configurableViewsRepository;
    private EventClientRepository eventClientRepository;
    private ResultsRepository resultsRepository;
    private static CommonFtsObject commonFtsObject;
    private ResultDetailsRepository resultDetailsRepository;
    private ConfigurableViewsHelper configurableViewsHelper;
    private BMIRepository bmiRepository;

    private static final String TAG = GiziApplication.class.getCanonicalName();
    private String password;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        context = Context.getInstance();

        context.updateApplicationContext(getApplicationContext());
        context.updateCommonFtsObject(createCommonFtsObject());

        //Initialize Modules
        CoreLibrary.init(context);
        ConfigurableViewsLibrary.init(context, getRepository());

        DrishtiSyncScheduler.setReceiverClass(GiziSyncBroadcastReceiver.class);

        startPullConfigurableViewsIntentService(getApplicationContext());
        try {
            Utils.saveLanguage("en");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        //Initialize JsonSpec Helper
        this.jsonSpecHelper = new JsonSpecHelper(this);

        setUpEventHandling();

    }

    public static synchronized GiziApplication getInstance() {
        return (GiziApplication) mInstance;
    }

    @Override
    public Repository getRepository() {
        try {
            if (repository == null) {
                repository = new GiziRepository(getInstance().getApplicationContext(), context);
                getConfigurableViewsRepository();
            }
        } catch (UnsatisfiedLinkError e) {
            logError("Error on getRepository: " + e);

        }
        return repository;
    }

    public String getPassword() {
        if (password == null) {
            String username = getContext().userService().getAllSharedPreferences().fetchRegisteredANM();
            password = getContext().userService().getGroupId(username);
        }
        return password;
    }

    @Override
    public void logoutCurrentUser() {

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getApplicationContext().startActivity(intent);
        context.userService().logoutSession();
    }

    public static JsonSpecHelper getJsonSpecHelper() {
        return getInstance().jsonSpecHelper;
    }

    public Context getContext() {
        return context;
    }

    protected void cleanUpSyncState() {
        DrishtiSyncScheduler.stop(getApplicationContext());
        context.allSharedPreferences().saveIsSyncInProgress(false);
    }

    @Override
    public void onTerminate() {
        logInfo("Application is terminating. Stopping Sync scheduler and resetting isSyncInProgress setting.");
        cleanUpSyncState();
        TimeChangedBroadcastReceiver.destroy(this);
        super.onTerminate();
    }

    public void startPullConfigurableViewsIntentService(android.content.Context context) {
        Intent intent = new Intent(context, PullConfigurableViewsIntentService.class);
        context.startService(intent);
    }

    public static CommonFtsObject createCommonFtsObject() {
        if (commonFtsObject == null) {
            commonFtsObject = new CommonFtsObject(getFtsTables());
            for (String ftsTable : commonFtsObject.getTables()) {
                commonFtsObject.updateSearchFields(ftsTable, getFtsSearchFields());
                commonFtsObject.updateSortFields(ftsTable, getFtsSortFields());
            }
        }
        return commonFtsObject;
    }

    private static String[] getFtsTables() {
        return new String[]{GiziConstants.PATIENT_TABLE_NAME};
    }

    private static String[] getFtsSearchFields() {
        return new String[]{KEY.PARTICIPANT_ID, KEY.PROGRAM_ID, KEY.FIRST_NAME, KEY.LAST_NAME};

    }

    private static String[] getFtsSortFields() {
        return new String[]{KEY.PARTICIPANT_ID, KEY.PROGRAM_ID, KEY.FIRST_NAME,
                KEY.LAST_INTERACTED_WITH, KEY.PRESUMPTIVE, KEY.CONFIRMED_TB, KEY.FIRST_ENCOUNTER,
                KEY.DIAGNOSIS_DATE, KEY.TREATMENT_INITIATION_DATE, KEY.DATE_REMOVED};
    }


    public ConfigurableViewsRepository getConfigurableViewsRepository() {
        if (configurableViewsRepository == null)
            configurableViewsRepository = new ConfigurableViewsRepository(getRepository());
        return configurableViewsRepository;
    }

    public EventClientRepository getEventClientRepository() {
        if (eventClientRepository == null) {
            eventClientRepository = new EventClientRepository(getRepository());
        }
        return eventClientRepository;
    }

    public ResultsRepository getResultsRepository() {
        if (resultsRepository == null) {
            resultsRepository = new ResultsRepository(getRepository());
        }
        return resultsRepository;
    }

    public ResultDetailsRepository getResultDetailsRepository() {
        if (resultDetailsRepository == null) {
            resultDetailsRepository = new ResultDetailsRepository(getRepository());
        }
        return resultDetailsRepository;
    }

    public ConfigurableViewsHelper getConfigurableViewsHelper() {
        if (configurableViewsHelper == null) {
            configurableViewsHelper = new ConfigurableViewsHelper(getConfigurableViewsRepository(),
                    getJsonSpecHelper(), getApplicationContext());
        }
        return configurableViewsHelper;
    }

    public BMIRepository getBmiRepository() {
        if (bmiRepository == null) {
            bmiRepository = new BMIRepository(getRepository());
        }
        return bmiRepository;
    }

    private void setUpEventHandling() {

        try {

            EventBus.builder().addIndex(new GiziEventBusIndex()).installDefaultEventBus();
            LocalBroadcastManager.getInstance(this).registerReceiver(syncCompleteMessageReceiver, new IntentFilter(PullConfigurableViewsIntentService.EVENT_SYNC_COMPLETE));

        } catch
                (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        EventBus.getDefault().register(this);

    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void triggerSync(TriggerSyncEvent event) {
        if (event != null) {
            startPullConfigurableViewsIntentService(this);
            ServiceTools.startService(getApplicationContext(), SyncService.class);
        }

    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void setServerLanguage(LanguageConfigurationEvent event) {
        //Set Language
        MainConfig config = GiziApplication.getJsonSpecHelper().getMainConfiguration();
        if (config != null && config.getLanguage() != null && event.isFromServer()) {
            Utils.saveLanguage(config.getLanguage());
        }
    }

    // This Broadcast Receiver is the handler called whenever an Intent with an action named PullConfigurableViewsIntentService.EVENT_SYNC_COMPLETE is broadcast.
    private BroadcastReceiver syncCompleteMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(android.content.Context context, Intent intent) {
            // Retrieve the extra data included in the Intent

            int recordsRetrievedCount = intent.getIntExtra(Constants.INTENT_KEY.SYNC_TOTAL_RECORDS, 0);
            if (recordsRetrievedCount > 0) {
                LanguageConfigurationEvent event = new LanguageConfigurationEvent(true);//To Do add check for language configs
                Utils.postEvent(event);
            }

            Utils.postEvent(new ViewConfigurationSyncCompleteEvent());

            String lastSyncTime = intent.getStringExtra(org.smartregister.configurableviews.util.Constants.INTENT_KEY.LAST_SYNC_TIME_STRING);

            Utils.writePrefString(context, org.smartregister.configurableviews.util.Constants.INTENT_KEY.LAST_SYNC_TIME_STRING, lastSyncTime);

        }
    };

}
