package org.smartregister.gizi.provider;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonPersonObjectController;
import org.smartregister.cursoradapter.SmartRegisterCLientsProviderForCursorAdapter;
import org.smartregister.gizi.R;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.service.AlertService;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import util.formula.Support;
import util.zscore.ZScoreSystemCalculation;

import static android.view.View.INVISIBLE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by soran on 08/11/17
 */

public class ChildSmartClientsProvider implements SmartRegisterCLientsProviderForCursorAdapter {
    private static final String TAG = ChildSmartClientsProvider.class.getSimpleName();
    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;
    private Drawable iconPencilDrawable;
    private final AbsListView.LayoutParams clientViewLayoutParams;

    protected CommonPersonObjectController controller;

    public ChildSmartClientsProvider(Context context, View.OnClickListener onClickListener,
                                    AlertService alertService) {
        this.context = context;
        this.onClickListener = onClickListener;
        Log.i(TAG, "ChildSmartClientsProvider: "+ alertService);
//        AlertService alertService1 = alertService;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT, (int) context.getResources().getDimension(org.smartregister.R.dimen.list_item_height));
//        int txtColorBlack =
        context.getResources().getColor(org.smartregister.R.color.text_black);

    }

    @Override
    public void getView(Cursor cursor,SmartRegisterClient smartRegisterClient, View convertView) {
        ViewHolder viewHolder;

        if (convertView.getTag() == null || !(convertView.getTag() instanceof ViewHolder)) {
            viewHolder = new ViewHolder();
            viewHolder.profilelayout = (LinearLayout) convertView.findViewById(R.id.profile_info_layout);
            viewHolder.name = (TextView) convertView.findViewById(R.id.txt_child_name);
            viewHolder.fatherName = (TextView) convertView.findViewById(R.id.ParentName);
            viewHolder.subVillage = (TextView) convertView.findViewById(R.id.txt_child_subVillage);
            viewHolder.age = (TextView) convertView.findViewById(R.id.txt_child_age);
            viewHolder.dateOfBirth = (TextView) convertView.findViewById(R.id.txt_child_date_of_birth);
            viewHolder.gender = (TextView) convertView.findViewById(R.id.txt_child_gender);
            viewHolder.visitDate = (TextView) convertView.findViewById(R.id.txt_child_visit_date);
            viewHolder.height = (TextView) convertView.findViewById(R.id.txt_child_height);
            viewHolder.weight = (TextView) convertView.findViewById(R.id.txt_child_weight);
            viewHolder.underweight = (TextView) convertView.findViewById(R.id.txt_child_underweight);
            viewHolder.stunting_status = (TextView) convertView.findViewById(R.id.txt_child_stunting);
            viewHolder.wasting_status = (TextView) convertView.findViewById(R.id.txt_child_wasting);

            viewHolder.absentAlert = (TextView) convertView.findViewById(R.id.absen);
            viewHolder.weightText = (TextView) convertView.findViewById(R.id.weightSchedule);
            viewHolder.weightLogo = (ImageView) convertView.findViewById(R.id.weightSymbol);
            viewHolder.heightText = (TextView) convertView.findViewById(R.id.heightSchedule);
            viewHolder.heightLogo = (ImageView) convertView.findViewById(R.id.heightSymbol);
            viewHolder.vitALogo = (ImageView) convertView.findViewById(R.id.vitASymbol);
            viewHolder.vitAText = (TextView) convertView.findViewById(R.id.vitASchedule);
            viewHolder.antihelminticLogo = (ImageView) convertView.findViewById(R.id.antihelminticSymbol);
            viewHolder.antihelminticText = (TextView) convertView.findViewById(R.id.antihelminticText);

            viewHolder.profilepic = (ImageView) convertView.findViewById(R.id.profilepic);
            viewHolder.follow_up = (ImageButton) convertView.findViewById(R.id.btn_edit);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.follow_up.setOnClickListener(onClickListener);
        viewHolder.follow_up.setTag(smartRegisterClient);
        viewHolder.profilelayout.setOnClickListener(onClickListener);
        viewHolder.profilelayout.setTag(smartRegisterClient);

        CommonPersonObjectClient pc = (CommonPersonObjectClient) smartRegisterClient;

        if (iconPencilDrawable == null) {
            iconPencilDrawable = context.getResources().getDrawable(R.drawable.ic_pencil);
        }
        viewHolder.follow_up.setImageDrawable(iconPencilDrawable);
        viewHolder.follow_up.setOnClickListener(onClickListener);

        DetailsRepository detailsRepository = org.smartregister.Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(pc);

        //start profile image
//        viewHolder.profilepic.setTag(R.id.entity_id, pc.getCaseId());//required when saving file to disk
        viewHolder.profilepic.setTag(R.id.entity_id, Support.getColumnmaps(pc, "_id"));//required when saving file to disk

        if (Support.getDetails(pc, "gender") != null) {
            Support.setImagetoHolderFromUri((Activity) context,
                    DrishtiApplication.getAppDir() + File.separator + Support.getDetails(pc, "base_entity_id") + ".JPEG",
                    viewHolder.profilepic, Support.getDetails(pc, "gender").equals("female") ? R.drawable.child_girl_infant : R.drawable.child_boy_infant);
        } else {
            Log.e(TAG, "getView: Gender is NOT SET");
        }

        viewHolder.name.setText(Support.getDetails(pc, "namaBayi"));
        String dob = Support.getColumnmaps(pc, "tanggalLahirAnak");
        dob = dob.length() > 7 ? dob.substring(0, dob.indexOf("T")) : dob;

        viewHolder.age.setVisibility(INVISIBLE);//.setText(Support.getDetails(pc,"tanggalLahirAnak")!= null ? Integer.toString(monthRangeToToday(ages))+" bln" : "");

        AllCommonsRepository childRepository = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_anak");
        CommonPersonObject childobject = childRepository.findByCaseID(pc.entityId());

        AllCommonsRepository kirep = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_kartu_ibu");
        final CommonPersonObject kiparent = kirep.findByCaseID(Support.getColumnmaps(childobject, "relational_id"));

        if (kiparent != null) {
            detailsRepository.updateDetails(kiparent);
            String namaayah = Support.getDetails(kiparent, "namaSuami") != null ? Support.getDetails(kiparent, "namaSuami") : "";
            String namaibu = Support.getColumnmaps(kiparent, "namalengkap") != null ? Support.getColumnmaps(kiparent, "namalengkap") : "";

            viewHolder.fatherName.setText(String.format("%s,%s", namaibu, namaayah));
            viewHolder.subVillage.setText(Support.getDetails(kiparent, "address1") != null ? Support.getDetails(kiparent, "address1") : "");
        }

        String Tgl = Support.getDetails(pc, "tanggalLahirAnak");
        if (Tgl.length() > 7) {
            if (Tgl != null && Tgl.contains("T"))
                Tgl = Tgl.substring(0, Tgl.indexOf("T"));

            viewHolder.setAntihelminticVisibility(
                    dayRangeBetween(Tgl.split("-")
                            , new SimpleDateFormat("yyyy-MM-dd").format(new Date()).split("-")
                    ) >= 365 ? View.VISIBLE : INVISIBLE
            );
            viewHolder.dateOfBirth.setText(!"-".equals(Support.getDetails(pc, "tanggalLahirAnak")) ? Tgl : "");

//        viewHolder.gender.setText( Support.getDetails(pc,"gender") != null ? setGender(Support.getDetails(pc,"gender")):"-");
            int age = monthRangeToToday(dob);
            viewHolder.gender.setText( !"-".equals(Support.getDetails(pc, "tanggalLahirAnak"))
                    ? age / 12 + " " + context.getString(R.string.years_unit) + " " + age % 12 + " " + context.getString(R.string.month_unit) : "-");


            // collect history data and clean latest history data which contains no specific date or value,
            if (!"nan".equals(Support.getDetails(pc, "umur").toLowerCase())) {
                String[] history1 = hasValue(Support.getDetails(pc, "history_berat")) ? Support.insertionSort(Support.getDetails(pc, "history_berat")) : new String[]{"0:0"};
                if (history1[history1.length - 1].charAt(history1[history1.length - 1].length() - 1) == ':')
                    history1[history1.length - 1] = history1[history1.length - 1] + "-";
                String[] history2 = hasValue(Support.getDetails(pc, "history_tinggi")) ? Support.insertionSort(Support.getDetails(pc, "history_tinggi")) : new String[]{"0:0"};
                if (history2[history2.length - 1].charAt(history2[history2.length - 1].length() - 1) == ':')
                    history2[history2.length - 1] = history2[history2.length - 1] + "-";
                String newestDateonHistory = history1.length > 1
                        ? findDate(Tgl, Support.getAge(history1[history1.length - 1]))
                        : hasValue(Support.getDetails(pc, "tanggalPenimbangan"))
                        ? Support.getDetails(pc, "tanggalPenimbangan")
                        : Tgl;

                System.out.println("history1 : " + history1[history1.length - 1]);
                System.out.println("history2 : " + history2[history2.length - 1]);
                System.out.println("newest : " + newestDateonHistory);
                String weightToDisplay;
                String heightToDisplay;

//                if (newestDateonHistory.equals(Support.getDetails(pc, "tanggalPenimbangan"))) {
//                    System.out.println("history = tglPenimbangan");
//                    heightToDisplay = Support.getDetails(pc, "tinggiBadan");
//                    weightToDisplay = Support.getDetails(pc, "beratBadan");
//                } else {
                System.out.println("history != tglPenimbangan");
                viewHolder.visitDate.setText(context.getString(R.string.tanggal) + " " + (history1.length > 1 ? newestDateonHistory : "-"));
                heightToDisplay = (!"-".equals(Support.getDetails(pc, "tinggiBadan"))
                        ? !Support.getDetails(pc, "tinggiBadan").equals(history2[history2.length - 1])
                        ? history2[history2.length - 1].split(":")[1]
                        : Support.getDetails(pc, "tinggiBadan")
                        : "-");
                weightToDisplay = (!"-".equals(Support.getDetails(pc, "beratBadan"))
                        ? !Support.getDetails(pc, "beratBadan").equals(history1[history1.length - 1])
                        ? history1[history1.length - 1].split(":")[1]
                        : Support.getDetails(pc, "beratBadan")
                        : "-");
//                }

                viewHolder.visitDate.setText(context.getString(R.string.tanggal) + " " + (history1.length > 1 ? newestDateonHistory : "-"));
                viewHolder.height.setText(context.getString(R.string.height) + " " + heightToDisplay + " cm");
                viewHolder.weight.setText(context.getString(R.string.weight) + " " + weightToDisplay + " Kg");
                viewHolder.weightText.setText(context.getString(R.string.label_weight));
                viewHolder.heightText.setText(context.getString(R.string.label_height));
                viewHolder.antihelminticText.setText(R.string.anthelmintic);

//------VISIBLE AND INVISIBLE COMPONENT
                viewHolder.absentAlert.setVisibility(!"-".equals(Support.getDetails(pc, "tanggalPenimbangan"))
                        ? isLate(Support.getDetails(pc, "tanggalPenimbangan"), 1)
                        ? View.VISIBLE
                        : INVISIBLE
                        : INVISIBLE
                );
                viewHolder.setVitAVisibility();

//------CHILD DATA HAS BEEN SUBMITTED OR NOT
                viewHolder.weightLogo.setImageDrawable(context.getResources().getDrawable(isLate(returnLatestDate(Support.getDetails(pc, "tanggalPenimbangan"), newestDateonHistory), 0) ? R.drawable.ic_remove : R.drawable.ic_yes_large));
                viewHolder.heightLogo.setImageDrawable(context.getResources().getDrawable(!isLate(returnLatestDate(Support.getDetails(pc, "tanggalPenimbangan"), newestDateonHistory), 0) && !Support.getDetails(pc, "tinggiBadan").equals("-") ? R.drawable.ic_yes_large : R.drawable.ic_remove));
                viewHolder.vitALogo.setImageDrawable(context.getResources().getDrawable(inTheSameRegion(Support.getDetails(pc, "lastVitA")) ? R.drawable.ic_yes_large : R.drawable.ic_remove));
                viewHolder.antihelminticLogo.setImageDrawable(context.getResources().getDrawable(isGiven(pc, "obatcacing") ? R.drawable.ic_yes_large : R.drawable.ic_remove));

                setViewStatusGizi(pc, viewHolder, newestDateonHistory, history1, history2);
                //================ END OF Z-SCORE==============================//
            }
            convertView.setLayoutParams(clientViewLayoutParams);
            // return convertView;
        }
    }
//    CommonPersonObjectController householdelcocontroller;

    private void setViewStatusGizi(CommonPersonObjectClient pc, ViewHolder viewHolder, String newestDateonHistory, String[]history1, String[]history2){
        if ( !"-".equals(Support.getDetails(pc, "tanggalPenimbangan"))) {
            String stuntingStatus = "";
            String underweightStatus;
            String wastingStatus = "";
            int umur = Integer.parseInt(history1[history1.length - 1].split(":")[0]);
            double berat = Double.parseDouble(history1[history1.length - 1].split(":")[1]);
            double tinggi;
            boolean isMale = Support.getDetails(pc,"gender").toLowerCase().contains("em");

            Log.i(TAG, "setViewStatusGizi:newestDateonHistory "+ newestDateonHistory);

            System.out.println("history 1 combined: "+Support.combine(history1, ","));
            System.out.println("history 2 combined: "+Support.combine(history2, ","));

            if (umur<1857){
                ZScoreSystemCalculation calculator = new ZScoreSystemCalculation();
                underweightStatus = calculator.getWFAZScoreClassification(calculator.countWFA(isMale,umur,berat));
                if (history2[history2.length-1].split(":")[1].length()>1){
                    tinggi = Double.parseDouble(history2[history2.length - 1].split(":")[1]);
                    stuntingStatus = calculator.getHFAZScoreClassification(calculator.countHFA(isMale,umur, tinggi));
                    wastingStatus = calculator.getWFLZScoreClassification(calculator.countWFL(isMale,berat,tinggi));
                }
            } else {
                underweightStatus = hasValue(Support.getDetails(pc, "underweight")) ? setStatus(Support.getDetails(pc, "underweight")) : "-";
                stuntingStatus = hasValue(Support.getDetails(pc, "stunting")) ? setStatus(Support.getDetails(pc, "stunting")) : "-";
                wastingStatus = hasValue(Support.getDetails(pc, "wasting")) ? setStatus(Support.getDetails(pc, "wasting")) : "-";
            }
            viewHolder.stunting_status.setText(String.format("%s %s", context.getString(R.string.stunting), setStatus(stuntingStatus)));
            viewHolder.underweight.setText(String.format("%s %s", context.getString(R.string.wfa), setStatus(underweightStatus)));
            viewHolder.wasting_status.setText(String.format("%s %s", context.getString(R.string.wasting), setStatus(wastingStatus)));
        } else {
            viewHolder.underweight.setText(String.format("%s ", context.getString(R.string.wfa)));
            viewHolder.stunting_status.setText(String.format("%s ", context.getString(R.string.stunting)));
            viewHolder.wasting_status.setText(String.format("%s ", context.getString(R.string.wasting)));
        }
    }

    private String setStatus(String status){
        switch (status.toLowerCase()){
            case "underweight" :
                return context.getString(R.string.underweight);
            case "severely underweight" :
                return context.getString(R.string.s_underweight);
            case "normal":
                return context.getString(R.string.normal);
            case "overweight":
                return context.getString(R.string.overweight);
            case "severely stunted" :
                return context.getString(R.string.s_stunted);
            case "stunted" :
                return context.getString(R.string.stunted);
            case "tall" :
                return context.getString(R.string.tall);
            case "severely wasted" :
                return context.getString(R.string.s_wasted);
            case "wasted" :
                return context.getString(R.string.wasted);
            default:
                return "";
        }
    }

//    private String setGender(String gender){
//        return gender.toLowerCase().contains("em") ? context.getString(R.string.child_female) : context.getString(R.string.child_male);
//    }

    private String returnLatestDate(String date1, String date2){
        if("-".equals(date1) || "_".equals(date2)){
            return "-".equals(date1) && "-".equals(date2)
                    ? null : "-".equals(date1)
                    ? date2 : date1;
        }
        return dayRangeBetween(date1.split("-"), date2.split("-"))>0 ? date2 : date1;
    }

    private boolean isLate(String lastVisitDate,int threshold) {
        return lastVisitDate == null || lastVisitDate.length() < 6 || monthRangeToToday(lastVisitDate) > threshold;
    }

//    private boolean isDue(String lastVisitDate){
//        if(lastVisitDate==null || lastVisitDate.length()<6)
//            return true;
//
//        return dayRangeBetween(lastVisitDate.split("-"),new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()).split("-")) <= 30;
//    }

    private boolean isGiven(CommonPersonObjectClient pc, String details) {
        return Support.getDetails(pc, details) != null && (Support.getDetails(pc, details).equalsIgnoreCase("ya") || Support.getDetails(pc, details).equalsIgnoreCase("yes"));
    }

    private boolean hasValue(String data) {
        return data != null && data.length() > 2;
    }

    private int monthRangeToToday(String lastVisitDate){
        if (lastVisitDate.length()<10)
            return 0;
        String currentDate[] = new SimpleDateFormat("yyyy-MM").format(new Date()).substring(0,7).split("-");
        return ((Integer.parseInt(currentDate[0]) - Integer.parseInt(lastVisitDate.substring(0,4)))*12 +
                (Integer.parseInt(currentDate[1]) - Integer.parseInt(lastVisitDate.substring(5,7))));
    }

    /**
     *  The part of method that using to check is the last visit date was in the same region as the
     *  current vitamin A period
     **/
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

    private int dayRangeBetween(String[]startDate, String[]endDate){
        return (Integer.parseInt(endDate[0]) - Integer.parseInt(startDate[0]))*360 +
                (Integer.parseInt(endDate[1]) - Integer.parseInt(startDate[1]))*30 +
                (Integer.parseInt(endDate[2]) - Integer.parseInt(startDate[2]));
    }

    public SmartRegisterClients getClients() {
        return controller.getClients();
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption,
                                              FilterOption searchFilter, SortOption sortOption) {
        return getClients().applyFilter(villageFilter, serviceModeOption, searchFilter, sortOption);
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {
        // do nothing.
    }

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
        return null;
    }

    public LayoutInflater inflater() {
        return inflater;
    }
    @Override
    public View inflatelayoutForCursorAdapter() {
        View View = inflater().inflate(R.layout.smart_register_gizi_client, null);
        return View;
    }

    class ViewHolder {

        private TextView name ;
        private TextView age ;
        private TextView subVillage;
        private LinearLayout profilelayout;
        private ImageView profilepic;
        private ImageButton follow_up;
        private TextView fatherName;
        private TextView gender;
        private TextView dateOfBirth;
        private TextView visitDate;
        private TextView height;
        private TextView weight;
        private TextView underweight;
        private TextView stunting_status;
        private TextView wasting_status;
        private TextView absentAlert;
        private TextView weightText;
        private ImageView weightLogo;
        private TextView heightText;
        private ImageView heightLogo;
        private ImageView vitALogo;
        private ImageView antihelminticLogo;
        protected TextView vitAText;
        private TextView antihelminticText;
//        private TextView village;
//        private TextView husbandname;
//        private FrameLayout due_date_holder;
//        private Button warnbutton;


        public void setVitAVisibility(){
//            int month = Integer.parseInt(new SimpleDateFormat("MM").format(new java.util.Date()));
//            int visibility = month == 2 || month == 8 ? View.VISIBLE : View.INVISIBLE;
//             vitALogo.setVisibility(visibility);
//             vitAText.setVisibility(visibility);
        }

        public void setAntihelminticVisibility(int visibility){
            antihelminticLogo.setVisibility(visibility);
            antihelminticText.setVisibility(visibility);
        }
    }

    // TODO: NPATH 3500
    public String findDate(String startDate, int myDayAge){
        int dayAge = myDayAge;
        int[]dayLength = { 31,28,31,30,31,30,31,31,30,31,30,31 };
        int startYear = Integer.parseInt(startDate.substring(0,4));
        int startMonth = Integer.parseInt(startDate.substring(5,7));
        int startDay = Integer.parseInt(startDate.substring(8, 10));

        dayLength[1] = startYear % 4 == 0 ? 29 : 28;
        while(dayAge > dayLength[startMonth-1]){
            dayAge = dayAge - dayLength[startMonth-1];
            startMonth++;
            if(startMonth>12){
                startYear++;
                startMonth = 1;
                dayLength[1] = startYear % 4 == 0 ? 29 : 28;
            }
        }
        startDay+=dayAge;
        if(startDay > dayLength[startMonth-1]) {
            startDay=startDay - dayLength[startMonth-1];
            startMonth++;
        }
        if(startMonth>12) {
            startYear++;
            startMonth = 1;
        }

        String m = "" + (startMonth<10 ? "0"+startMonth : Integer.toString(startMonth));
        String d = "" + (startDay<10 ? "0"+startDay : Integer.toString(startDay));
        return Integer.toString(startYear)+"-"+m+"-"+d;
    }


}
