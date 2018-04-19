package org.smartregister.vaksinator.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.vaksinator.R;
import org.smartregister.vaksinator.libs.FlurryFacade;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import util.formula.Support;

//import org.smartregister.vaksinator.face.camera.SmartShutterActivity;

/**
 * Created by Iq on 09/06/16
 */
public class VaksinatorDetailActivity extends Activity {
    private SimpleDateFormat timer = new SimpleDateFormat("hh:mm:ss");
    //image retrieving
    private static final String TAG = VaksinatorDetailActivity.class.getSimpleName();
    public static CommonPersonObjectClient controller;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Context context = Context.getInstance();
        setContentView(R.layout.smart_register_jurim_detail_client);

        DetailsRepository detailsRepository = org.smartregister.Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(controller);

        AllCommonsRepository childRepository = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_anak");
        final CommonPersonObject controllers = childRepository.findByCaseID(controller.entityId());

        AllCommonsRepository kirep = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_kartu_ibu");
        final CommonPersonObject kiparent = kirep.findByCaseID(Support.getColumnmaps(controllers, "relational_id"));

        String DetailStart = timer.format(new Date());
        Map<String, String> Detail = new HashMap<>();
        Detail.put("start", DetailStart);
        FlurryAgent.logEvent("vaksinator_detail_view",Detail, true );
        //label
        TextView label001 = (TextView) findViewById(R.id.label001);
        TextView label002 = (TextView) findViewById(R.id.label002);
        TextView nameLabel = (TextView) findViewById(R.id.nameLabel);
        TextView fatherLabel = (TextView) findViewById(R.id.fatherNameLabel);
        TextView motherLabel = (TextView) findViewById(R.id.motherNameLabel);
        TextView villageLabel = (TextView) findViewById(R.id.villageLabel);
        TextView subVillageLabel = (TextView) findViewById(R.id.subVillageLabel);
        TextView dateOfBirthLabel = (TextView) findViewById(R.id.dateOfBirthLabel);
        TextView birthWeightLabel = (TextView) findViewById(R.id.birthWeightLabel);
        TextView antipiretikLabel = (TextView) findViewById(R.id.antipyreticLabel);
        TextView hbLabel = (TextView) findViewById(R.id.hbLabel);
        TextView campakLabel = (TextView) findViewById(R.id.campakLabel);
        TextView completeLabel = (TextView) findViewById(R.id.completeLabel);
        TextView additionalDPTLabel = (TextView) findViewById(R.id.additionalDPTLabel);
        TextView additionalMeaslesLabel = (TextView) findViewById(R.id.additionalMeaslesLabel);

        //profile
        TextView uid = (TextView) findViewById(R.id.uidValue);
        TextView nama = (TextView) findViewById(R.id.childName);
        TextView motherName = (TextView) findViewById(R.id.motherName);
        TextView fatherName = (TextView) findViewById(R.id.fatherName);
        TextView village = (TextView) findViewById(R.id.village);
        TextView subVillage = (TextView) findViewById(R.id.subvillage);
        TextView dateOfBirth = (TextView) findViewById(R.id.dateOfBirth);
        TextView birthWeight = (TextView) findViewById(R.id.birthWeight);
        TextView antipiretik = (TextView) findViewById(R.id.antypiretic);

        //vaccination date
        TextView hb1Under7 = (TextView) findViewById(R.id.hb1under7);
        TextView bcg = (TextView) findViewById(R.id.bcg);
        TextView pol1 = (TextView) findViewById(R.id.pol1);
        TextView dpt1 = (TextView) findViewById(R.id.dpt1);
        TextView pol2 = (TextView) findViewById(R.id.pol2);
        TextView dpt2 = (TextView) findViewById(R.id.dpt2);
        TextView pol3 = (TextView) findViewById(R.id.pol3);
        TextView dpt3 = (TextView) findViewById(R.id.dpt3);
        TextView pol4 = (TextView) findViewById(R.id.pol4);
        TextView ipv = (TextView) findViewById(R.id.ipv);
        TextView measles = (TextView) findViewById(R.id.measles);
        TextView complete = (TextView) findViewById(R.id.complete);
        TextView additionalDPT = (TextView) findViewById(R.id.additionalDPT);
        TextView additionalMeasles = (TextView) findViewById(R.id.additionalMeasles);
        final ImageView photo = (ImageView) findViewById(R.id.photo);

        ImageButton backButton = (ImageButton) findViewById(R.id.btn_back_to_home);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

                startActivity(new Intent(VaksinatorDetailActivity.this, VaksinatorSmartRegisterActivity.class));
                overridePendingTransition(0, 0);
                //Start capture flurry log for FS
                String DetailEnd = timer.format(new Date());
                Map<String, String> Detail = new HashMap<>();
                Detail.put("end", DetailEnd);
                FlurryAgent.logEvent("vaksinator_detail_view",Detail, true );
            }
        });

        TextView recapitulationLabel = (TextView)findViewById(R.id.recapitulation_label);
        recapitulationLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                VaksinatorRecapitulationActivity.staticVillageName = Support.getDetails(controller,"desa_anak");
                startActivity(new Intent(VaksinatorDetailActivity.this, VaksinatorRecapitulationActivity.class));
                overridePendingTransition(0, 0);
                FlurryFacade.logEvent("click_recapitulation_button");
            }
        });

        //Label rename
        label001.setText(getString(R.string.title001));
        label002.setText(getString(R.string.title002));
        nameLabel.setText(getString(R.string.namaAnak));
        fatherLabel.setText(getString(R.string.namaAyah));
        motherLabel.setText(getString(R.string.namaIbu));
        villageLabel.setText(getString(R.string.desa));
        subVillageLabel.setText(getString(R.string.dusun));
        dateOfBirthLabel.setText(getString(R.string.tanggalLahir));
        birthWeightLabel.setText(getString(R.string.beratLahir));
        antipiretikLabel.setText(getString(R.string.dapatAntipiretik));
        hbLabel.setText("HB0 (0-7 "+getString(R.string.hari)+")");
        campakLabel.setText(getString(R.string.measles));
        completeLabel.setText(getString(R.string.imunisasiLengkap));
        additionalDPTLabel.setText(getString(R.string.dptTambahan));
        additionalMeaslesLabel.setText(getString(R.string.campakTambahan));


        uid.setText(": "+(Support.getDetails(controller, "UniqueId")));
        nama.setText(": " + (Support.getColumnmaps(controller, "namaBayi")));
        village.setText(": " + Support.getDetails(controller,"desa_anak"));

        System.out.println("details: "+controller.getDetails().toString());

        if(kiparent != null) {
            detailsRepository.updateDetails(kiparent);
            String namaayah = kiparent.getDetails().get("namaSuami") != null ? kiparent.getDetails().get("namaSuami") : "";
            String namaibu = kiparent.getColumnmaps().get("namalengkap") != null ? kiparent.getColumnmaps().get("namalengkap") : "";

            fatherName.setText(": " + namaayah);
            motherName.setText(": " +namaibu);
            subVillage.setText(kiparent.getDetails().get("address1") != null ? ": " + kiparent.getDetails().get("address1") : "");
            // viewHolder.no_ibu.setText(kiparent.getDetails().get("noBayi") != null ? kiparent.getDetails().get("noBayi") : "");
        }

        /*fatherName.setText(": " + (Support.getDetails(controller,"namaAyah") != null ? transformToddmmyyyy(Support.getDetails(controller,"namaAyah");
        motherName.setText(": " + (Support.getDetails(controller,"namaIbu") != null
                ? Support.getDetails(controller,"namaIbu")
                : Support.getDetails(controller,"nama_orang_tua")!=null
                        ? Support.getDetails(controller,"nama_orang_tua")
                        : "-"
            )
        );*/
       /* village.setText(": " + (Support.getDetails(controller,"desa") != null
                ? Support.getDetails(controller,"desa")
                : "-"))));*/

   /*     subVillage.setText(": " + (Support.getDetails(controller,"dusun") != null
                ? Support.getDetails(controller,"dusun")
                : Support.getDetails(controller,"village") != null
                    ? Support.getDetails(controller,"village")
                    : "-")
        );
*/

        dateOfBirth.setText(": " + (transformToddmmyyyy(Support.getColumnmaps(controller,"tanggalLahirAnak").substring(0,10))));
        birthWeight.setText(": " + (!"-".equals(Support.getDetails(controller, "beratLahir"))
                ? Double.toString(Integer.parseInt(controller.getDetails()
                .get("beratLahir"))/1000)
                + " kg"
                : "-"));
        antipiretik.setText(": " + (yesNo(Support.getDetails(controller, "antipiretik").toLowerCase().contains("y"))));

        hb1Under7.setText(": " + transformToddmmyyyy(hasDate(controller,"hb0")
                ? Support.getDetails(controller, "hb0")
                : hasDate(controller,"hb1_kurang_7_hari")
                ? Support.getDetails(controller, "hb1_kurang_7_hari")
                :"-"));

        bcg.setText(": " + (transformToddmmyyyy(Support.getDetails(controller, "bcg"))));
        pol1.setText(": " + (transformToddmmyyyy(Support.getDetails(controller, "polio1"))));
        dpt1.setText(": " + (transformToddmmyyyy(Support.getDetails(controller, "dptHb1"))));
        pol2.setText(": " + (transformToddmmyyyy(Support.getDetails(controller, "polio2"))));
        dpt2.setText(": " + (transformToddmmyyyy(Support.getDetails(controller, "dptHb2"))));
        pol3.setText(": " + (transformToddmmyyyy(Support.getDetails(controller, "polio3"))));
        dpt3.setText(": " + (transformToddmmyyyy(Support.getDetails(controller, "dptHb3"))));
        pol4.setText(": " + (transformToddmmyyyy(Support.getDetails(controller, "polio4"))));
        ipv.setText(": " + (transformToddmmyyyy(Support.getDetails(controller, "ipv"))));
        measles.setText(": " + (transformToddmmyyyy(Support.getDetails(controller, "campak"))));

        complete.setText(": " + yesNo(isComplete()));
        additionalDPT.setText(": " + (transformToddmmyyyy(Support.getDetails(controller, "dpt_hb_lanjutan"))));
        additionalMeasles.setText(": " + (transformToddmmyyyy(Support.getDetails(controller, "campak_lanjutan"))));

//        String mgender = controller.getDetails().containsKey("gender") ? Support.getDetails(controller, "gender"):"laki";
        //start profile image
//        int placeholderDrawable = mgender.equalsIgnoreCase("male") ? R.drawable.child_boy_infant:R.drawable.child_girl_infant;
        photo.setTag(R.id.entity_id, controller.getCaseId());//required when saving file to disk
        if(controller.getCaseId()!=null){//image already in local storage most likey ):
            //set profile image by passing the client id.If the image doesn't exist in the image repository then download and save locally
//            DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(controller.getCaseId(), OpenSRPImageLoader.getStaticImageListener(photo, placeholderDrawable, placeholderDrawable));
            VaksinatorDetailActivity.setImagetoHolderFromUri( this ,
                    DrishtiApplication.getAppDir() + File.separator + Support.getDetails(controller, "base_entity_id") + ".JPEG",
                    photo, Support.getDetails(controller, "gender").equals("female") ? R.drawable.child_girl_infant : R.drawable.child_boy_infant);

        }

//        if(Support.getDetails(controller,"profilepic")!= null){
//            if((Support.getDetails(controller,"gender")!=null?Support.getDetails(controller,"gender"):"").equalsIgnoreCase("female")) {
//                setImagetoHolderFromUri(VaksinatorDetailActivity.this, Support.getDetails(controller,"profilepic"), photo, R.drawable.child_girl_infant);
//            } else if ((Support.getDetails(controller,"gender")!=null?Support.getDetails(controller,"gender"):"").equalsIgnoreCase("male")){
//                setImagetoHolderFromUri(VaksinatorDetailActivity.this, Support.getDetails(controller,"profilepic"), photo, R.drawable.child_boy_infant);
//
//            }
//        }
//        else {
//            if (Support.getDetails(controller,"gender").equalsIgnoreCase("male") ) {
//                photo.setImageDrawable(getResources().getDrawable(R.drawable.child_boy_infant));
//            } else {
//                photo.setImageDrawable(getResources().getDrawable(R.drawable.child_girl_infant));
//            }
//        }

        /*if(Support.getDetails(controller,"profilepic")!= null){
            setImagetoHolderFromUri(VaksinatorDetailActivity.this, Support.getDetails(controller,"profilepic"), photo, R.drawable.child_boy_infant);
        }
        else {
            photo.setImageResource(Support.getDetails(controller,"jenisKelamin").contains("em")
                    ? R.drawable.child_girl_infant
                    : R.drawable.child_boy_infant);

        }
*/
//        photo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FlurryFacade.logEvent("taking_anak_pictures_on_child_detail_view");
//                bindobject = "anak";
//                entityid = controller.entityId();
//                android.util.Log.e(TAG, "onClick: " + entityid);
////                dispatchTakePictureIntent(photo);
//                Intent takePictureIntent = new Intent(VaksinatorDetailActivity.this, SmartShutterActivity.class);
//                takePictureIntent.putExtra("IdentifyPerson", false);
//                takePictureIntent.putExtra("org.sid.sidface.ImageConfirmation.id", entityid);
//                takePictureIntent.putExtra("org.sid.sidface.ImageConfirmation.origin", TAG); // send Class Name
//                startActivityForResult(takePictureIntent, 1);
//
//
//            }
//        });
    }


    private boolean isComplete(){
        return ((Support.getDetails(controller, "hb0") != null &&
                Support.getDetails(controller, "bcg") != null &&
                Support.getDetails(controller, "polio1") != null &&
                Support.getDetails(controller, "dptHb1") != null &&
                Support.getDetails(controller, "polio2") != null &&
                Support.getDetails(controller, "dptHb2") != null &&
                Support.getDetails(controller, "polio3") != null &&
                Support.getDetails(controller, "dptHb3") != null &&
                Support.getDetails(controller, "polio4") != null &&
                Support.getDetails(controller, "campak") != null) &&
                (!Support.getDetails(controller, "hb0").equals("-") &&
                !Support.getDetails(controller, "bcg").equals("-") &&
                !Support.getDetails(controller, "polio1").equals("-") &&
                !Support.getDetails(controller, "dptHb1").equals("-") &&
                !Support.getDetails(controller, "polio2").equals("-") &&
                !Support.getDetails(controller, "dptHb2").equals("-") &&
                !Support.getDetails(controller, "polio3").equals("-") &&
                !Support.getDetails(controller, "dptHb3").equals("-") &&
                !Support.getDetails(controller, "polio4").equals("-") &&
                !Support.getDetails(controller, "campak").equals("-"))
        );
    }

    public String yesNo(boolean cond){
        return getString(cond? R.string.mcareyes_button_label : R.string.mcareno_button_label);
    }
    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, VaksinatorSmartRegisterActivity.class));
        overridePendingTransition(0, 0);


    }

    /*
    * Used to check if the variable contains a date (10 character which representing yyyy-MM-dd) or not
    * params:
    * CommonPersonObjectClient pc
    * String variable
    *
    * return:
    * true - if the variable contains date
    * false - if the variable null or less than 10 character length
    * */
    private boolean hasDate(CommonPersonObjectClient pc, String variable){
        return pc.getDetails().get(variable)!=null && pc.getDetails().get(variable).length()==10;
    }

//    private int age(String date1, String date2){
//        return (Integer.parseInt(date2.substring(0,3)) - Integer.parseInt(date1.substring(0,3)))*360
//                + (Integer.parseInt(date2.substring(5,7)) - Integer.parseInt(date1.substring(5,7)))*30
//                + (Integer.parseInt(date2.substring(8)) - Integer.parseInt(date1.substring(8)));
//    }

    public String transformToddmmyyyy(String myDate){
        String date = myDate;
        if(date.length()>3 && date.charAt(4) == '-')
                date = String.format("%s/%s/%s", (Object[]) new String[]{date.substring(8, 10), date.substring(5, 7), date.substring(0, 4)});
        return date;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        refresh
        Log.e(TAG, "onActivityResult: refresh" );
        finish();
        startActivity(getIntent());

    }

    public static void setImagetoHolderFromUri(Activity activity, String file, ImageView view, int placeholder){
        view.setImageDrawable(activity.getResources().getDrawable(placeholder));
        File externalFile = new File(file);
        if (externalFile.exists()) {
            Uri external = Uri.fromFile(externalFile);
            view.setImageURI(external);
        }

    }
}