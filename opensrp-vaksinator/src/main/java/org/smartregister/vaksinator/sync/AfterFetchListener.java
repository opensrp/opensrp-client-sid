package org.smartregister.vaksinator.sync;

import org.smartregister.domain.FetchStatus;

public interface AfterFetchListener {
    public void afterFetch(FetchStatus fetchStatus);
}
