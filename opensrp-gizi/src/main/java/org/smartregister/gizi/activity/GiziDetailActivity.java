package org.smartregister.gizi.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;

import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.gizi.R;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.util.Log;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import util.formula.Support;
import util.growthchart.GrowthChartGenerator;
import util.kms.KmsCalc;
import util.kms.KmsPerson;

/**
 * Created by Iq on 26/04/16
 */
public class GiziDetailActivity extends Activity {

    private SimpleDateFormat timer = new SimpleDateFormat("hh:mm:ss");
    private static final String TAG = GiziDetailActivity.class.getSimpleName();
    public static CommonPersonObjectClient childclient;
    protected static String bindobject;
    protected static String entityid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Context context = Context.getInstance();
        setContentView(R.layout.gizi_detail_activity);
        String DetailStart = timer.format(new Date());
                Map<String, String> Detail = new HashMap<>();
                Detail.put("start", DetailStart);
                FlurryAgent.logEvent("gizi_detail_view",Detail, true );

        final ImageView childview = (ImageView)findViewById(R.id.detail_profilepic);
        //header
        TextView header_name = (TextView) findViewById(R.id.header_name);
        //sub header
        TextView subheader = (TextView) findViewById(R.id.txt_title_label);
        //profile
        TextView uniqueId = (TextView) findViewById(R.id.txt_profile_unique_id);
        TextView nama = (TextView) findViewById(R.id.txt_profile_child_name);
        TextView father_name = (TextView) findViewById(R.id.txt_profile_father_name);
        TextView mother_name = (TextView) findViewById(R.id.txt_profile_mother_name);
        TextView posyandu = (TextView) findViewById(R.id.txt_profile_posyandu);
        TextView village_name = (TextView) findViewById(R.id.txt_profile_village_name);
        TextView birth_date = (TextView) findViewById(R.id.txt_profile_birth_date);
        TextView gender = (TextView) findViewById(R.id.txt_profile_child_gender);
        TextView birthWeight = (TextView) findViewById(R.id.txt_profile_birth_weight);
        TextView weight = (TextView) findViewById(R.id.txt_profile_last_weight);
        TextView height = (TextView) findViewById(R.id.txt_profile_last_height);
        //child growth
        TextView nutrition_status = (TextView) findViewById(R.id.txt_profile_nutrition_status);
        TextView bgm = (TextView) findViewById(R.id.txt_profile_bgm);
        TextView dua_t = (TextView) findViewById(R.id.txt_profile_2t);
        TextView under_yellow_line = (TextView) findViewById(R.id.txt_profile_under_yellow_line);
        TextView breast_feeding = (TextView) findViewById(R.id.txt_profile_breastfeeding);
        TextView mpasi = (TextView) findViewById(R.id.txt_profile_mp_asi);
        TextView vitA = (TextView) findViewById(R.id.txt_vitA);
        TextView obatCacing = (TextView) findViewById(R.id.txt_anthelmintic);
        TextView lastVitA = (TextView) findViewById(R.id.txt_profile_last_vitA);
        TextView lastAnthelmintic = (TextView) findViewById(R.id.txt_profile_last_anthelmintic);

        ImageButton back = (ImageButton) findViewById(org.smartregister.R.id.btn_back_to_home);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(GiziDetailActivity.this, GiziSmartRegisterActivity.class));
                overridePendingTransition(0, 0);

                String DetailEnd = timer.format(new Date());
                Map<String, String> Detail = new HashMap<>();
                Detail.put("end", DetailEnd);
                FlurryAgent.logEvent("gizi_detail_view", Detail, true);
            }
        });
        DetailsRepository detailsRepository = org.smartregister.Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(childclient);

        System.out.println("columnmaps: " + childclient.getColumnmaps().toString());
        System.out.println("details: "+childclient.getDetails().toString());

        childview.setTag(R.id.entity_id, childclient.getCaseId());//required when saving file to disk

        if (childclient.getDetails().get("gender") != null) {
            System.out.println(childclient.getDetails().toString());
            Support.setImagetoHolderFromUri( this ,
                    DrishtiApplication.getAppDir() + File.separator + childclient.getDetails().get("base_entity_id") + ".JPEG",
                    childview, childclient.getDetails().get("gender").equals("female") ? R.drawable.child_girl_infant : R.drawable.child_boy_infant);
        } else {
            android.util.Log.e(TAG, "getView: Gender is NOT SET");
        }

        header_name.setText(R.string.child_profile);
        subheader.setText(R.string.child_profile);
        uniqueId.setText(String.format("%s %s",getString(R.string.unique_id),Support.getDetails(childclient,"UniqueId")));
        nama.setText(String.format("%s %s",getString(R.string.child_name) , Support.getDetails(childclient,"namaBayi")));

        AllCommonsRepository childRepository = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_anak");
        CommonPersonObject childobject = childRepository.findByCaseID(childclient.entityId());

        AllCommonsRepository kirep = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_kartu_ibu");
        final CommonPersonObject kiparent = kirep.findByCaseID(childobject.getColumnmaps().get("relational_id"));

        if(kiparent != null) {
            detailsRepository.updateDetails(kiparent);
            String namaayah = Support.getDetails(kiparent,"namaSuami");
            String namaibu = Support.getColumnmaps(kiparent, "namalengkap");

            father_name.setText(getString(R.string.father_name)+" " + namaayah);
            mother_name.setText(getString(R.string.mother_name) +" " +namaibu);
            village_name.setText(String.format("%s %s",getString(R.string.village),(Support.getDetails(kiparent,"cityVillage"))));
            posyandu.setText(String.format("%s %s",getString(R.string.posyandu),(Support.getDetails(kiparent,"address1"))));

        }

        String dateOfBirth = Support.getDetails(childclient, "tanggalLahirAnak").substring(0,10);
        birth_date.setText(String.format("%s %s",getString(R.string.birth_date) ,(dateOfBirth.contains("T") ? dateOfBirth.substring(0, 10) : dateOfBirth)));
        gender.setText(String.format("%s %s",getString(R.string.gender), (Support.getDetails(childclient, "gender").toLowerCase().contains("em")? getString(R.string.child_female) : getString(R.string.child_male))));
        birthWeight.setText(String.format("%s %s",getString(R.string.birth_weight), (Support.getDetails(childclient, "beratLahir") + " gr")));
        weight.setText(String.format("%s %s",getString(R.string.weight),(Support.getDetails(childclient, "beratBadan")) + " Kg"));
        height.setText(String.format("%s %s",getString(R.string.height),(Support.getDetails(childclient, "tinggiBadan") + " Cm")));
        vitA.setText(String.format("%s : %s",getString(R.string.vitamin_a),(inTheSameRegion(Support.getDetails(childclient, "lastVitA")) ? getString(R.string.yes) : getString(R.string.no))));
        mpasi.setText(String.format("%s %s",getString(R.string.mpasi),(yesNo(Support.getDetails(childclient, "mp_asi")))));
        obatCacing.setText(String.format("%s %s",getString(R.string.obatcacing),(inTheSameRegionAnth(Support.getDetails(childclient, "lastAnthelmintic")) ? getString(R.string.yes) : getString(R.string.no))));
        lastVitA.setText(String.format("%s %s",getString(R.string.lastVitA),(Support.getDetails(childclient, "lastVitA"))));
        lastAnthelmintic.setText(String.format("%s %s",getString(R.string.lastAnthelmintic),(Support.getDetails(childclient, "lastAnthelmintic"))));

//        String[]history = Support.split(Support.fixHistory(Support.getDetails(childclient,"history_berat")));
//        String[]historyUmur = history[0].split(",");
//        String[]historyBerat = history[1].split(",");
        KmsPerson anak = new KmsPerson(childclient);
        KmsCalc kalkulator = new KmsCalc();

//        System.out.println("age "+anak.getAge());
//        System.out.println("dob "+anak.getDateOfBirth());
//        System.out.println("weight "+anak.getWeight());
//        System.out.println("prev weight "+anak.getPreviousWeight());
//        System.out.println("2nd last weight "+anak.getSecondLastWeight());
//        System.out.println("last visit "+anak.getLastVisitDate());
//        System.out.println("2nd last visit "+anak.getSecondLastVisitDate());
//        System.out.println("3rd last visit "+anak.getThirdLastVisitDate());

        dua_t.setText(String.format("%s %s", getString(R.string.dua_t), (yesNo(kalkulator.cek2T(anak)))));
        bgm.setText(String.format("%s %s",getString(R.string.bgm), (yesNo(kalkulator.cekBGM(anak)))));
        under_yellow_line.setText(String.format("%s %s",getString(R.string.under_yellow_line),(yesNo(kalkulator.cekBawahKuning(anak)))));
        breast_feeding.setText(String.format("%s %s",getString(R.string.asi),(yesNo(Support.getDetails(childclient, "asi")))));
        nutrition_status.setText(String.format("%s %s",getString(R.string.nutrition_status),weightStatus(kalkulator.cekWeightStatus(anak))));

        //set value
        String[]data = split(Support.fixHistory(Support.getDetails(childclient, "history_berat")));
        String[] history_umur = data[0].split(",");
        String umurs=arrayToString(history_umur,",");

        generateGrowthChart(umurs,data[1]);
       // hash = Tools.retrieveHash(context.applicationContext());
        childview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bindobject = "anak";
                entityid = childclient.entityId();

//                if (hash.containsValue(entityid)) {
//                    android.util.Log.e(TAG, "onClick: " + entityid + " updated");
//                    mode = "updated";
//                    updateMode = true;
//                }

              /*  Intent takePictureIntent = new Intent(GiziDetailActivity.this, SmartShutterActivity.class);
                takePictureIntent.putExtra("IdentifyPerson", false);
                takePictureIntent.putExtra("org.sid.sidface.ImageConfirmation.id", entityid);
                takePictureIntent.putExtra("org.sid.sidface.ImageConfirmation.origin", TAG); // send Class Name
                startActivityForResult(takePictureIntent, 2);*/


            }
        });
    }

    // english: fEMale, bahasa: perEMpuan, both have EM; since en: male, bhs: laki, both no EM
//    private  String gender(String value){
//        if (value.toLowerCase().contains("em"))
//            return getString(R.string.child_female);
//        else
//            return getString(R.string.child_male);
//    }

    private void generateGrowthChart(String xAxis, String yAxis){
        Log.logInfo("Berat :" +yAxis);
        Log.logInfo("umurs :" +xAxis);
        GraphView graph = (GraphView) findViewById(R.id.graph);
        new GrowthChartGenerator(graph,childclient.getDetails().get("gender"),
                Support.getDetails(childclient, "tanggalLahirAnak").substring(0, Support.getDetails(childclient, "tanggalLahirAnak").indexOf("T"))
                ,xAxis,yAxis);

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values
                    return super.formatLabel(value, isValueX )+ " Month";
                } else {
                    // show currency for y values
                    return super.formatLabel(value, isValueX) + " Kg";
                }
            }
        });
    }

    private String yesNo(String value){
        if(value==null)
            return "-";
        else if(value.toLowerCase().contains("yes") || value.toLowerCase().contains("ya"))
            return getString(R.string.yes);
        else
            return getString(R.string.no);
    }

    private String weightStatus(String value){
        if(value.toLowerCase().contains("gain") || value.toLowerCase().contains("idak"))
            return getString(R.string.weight_not_increase);
        else if(value.toLowerCase().contains("ncrea"))
            return getString(R.string.weight_increase);
        else if(value.toLowerCase().contains("atten"))
            return getString(R.string.weight_not_attend);
        else
            return getString(R.string.weight_new);
    }

//    String mCurrentPhotoPath;
//    private File createImageFile() throws IOException {
//         Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );
//
//         Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
//        return image;
//    }


//    public static void setImagetoHolder(Activity activity, String file, ImageView view, int placeholder){
//        mImageThumbSize = 300;
//        mImageThumbSpacing = Context.getInstance().applicationContext().getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);
//
//
//        ImageCache.ImageCacheParams cacheParams =
//                new ImageCache.ImageCacheParams(activity, IMAGE_CACHE_DIR);
//        cacheParams.setMemCacheSizePercent(0.50f); // Set memory cache to 25% of app memory
//        mImageFetcher = new ImageFetcher(activity, mImageThumbSize);
//        mImageFetcher.setLoadingImage(placeholder);
//        mImageFetcher.addImageCache(activity.getFragmentManager(), cacheParams);
//        mImageFetcher.loadImage("file:///"+file,view);
//    }
    public String arrayToString(String[]data, String separator){
        String umurs="";
        for (String aData : data) {
            umurs = umurs + "," + Integer.toString(Integer.parseInt(aData) / 30);
        }
        return umurs.substring(1,umurs.length());
    }

    private boolean inTheSameRegion(String date){
        if(date==null || date.length()<6)
            return false;
        int currentDate = Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
        int visitDate = Integer.parseInt(date.substring(5, 7));

        int currentYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int visitYear = Integer.parseInt(date.substring(0, 4));

        boolean date1 = currentDate < 2 || currentDate >=8;
        boolean date2 = visitDate < 2 || visitDate >=8;

        int indicator = currentDate == 1 ? 2:1;

        return (!((!date1 && date2) || (date1 && !date2)) && ((currentYear-visitYear)<indicator));
    }

    private boolean inTheSameRegionAnth(String date){
        if(date==null || date.length()<6)
            return false;
        int visitDate = Integer.parseInt(date.substring(5, 7));

        int currentYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
        int visitYear = Integer.parseInt(date.substring(0, 4));

        return (((currentYear-visitYear)*12) + (8-visitDate)) <=12;
    }

    private String[] split(String data){
        String myData = data;
        if(myData == null)
            myData="";
        if(!myData.contains(":"))
            return new String[]{"0","0"};
        String []temp = myData.split(",");
        String []result = {"",""};
        for (String aTemp : temp) {
            result[0] = result[0] + "," + aTemp.split(":")[0];
            result[1] = result[1] + "," + aTemp.split(":")[1];
        }
        result[0]=result[0].substring(1,result[0].length());
        result[1]=result[1].substring(1,result[1].length());
        return result;
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, GiziSmartRegisterActivity.class));
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        android.util.Log.e(TAG, "onActivityResult: refresh");
        finish();
        startActivity(getIntent());
    }

}
