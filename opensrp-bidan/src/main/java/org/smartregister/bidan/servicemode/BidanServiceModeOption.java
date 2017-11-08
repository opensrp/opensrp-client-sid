package org.smartregister.bidan.servicemode;

import android.util.Log;

import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity.ClientsHeaderProvider;
import org.smartregister.view.dialog.ServiceModeOption;

import java.util.Arrays;

public class BidanServiceModeOption extends ServiceModeOption {

    private final String TAG = BidanServiceModeOption.class.getName();
    private final String name;
    private final int[] headerTextResourceIds;
    private final int[] columnWeights;

    public BidanServiceModeOption(SmartRegisterClientsProvider provider, String name, int[] headerTextResourceIds, int[] columnWeights) {
        super(provider);
        this.name = name;
        this.headerTextResourceIds = headerTextResourceIds;
        this.columnWeights = columnWeights;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public ClientsHeaderProvider getHeaderProvider() {
        return new ClientsHeaderProvider() {
            @Override
            public int count() {
                return headerTextResourceIds.length;
            }

            @Override
            public int weightSum() {
                int sum = 0;
                for (int columnWeight : columnWeights) {
                    sum += columnWeight;
                }
                return sum;
            }

            @Override
            public int[] weights() {
                return columnWeights;
            }

            @Override
            public int[] headerTextResourceIds() {
                return headerTextResourceIds;
            }
        };
    }
}
