package org.smartregister.vaksinator.sync;

import org.smartregister.domain.FetchStatus;

public interface AfterFetchListener {
    void afterFetch(FetchStatus fetchStatus);
}
