package org.smartregister.bidan.options;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.bidan.R;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class HistoryDetailAdapter extends ArrayAdapter<HistoryDetailAdapter.HistoryDetailData> implements AdapterView.OnItemSelectedListener {
    private final Activity context;
    private final ObjectMapper mapper = new ObjectMapper();
    private final List<JSONObject> detailEvents;
    private final String form;
    final TableLayout table;
    final LayoutInflater inflater;
    final LinearLayout baseHistoryLayout;
    private final int startField;
    private final String keyTanggalKunjungan;

    public HistoryDetailAdapter(Activity context, List<JSONObject> detailEvents, String form, int startField, String keyTanggalKunjungan) {
        super(context, android.R.layout.simple_spinner_item);
        this.context = context;
        this.startField = startField;
        this.detailEvents = detailEvents;
        this.form = form;
        baseHistoryLayout = findViewById(R.id.history_detail);
        inflater = LayoutInflater.from(context);
        table = baseHistoryLayout.findViewById(R.id.base_tbl_history_detail_layout);
        this.keyTanggalKunjungan = keyTanggalKunjungan;
        init();
    }

    private <T extends View> T findViewById(int id) {
        return context.findViewById(id);
    }

    private Map<String, Object> open(String type) throws IOException {
        return mapper.readValue(context.getAssets().open("www/form/" + form + "/" + type + ".json"), new TypeReference<Map<String, Object>>() {
        });
    }

    private String getTanggalKunjungan(JSONObject val) {
        String result = "";
        try {
            if (val.getString(keyTanggalKunjungan) != null && !val.getString(keyTanggalKunjungan).trim().isEmpty()) {
                result = val.getString(keyTanggalKunjungan);
            }
            if (val.has("kunjunganKe") && val.getString("kunjunganKe") != null) {
                result = StringUtils.leftPad(val.getString("kunjunganKe"), 2, '0') + result;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void init() {
        try {
            if (detailEvents.size() > 0) {
                add(new HistoryDetailData("Silahkan Pilih Kunjungan"));
                AtomicInteger integer = new AtomicInteger(1);
                Collections.sort(detailEvents, new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject o1, JSONObject o2) {
                        return getTanggalKunjungan(o1).compareTo(getTanggalKunjungan(o2));
                    }
                });
                for (final JSONObject detailEvent : detailEvents) {

                    Map<String, Object> formDefinition = open("form_definition");
                    Map<String, Object> detailForm = open("form");
                    List<Map<String, Object>> detailChildrenForm = (List<Map<String, Object>>) detailForm.get("children");
                    Map<String, Object> labelForm = new LinkedHashMap<>();
                    for (Map<String, Object> f : detailChildrenForm) {
                        String label = "hidden";
                        if (f.containsKey("label") && f.get("label") != null) {
                            if (!f.get("type").equals("hidden"))
                                label = (String) ((Map<String, Object>) f.get("label")).get("Bahasa");
                        }
                        labelForm.put((String) f.get("name"), label);
                    }
                    Map<String, Object> form = (Map<String, Object>) formDefinition.get("form");
                    List<Map<String, Object>> fields = (List<Map<String, Object>>) form.get("fields");
                    List<Map<String, Object>> results = new LinkedList<>();
                    results.add(new LinkedHashMap<String, Object>() {{
                        put("id", keyTanggalKunjungan);
                        put("bind", keyTanggalKunjungan);
                        put("label", keyTanggalKunjungan);
                        put("value", detailEvent.getString(keyTanggalKunjungan));
                    }});

                    for (int i = 0; i < fields.size(); i++) {
                        Map<String, Object> field = fields.get(i);
                        if (field == null || field.get("bind") == null)
                            continue;
                        String[] binds = ((String) field.get("bind")).split("/");
                        String bind = binds[binds.length - 1];
                        String label = (String) labelForm.get(bind);
                        if (label == null)
                            continue;
                        if (label.equals("hidden"))
                            continue;
                        if (labelForm.get(bind) == null)
                            label = bind;
                        Map<String, Object> result = new LinkedHashMap<>();
                        String name = (String) field.get("name");
                        result.put("id", name);

                        result.put("bind", bind);
                        result.put("label", label.replace(":", "").replace("?", "").trim());
                        result.put("value", "-");
                        if (detailEvent.has(name) && detailEvent.getString(name) != null && !detailEvent.getString(name).trim().isEmpty()) {
                            result.put("value", detailEvent.getString((String) field.get("name")));
                            results.add(result);
                        }

                    }
                    Integer kunjKe = integer.getAndIncrement();
                    if (detailEvent.has("kunjunganKe") && detailEvent.getString("kunjunganKe") != null && !detailEvent.getString("kunjunganKe").trim().isEmpty()) {
                        kunjKe = Integer.parseInt(detailEvent.getString("kunjunganKe"));
                        integer.set(kunjKe);
                    }
                    add(new HistoryDetailData("Kunjungan Ke " + kunjKe + " (" + detailEvent.getString(keyTanggalKunjungan) + ")", results));
                }
            } else {
                add(new HistoryDetailData("Tidak ada detail history"));
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        final HistoryDetailData item = getItem(position);
        table.removeAllViews();
        for (Map<String, Object> result : item.getDetails()) {
            final TableRow second = (TableRow) inflater.inflate(R.layout.row_history, null);
            final TextView secondLabel = (TextView) second.getChildAt(0);
            secondLabel.setText((String) result.get("label"));
            final TextView secondValue = (TextView) second.getChildAt(1);
            secondValue.setText((String) result.get("value"));
            table.addView(second);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public static class HistoryDetailData {
        private List<Map<String, Object>> details = new ArrayList<>();
        private String kunjunganKe;
        private int id = -1;

        public HistoryDetailData() {
        }

        public HistoryDetailData(String kunjunganKe, List<Map<String, Object>> detail) {
            this.kunjunganKe = kunjunganKe;
            this.details = detail;
            id = 1;
        }

        public HistoryDetailData(String kunjunganKe) {
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
    }
}
