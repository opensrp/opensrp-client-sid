package org.smartregister.bidan.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.Context;
import org.smartregister.bidan.R;
import org.smartregister.bidan.options.DetailHistory;
import org.smartregister.bidan.repository.EventRepository;
import org.smartregister.bidan.utils.CameraPreviewActivity;
import org.smartregister.bidan.utils.Support;
import org.smartregister.bidan.utils.Tools;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static org.smartregister.util.StringUtil.humanize;

//import org.smartregister.bidan.lib.FlurryFacade;

/**
 * Created by Iq on 07/09/16
 */
public class DetailChildActivity extends Activity {

    private static final String TAG = DetailChildActivity.class.getName();
    public static CommonPersonObjectClient childclient;
    private final String defaultJenisKunjungan = "Pilih Jenis Kunjungan";
    //    private static String entityid;
//    @Bind(R.id.childdetailprofileview)
//    ImageView childview;
    private String userId;

//    public DetailChildActivity(String userId) {
//        this.userId = userId;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anak_detail_activity);

        userId = childclient.getDetails().get("base_entity_id");
        final TextView show_history = findViewById(R.id.tv_detail_history);
        final TextView show_basic = findViewById(R.id.tv_child_detail_information);
        final Spinner spinnerHistory = findViewById(R.id.history_ke);
        Spinner spinnerChildType = findViewById(R.id.jenis_kunjungan);
        final ArrayAdapter<String> jenisAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{defaultJenisKunjungan, "Kunjungan neonatal", "Kunjungan Balita"});
        spinnerChildType.setAdapter(jenisAdapter);

        final ObjectMapper mapper = new ObjectMapper();
        spinnerChildType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    final String item = jenisAdapter.getItem(position);
                    if (!item.equals(defaultJenisKunjungan)) {
                        final List<JSONObject> detailEvents = EventRepository.getEventsByBaseIdAndEventType(item, userId);
//                        List<DetailHistory> histories = new ArrayList<>();
                        final ArrayAdapter<DetailHistory> data = new ArrayAdapter<>(DetailChildActivity.this, android.R.layout.simple_spinner_item);
                        LinearLayout baseHistoryLayout = findViewById(R.id.history_detail);
                        final TableLayout table = baseHistoryLayout.findViewById(R.id.base_tbl_history_detail_layout);
                        final LayoutInflater inflater = LayoutInflater.from(DetailChildActivity.this);
                        if (detailEvents.size() > 0) {
                            data.add(new DetailHistory("Silahkan Pilih Tanggal"));

                            String keyTanggalKunjungan = "tanggalKunjunganBayiPerbulan";
                            int startField = 5;
                            String formType = "kohort_bayi_kunjungan";
                            if (item.toLowerCase().endsWith("balita")) {
                                formType = "kohort_balita_kunjungan";
                                startField = 6;
                            }
                            AtomicInteger integer = new AtomicInteger(1);
                            for (final JSONObject detailEvent : detailEvents) {
                                Map<String, Object> formDefinition = mapper.readValue(getAssets().open("www/form/" + formType + "/form_definition.json"), new TypeReference<Map<String, Object>>() {
                                });
                                Map<String, Object> detailForm = mapper.readValue(getAssets().open("www/form/" + formType + "/form.json"), new TypeReference<Map<String, Object>>() {
                                });
                                List<Map<String, Object>> detailChildrenForm = (List<Map<String, Object>>) detailForm.get("children");
                                Map<String, Object> labelForm = new LinkedHashMap<>();
                                for (Map<String, Object> f : detailChildrenForm) {
                                    if (f.containsKey("label") && f.get("label") != null)
                                        labelForm.put((String) f.get("name"), ((Map<String, Object>) f.get("label")).get("Bahasa"));
                                }
                                Map<String, Object> form = (Map<String, Object>) formDefinition.get("form");
                                List<Map<String, Object>> fields = (List<Map<String, Object>>) form.get("fields");
                                List<Map<String, Object>> results = new LinkedList<>();
//                                final LinkedHashMap<String, Object> firstDetail = new LinkedHashMap<>();
//                                firstDetail.put("id", keyTanggalKunjungan);
//                                firstDetail.put("bind", keyTanggalKunjungan);
//                                firstDetail.put("label", "Tanggal Kunjungan");
//                                firstDetail.put("value", detailEvent.getString(keyTanggalKunjungan));
//                                results.add(firstDetail);

                                for (int i = startField; i < fields.size(); i++) {
                                    Map<String, Object> field = fields.get(i);
                                    String[] binds = ((String) field.get("bind")).split("/");
                                    String bind = binds[binds.length - 1];
                                    if (labelForm.get(bind) == null)
                                        continue;
                                    Map<String, Object> result = new LinkedHashMap<>();
                                    String name = (String) field.get("name");
                                    result.put("id", name);

                                    result.put("bind", bind);
                                    result.put("label", labelForm.get(bind));
                                    result.put("value", "-");
                                    if (detailEvent.has(name) && detailEvent.getString(name) != null && !detailEvent.getString(name).trim().isEmpty()) {
                                        result.put("value", detailEvent.getString((String) field.get("name")));
                                        results.add(result);
                                    }

                                }
                                data.add(new DetailHistory("Kunjungan Ke " + integer.getAndIncrement() + " (" + detailEvent.getString(keyTanggalKunjungan) + ")", results));
                            }
                        } else {
                            data.add(new DetailHistory("Tidak ada detail history " + item));
                        }

                        spinnerHistory.setAdapter(data);
                        spinnerHistory.setSelection(0);
                        spinnerHistory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                final DetailHistory item = data.getItem(position);
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
                        });
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final ImageView childview = (ImageView) findViewById(R.id.childdetailprofileview);
        //header
//        TextView today = (TextView) findViewById(R.id.detail_today);

        //profile
        TextView nama = (TextView) findViewById(R.id.txt_child_name);
        TextView mother = (TextView) findViewById(R.id.txt_mother_name);
        TextView father = (TextView) findViewById(R.id.txt_father_number);
        TextView dob = (TextView) findViewById(R.id.tv_dob);

        //detail data
        TextView txt_noBayi = (TextView) findViewById(R.id.txt_noBayi);
        TextView txt_jenisKelamin = (TextView) findViewById(R.id.txt_jenisKelamin);
        TextView txt_beratLahir = (TextView) findViewById(R.id.txt_beratLahir);
        TextView tinggi = (TextView) findViewById(R.id.txt_hasilPengukuranTinggiBayihasilPengukuranTinggiBayi);
        TextView berat = (TextView) findViewById(R.id.txt_indikatorBeratBedanBayi);
        TextView asi = (TextView) findViewById(R.id.txt_pemberianAsiEksklusif);
        TextView status_gizi = (TextView) findViewById(R.id.txt_statusGizi);
        TextView kpsp = (TextView) findViewById(R.id.txt_hasilDilakukannyaKPSP);
        TextView vita = (TextView) findViewById(R.id.txt_pelayananVita);
        TextView hb0 = (TextView) findViewById(R.id.txt_tanggalpemberianimunisasiHb07);
        TextView pol1 = (TextView) findViewById(R.id.txt_tanggalpemberianimunisasiBCGdanPolio1);
        TextView pol2 = (TextView) findViewById(R.id.txt_tanggalpemberianimunisasiDPTHB1Polio2);
        TextView pol3 = (TextView) findViewById(R.id.txt_tanggalpemberianimunisasiDPTHB2Polio3);
        TextView pol4 = (TextView) findViewById(R.id.txt_tanggalpemberianimunisasiDPTHB3Polio4);
        TextView campak = (TextView) findViewById(R.id.txt_tanggalpemberianimunisasiCampak);

        TextView growthChartButton = (TextView) findViewById(R.id.chart_label);
        ImageButton back = (ImageButton) findViewById(org.smartregister.R.id.btn_back_to_home);

        growthChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
//                DetailChildActivity.childclient = childclient;
                startActivity(new Intent(DetailChildActivity.this, DetailChildActivity.class));
                overridePendingTransition(0, 0);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(DetailChildActivity.this, AnakSmartRegisterActivity.class));
                overridePendingTransition(0, 0);
            }
        });
        show_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.id3).setVisibility(VISIBLE);
                findViewById(R.id.id1).setVisibility(GONE);
            }
        });

        show_basic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.id1).setVisibility(VISIBLE);
                findViewById(R.id.id3).setVisibility(GONE);
            }
        });
        DetailsRepository detailsRepository = Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(childclient);

//        String gender = childclient.getDetails().containsKey("gender") ? childclient.getDetails().get("gender") : "laki";
//        userId = childclient.getDetails().get("base_entity_id");
//        int placeholderDrawable = gender.equalsIgnoreCase("male") ? R.drawable.child_boy_infant : R.drawable.child_girl_infant;

        //start profile image
        childview.setTag(R.id.entity_id, childclient.getCaseId());//required when saving file to disk
        if (childclient.getCaseId() != null) {
            //image already in local storage most likey ):
            //set profile image by passing the client id.If the image doesn't exist in the image repository then download and save locally
//            DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(ancClient.getCaseId(), OpenSRPImageLoader.getStaticImageListener(childview, placeholderDrawable, placeholderDrawable));

            Support.setImagetoHolderFromUri(this,
                    userId,
                    childview, "female".equals(childclient.getDetails().get("gender")) ? R.drawable.child_girl_infant : R.drawable.child_boy_infant);
        }

        //end profile image

        AllCommonsRepository childRepository = Context.getInstance().allCommonsRepositoryobjects("ec_anak");

        CommonPersonObject childobject = childRepository.findByCaseID(childclient.entityId());

//        AllCommonsRepository iburep = Context.getInstance().allCommonsRepositoryobjects("ec_ibu");
//        final CommonPersonObject ibuparent = iburep.findByCaseID(childobject.getColumnmaps().get("relational_id"));

        AllCommonsRepository kirep = Context.getInstance().allCommonsRepositoryobjects("ec_kartu_ibu");
        //Fix aplikasi crash saat mengisi data pada kunjungan ANC dan saat membuka dan melihat dashboard anak pada kohort anak
        final String relational_id = childobject.getColumnmaps().get("relational_id");
        Map<String, String> columnmaps = new HashMap<>();
        if (relational_id != null) {
            final CommonPersonObject kiparent = kirep.findByCaseID(relational_id);
            if (kiparent != null) {
                if (kiparent.getColumnmaps() != null) {
                    columnmaps = kiparent.getColumnmaps();
                }
            }
        }

        nama.setText(String.format("%s%s", getResources().getString(R.string.name), humanize(childclient.getColumnmaps().get("namaBayi") != null ? childclient.getColumnmaps().get("namaBayi") : "-")));
        mother.setText(String.format("%s%s", getResources().getString(R.string.child_details_mothers_name_label), humanize(columnmaps.get("namalengkap") != null ? columnmaps.get("namalengkap") : "-")));
        father.setText(String.format("%s%s", getResources().getString(R.string.child_details_fathers_name_label), humanize(columnmaps.get("namaSuami") != null ? columnmaps.get("namaSuami") : "-")));
        dob.setText(String.format("%s%s", getResources().getString(R.string.date_of_birth), humanize(childclient.getColumnmaps().get("tanggalLahirAnak") != null ? childclient.getColumnmaps().get("tanggalLahirAnak") : "-")));

        txt_noBayi.setText(String.format("%s: ", humanize(childclient.getDetails().get("noBayi") != null ? childclient.getDetails().get("noBayi") : "-")));
        txt_jenisKelamin.setText(String.format(": %s", humanize(childclient.getDetails().get("gender") != null ? childclient.getDetails().get("gender") : "-")));
        txt_beratLahir.setText(String.format(": %s", humanize(childclient.getDetails().get("beratLahir") != null ? childclient.getDetails().get("beratLahir") : "-")));
        tinggi.setText(String.format(": %s", humanize(childclient.getDetails().get("panjangBayi") != null ? childclient.getDetails().get("panjangBayi") : "-")));
        berat.setText(String.format(": %s", humanize(childclient.getDetails().get("beratBayi") != null ? childclient.getDetails().get("beratBayi") : "-")));
        asi.setText(String.format(": %s", humanize(childclient.getDetails().get("AsiAksklusif") != null ? childclient.getDetails().get("AsiAksklusif") : "-")));
        status_gizi.setText(String.format(": %s", humanize(childclient.getDetails().get("statusGizi") != null ? childclient.getDetails().get("statusGizi") : "-")));
        kpsp.setText(String.format(": %s", humanize(childclient.getDetails().get("hasilDilakukannyaKPSP") != null ? childclient.getDetails().get("hasilDilakukannyaKPSP") : "-")));
        hb0.setText(String.format(": %s", humanize(childclient.getDetails().get("hb0") != null ? childclient.getDetails().get("hb0") : "-")));
        pol1.setText(String.format(": %s", humanize(childclient.getDetails().get("polio1") != null ? childclient.getDetails().get("polio1") : childclient.getDetails().get("bcg") != null ? childclient.getDetails().get("bcg") : "-")));
        pol2.setText(String.format(": %s", humanize(childclient.getDetails().get("dptHb1") != null ? childclient.getDetails().get("dptHb1") : childclient.getDetails().get("polio2") != null ? childclient.getDetails().get("polio2") : "-")));
        pol3.setText(String.format(": %s", humanize(childclient.getDetails().get("dptHb2") != null ? childclient.getDetails().get("dptHb2") : childclient.getDetails().get("polio3") != null ? childclient.getDetails().get("polio3") : "-")));
        pol4.setText(String.format(": %s", humanize(childclient.getDetails().get("dptHb3") != null ? childclient.getDetails().get("dptHb3") : childclient.getDetails().get("polio4") != null ? childclient.getDetails().get("polio4") : "-")));
        campak.setText(String.format(": %s", humanize(childclient.getDetails().get("campak") != null ? childclient.getDetails().get("campak") : "-")));
        vita.setText(String.format(": %s", humanize(childclient.getDetails().get("pelayananVita") != null ? childclient.getDetails().get("pelayananVita") : "-")));

//        hash = Tools.retrieveHash(context.applicationContext());

        childview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                FlurryFacade.logEvent("taking_child_pictures_on_anak_detail_view");
//                entityid = childclient.entityId();
//                if (hash.containsValue(entityid)) {
//                    updateMode = true;
//                }

                Intent intent = new Intent(DetailChildActivity.this, CameraPreviewActivity.class);
                intent.putExtra(CameraPreviewActivity.REQUEST_TYPE, 201);
                startActivityForResult(intent, 201);
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, AnakSmartRegisterActivity.class));
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(TAG, "onActivityResult: onActivityResult()");
        if (requestCode == 201 && resultCode == -1) {
            Log.d(TAG, "onActivityResult: Process Photo");
            StringBuilder path = new StringBuilder();
            path.append(DrishtiApplication.getAppDir());

            File file = new File(path.toString());
            if (!file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.mkdir();
            }
            if (file.canWrite()) {
                path.append(File.separator).append(userId).append(".jpg");
                Tools.saveFile(Tools.scaleDown((Bitmap) intent.getExtras().get("data"), 400.0f, false), path.toString());
            }

        }
        Log.d(TAG, "onActivityResult: getIntent() = " + getIntent().toString());
        finish();
        startActivity(getIntent());

    }
}
