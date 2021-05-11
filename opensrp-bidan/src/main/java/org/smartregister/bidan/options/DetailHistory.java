package org.smartregister.bidan.options;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DetailHistory {
    private List<Map<String, Object>> details = new ArrayList<>();
    private String kunjunganKe;
    private int id = -1;

    public DetailHistory() {
    }

    public DetailHistory(String kunjunganKe, List<Map<String, Object>> detail) {
        this.kunjunganKe = kunjunganKe;
        this.details = detail;
        id = 1;
    }

    public DetailHistory(String kunjunganKe) {
        this.kunjunganKe = kunjunganKe;

    }

    public List<Map<String, Object>> getDetails() {
        return details;
    }

    public void setDetails(List<Map<String, Object>> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return kunjunganKe;
    }

    public static class DetailHistoryAdapter extends ArrayAdapter<DetailHistory> {
        private List<DetailHistory> detailHistories = new ArrayList<>();

        public DetailHistoryAdapter(@NonNull @NotNull Context context, int resource, @NonNull @NotNull List<DetailHistory> objects) {
            super(context, resource, objects);
            detailHistories = objects;
        }

        @Override
        public boolean isEnabled(int position) {
            return detailHistories.get(position).id >= 0 || detailHistories.get(position).details.size() > 0;
        }
    }
}
