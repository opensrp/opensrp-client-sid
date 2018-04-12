package org.smartregister.vaksinator.utils;

/**
 * Created by wildan on 10/30/17.
 */

public class AllConstantsINA {
    // Flurry Bidan Testing 2
    public static final String FLURRY_KEY = "9RDM5TWW4HJ5QH8TNJ3P";
    public static int SLEEP_TIME = 15000;
    public static final int WAITING_TIME = 5000;
    public static boolean IDLE = false;

    public static class SyncFilters {
        // These pull filters must be defined in your couchDB server for them to work
        // Filters
        public static final String FILTER_LOCATION_ID = "locationId";
        public static final String FILTER_PROVIDER = "providerId";

        //filter by team e.g. team = user1,user2,user3
        public static final String FILTER_TEAM = "team";

    }

    public static class CloudantSync {
        public static final String ACTION_DATABASE_CREATED = "org.smartregister.DATABASE_CREATED_ACTION";
        public static final String ACTION_REPLICATION_ERROR = "org.smartregister.REPLICATION_ERROR_ACTION";
        public static final String ACTION_REPLICATION_COMPLETED = "org.smartregister.REPLICATION_COMPLETED_ACTION";
        public static final String REPLICATION_ERROR = "REPLICATION_ERROR";
        public static final String DOCUMENTS_REPLICATED = "DOCUMENTS_REPLICATED";
        public static final String BATCHES_REPLICATED = "BATCHES_REPLICATED";
        public static final String COUCHDB_PORT = "COUCHDB_PORT";
        public static final String COUCH_DATABASE_NAME = "COUCH_DATABASE_NAME";
        public static final String COUCH_DATABASE_USER = "COUCH_DATABASE_USER";
        public static final String COUCH_DATABASE_PASS = "COUCH_DATABASE_PASS";
    }

}
