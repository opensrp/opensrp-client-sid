package org.smartregister.bidan.sync;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;

import com.cloudant.http.interceptors.BasicAuthInterceptor;
import com.cloudant.sync.datastore.Datastore;
import com.cloudant.sync.event.Subscribe;
import com.cloudant.sync.notifications.ReplicationCompleted;
import com.cloudant.sync.notifications.ReplicationErrored;
import com.cloudant.sync.replication.PullFilter;
import com.cloudant.sync.replication.Replicator;
import com.cloudant.sync.replication.ReplicatorBuilder;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.smartregister.bidan.utils.AllConstantsINA;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.AssetHandler;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;

//import org.smartregister.bidan.utils.AllConstantsINA;


public class CloudantSyncHandler {
    private static final String LOG_TAG = "CloudantSyncHandler";
    private static CloudantSyncHandler instance;
    private final Context mContext;
    private final Handler mHandler;
    private Replicator mPushReplicator;
    private Replicator mPullReplicator;
    private CloudantSyncListener mListener;
    private CountDownLatch countDownLatch;
    private String dbURL;

    public CloudantSyncHandler(Context context) {
        mContext = context;
//        ClientProcessor mClientProcessor = new ClientProcessor(mContext);

        // Allow us to switch code called by the ReplicationListener into
        // the main thread so the UI can update safely.
        mHandler = new Handler(Looper.getMainLooper());
        try {
            // Retrieve database host from preferences
            SharedPreferences preferences = PreferenceManager
                    .getDefaultSharedPreferences(mContext);
            AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);
            String locationAnmids = allSharedPreferences.getPreference(allSharedPreferences.fetchRegisteredANM() + "-locationid");
            String port = AllConstantsINA.CloudantSync.COUCHDB_PORT;
            String databaseName = AllConstantsINA.CloudantSync.COUCH_DATABASE_NAME + "_" + locationAnmids.toLowerCase().replace(' ', '_').replace(".", "");
            dbURL = allSharedPreferences.fetchHost("").concat(":").concat(port).concat("/")
                    .concat(databaseName);

            // Replication Filter by provider
//            String designDocumentId = this.replicationFilterSettings();
//            PullFilter pullFilter = null;

//            if (designDocumentId != null) {
//                String filterDoc = designDocumentId.split("/")[1];
//                HashMap<String, String> filterParams = new HashMap<String, String>();
//                filterParams.put(AllConstantsINA.SyncFilters.FILTER_PROVIDER,
//                        allSharedPreferences.fetchRegisteredANM());
//                pullFilter = new PullFilter(
//                        filterDoc.concat("/").concat(AllConstantsINA.SyncFilters.FILTER_PROVIDER),
//                        filterParams);
//            }

            this.reloadReplicationSettings(null);

        } catch (URISyntaxException e) {
            Log.e(LOG_TAG, "Unable to construct remote URI from configuration", e);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception While setting up datastore", e);
        }

    }

    public static CloudantSyncHandler getInstance(Context context) {
        if (instance == null) {
            instance = new CloudantSyncHandler(context);
        }
        return instance;
    }

    //
    // GETTERS AND SETTERS
    //

    /**
     * Sets the listener for replication callbacks as a weak reference.
     *
     * @param listener {@link CloudantSyncListener} to receive callbacks.
     */
    public void setReplicationListener(CloudantSyncListener listener) {
        this.mListener = listener;
    }

    //
    // MANAGE REPLICATIONS
    //

    /**
     * <p>Stops running replications.</p>
     * <p/>
     * <p>The stop() methods stops the replications asynchronously, see the
     * replicator docs for more information.</p>
     */
    public void stopAllReplications() {
        if (this.mPullReplicator != null) {
            this.mPullReplicator.stop();
        }
        if (this.mPushReplicator != null) {
            this.mPushReplicator.stop();
        }
    }

    /**
     * <p>Starts the configured push replication.</p>
     */
    public void startPushReplication() {
        if (this.mPushReplicator != null) {
            this.mPushReplicator.start();
        } else {
            throw new IllegalArgumentException("Push replication not set up correctly");
        }
    }

    /**
     * <p>Starts the configured pull replication.</p>
     */
    public void startPullReplication() {
        if (this.mPullReplicator != null) {
            this.mPullReplicator.start();
        } else {
            throw new IllegalArgumentException("Push replication not set up correctly");
        }
    }

    /**
     * <p>Stops running replications and reloads the replication settings from
     * the app's preferences.</p>
     */
    public void reloadReplicationSettings(PullFilter pullFilter) throws Exception {
        this.stopAllReplications();

        // Set up the new replicator objects
        URI uri = this.createServerURI();

        CloudantDataHandler mCloudantDataHandler = CloudantDataHandler.getInstance(mContext);
        Datastore mDatastore = mCloudantDataHandler.getDatastore();

        ReplicatorBuilder.Pull mPullBuilder = ReplicatorBuilder.pull().to(mDatastore).from(uri);
        ReplicatorBuilder.Push mPushBuilder = ReplicatorBuilder.push().from(mDatastore).to(uri);

        if (pullFilter != null) {
            mPullBuilder.filter(pullFilter);
        }

        String username = AllConstantsINA.CloudantSync.COUCH_DATABASE_USER;
        String password = AllConstantsINA.CloudantSync.COUCH_DATABASE_PASS;

        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            mPullBuilder
                    .addRequestInterceptors(new BasicAuthInterceptor(username + ":" + password));
            mPushBuilder
                    .addRequestInterceptors(new BasicAuthInterceptor(username + ":" + password));
        }

        mPullReplicator = mPullBuilder.build();
        mPushReplicator = mPushBuilder.build();

        mPushReplicator.getEventBus().register(this);
        mPullReplicator.getEventBus().register(this);

        Log.d(LOG_TAG, "Set up replicators for URI:" + uri.toString());
    }

    /**
     * <p>Returns the URI for the remote database, based on the app's
     * configuration.</p>
     *
     * @return the remote database's URI
     * @throws URISyntaxException if the settings give an invalid URI
     */
    private URI createServerURI() throws URISyntaxException {
        // We recommend always using HTTPS to talk to Cloudant.
        return new URI(dbURL);
    }

    //
    // REPLICATIONLISTENER IMPLEMENTATION
    //

    /**
     * Calls the SecuredActivity's replicationComplete method on the main thread,
     * as the complete() callback will probably come from a replicator worker
     * thread.
     */
    @Subscribe
    public void complete(final ReplicationCompleted rc) {
        // Call the logic to break down CE into case models
        try {
            if (rc.documentsReplicated > 0) {
                ClientProcessor.getInstance(mContext.getApplicationContext()).processClient();
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString(), e);
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                // Fire callback if a replicationListener exists
                if (mListener != null) {
                    mListener.replicationComplete();
                }

                if (countDownLatch != null) {
                    countDownLatch.countDown();
                }

                // Fire this incase the replication was lauched from an intent service
                Intent localIntent = new Intent(
                        AllConstantsINA.CloudantSync.ACTION_REPLICATION_COMPLETED);
                // Puts the status into the Intent
                localIntent.putExtra(AllConstantsINA.CloudantSync.DOCUMENTS_REPLICATED,
                        rc.documentsReplicated);
                localIntent.putExtra(AllConstantsINA.CloudantSync.BATCHES_REPLICATED,
                        rc.batchesReplicated);
                // Broadcasts the Intent to receivers in this app.
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(localIntent);
            }
        });
    }

    /**
     * Calls the SecuredActivity's replicationComplete method on the main thread,
     * as the error() callback will probably come from a replicator worker
     * thread.
     */
    @Subscribe
    public void error(final ReplicationErrored re) {
        Log.e(LOG_TAG, "Replication error:", re.errorInfo.getException());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                // Fire callback if a replicationListener exists
                if (mListener != null) {
                    mListener.replicationError();
                }

                if (countDownLatch != null) {
                    countDownLatch.countDown();
                }

                //Fire this incase the replication was lauched from an intent service
                Intent localIntent = new Intent(AllConstantsINA.CloudantSync.ACTION_REPLICATION_ERROR);
                // Puts the status into the Intent
                localIntent.putExtra(AllConstantsINA.CloudantSync.REPLICATION_ERROR,
                        re.errorInfo.getException().getMessage());
                // Broadcasts the Intent to receivers in this app.
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(localIntent);
            }
        });
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    public String replicationFilterSettings() {
        try {
            String localJsonString = getFileContents("sync_filters.json");
            JsonObject localJson = new JsonParser().parse(localJsonString).getAsJsonObject();

            String designDocumentId = localJson.get("_id").getAsString();
            if (designDocumentId == null || designDocumentId.isEmpty() || !designDocumentId
                    .contains("_design/")) {
                return null;
            }

            JsonObject serverJson = getReplicationFiler(designDocumentId);
            if (serverJson == null) {
                return null;
            }

            JsonElement idElement = serverJson.get("_id");
            if (idElement != null && idElement.getAsString().equals(designDocumentId)) {
                //Compare filters and update if the filters are different
                JsonObject syncFiltersElement = localJson.get("filters").getAsJsonObject();
                JsonObject serverFiltersElement = serverJson.get("filters").getAsJsonObject();
                if (!syncFiltersElement.equals(serverFiltersElement)) {
                    String rev = serverJson.get("_rev").getAsString();
                    localJson.addProperty("_rev", rev);
                    setReplicationFilter(localJson, designDocumentId);
                }
                return designDocumentId;
            }

            // Define replication filter
            boolean result = setReplicationFilter(localJson, designDocumentId);
            if (result) {
                return designDocumentId;
            } else {
                return null;
            }

        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception While getting sync filters json", e);
            return null;
        }

    }

    public Boolean setReplicationFilter(JsonObject jsonObject, String designDocumentId) {
        try {

            Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls()
                    .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();

            String json = gson.toJson(jsonObject);

            URL obj = new URL(dbURL.concat("/".concat(designDocumentId)));
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            conn.setRequestMethod("PUT");

            String authEncoded = getAuthorization();
            if (authEncoded != null) {
                String basicAuth = "Basic " + authEncoded;
                conn.setRequestProperty("Authorization", basicAuth);
            }

            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write(json);
            out.close();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader(conn.getInputStream()));

            JsonObject rootJson = root.getAsJsonObject();

            return rootJson.has("ok") && rootJson.has("id") && rootJson.get("id").getAsString().equals(designDocumentId) && rootJson.get("ok").getAsBoolean();

        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception While setting replication filter", e);
            return null;
        }
    }

    public JsonObject getReplicationFiler(String designDocumentId) {
        try {
            HttpClient httpclient = new DefaultHttpClient();

            HttpGet get = new HttpGet(dbURL.concat("/".concat(designDocumentId)));

            String authEncoded = getAuthorization();
            if (authEncoded != null) {
                String basicAuth = "Basic " + authEncoded;
                get.setHeader("Authorization", basicAuth);
            }

            HttpResponse response = httpclient.execute(get);
            HttpEntity entity = response.getEntity();
            InputStream in = entity.getContent();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader(in));

            return root.getAsJsonObject();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception While getting replication filter", e);
            return null;
        }
    }

    public String getAuthorization() {

        String username = AllConstantsINA.CloudantSync.COUCH_DATABASE_USER;
        String password = AllConstantsINA.CloudantSync.COUCH_DATABASE_PASS;

        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            String authenticationData = username + ":" + password;
            return Base64.encodeToString(authenticationData.getBytes(Charset.forName("utf-8")),
                    Base64.DEFAULT);
        }

        return null;
    }

    public String getFileContents(String fileName) {
        return AssetHandler.readFileFromAssetsFolder(fileName, mContext);
    }
}