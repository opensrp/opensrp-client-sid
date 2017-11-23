package org.smartregister.bidan_cloudant.sync;

import org.smartregister.domain.FetchStatus;

public interface AfterFetchListener {
    void afterFetch(FetchStatus fetchStatus);
}
