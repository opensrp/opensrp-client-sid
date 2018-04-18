package org.smartregister.vaksinator.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.smartregister.Context;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.vaksinator.R;

import java.util.Date;
import java.util.Map;


/**
 * Created by Iq on 09/06/16, modified by Marwan on 14/07/16
 */
public class VaksinatorRecapitulationActivity extends Activity {
    private Context context;
    public static CommonPersonObjectClient controller;
    public static String staticVillageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = Context.getInstance();
        setContentView(R.layout.smart_register_jurim_client_reporting);

//        AllCommonsRepository childRepository = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_anak");
//        List <CommonPersonObject> childobject = childRepository.all();

        SmartRegisterQueryBuilder queryBuilder = new SmartRegisterQueryBuilder();
        queryBuilder.SelectInitiateMainTableCounts("ec_anak");

//        String tablename, String searchJoinTable, String mainCondition, String searchFilter, String sort, int limit, int offset
//        String sql = queryBuilder.searchQueryFts("ec_anak", "", " is_closed = 0 ", " is_closed = 0 ");
//        List<String> ids = context().commonrepository("ec_anak").findSearchIds(sql);
//        String squery = queryBuilder.toStringFts(ids, "ec_anak" + "." + CommonRepository.ID_COLUMN);
//        squery = queryBuilder.endQuery(squery);

        final org.smartregister.commonregistry.CommonPersonObjectClients clients = new org.smartregister.commonregistry.CommonPersonObjectClients();
        String query = "SELECT * FROM ec_anak where ec_anak.is_closed = 0";
        //System.out.println("search query = " + query);
        Cursor cursor = context.commonrepository("ec_anak").rawCustomQueryForAdapter(query);
        cursor.moveToFirst();
        Map<String,String> details;
        cursor.moveToFirst();
        for(int i=0;i<cursor.getCount();i++) {
            cursor.moveToPosition(i);
            String baseEntityId = cursor.getString(cursor.getColumnIndex("base_entity_id"));
            //System.out.println("base entity id: "+baseEntityId);
            details = context.detailsRepository().getAllDetailsForClient(baseEntityId);
            //System.out.println(details);

            clients.add(new CommonPersonObjectClient(baseEntityId,details,cursor.getString(cursor.getColumnIndex("namaBayi"))));
        }
        cursor.close();

//        for(int i=1;i<childobject.size();i++){
//            CommonPersonObject object = childRepository.findByCaseID(childobject.get(i).getCaseId());
//            if(object.getDetails() != null)
//                //System.out.println("detail size of "+i+": "+object.getDetails().toString());
//            else
//                //System.out.println("detail size of "+i+": ");
//        }

//        Context otherContext = Context.getInstance().updateApplicationContext(this.getApplicationContext());
//
//        org.smartregister.commonregistry.CommonPersonObjectController data = new org.smartregister.commonregistry.CommonPersonObjectController(otherContext.allCommonsRepositoryobjects("anak"),
//                otherContext.allBeneficiaries(), otherContext.listCache(),
//                otherContext.personObjectClientsCache(), "nama_bayi", "anak", "tanggal_lahir",
//                org.smartregister.commonregistry.CommonPersonObjectController.ByColumnAndByDetails.byDetails);
//
//        final org.smartregister.commonregistry.CommonPersonObjectClients clients = data.getClients();
        final LocalVariable var = new LocalVariable();

        var.setDefaultSpinnerDate();
        updateView(var, clients, var.monthSpinner.getSelectedItemPosition() + 1, Integer.parseInt(var.yearSpinner.getSelectedItem().toString()));
        var.setSubtitle(staticVillageName);

        var.monthSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        updateView(var,clients,var.monthSpinner.getSelectedItemPosition()+1, Integer.parseInt(var.yearSpinner.getSelectedItem().toString()));
                        var.setSubtitle(staticVillageName);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // do nothing
                    }
                }
        );

        var.yearSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        updateView(var,clients,var.monthSpinner.getSelectedItemPosition()+1, Integer.parseInt(var.yearSpinner.getSelectedItem().toString()));
                        var.setSubtitle(staticVillageName);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // do nothing
                    }
                }
        );
//
        ImageButton backButton = (ImageButton) findViewById(R.id.btn_back_to_home);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(VaksinatorRecapitulationActivity.this, VaksinatorSmartRegisterActivity.class));
                overridePendingTransition(0, 0);
            }
        });
    }

    private int recapitulation(org.smartregister.commonregistry.CommonPersonObjectClients clients,String fieldName, String keyword){
        int counter = 0;
        org.smartregister.commonregistry.CommonPersonObjectClient cl;
        for(int i=0;i<clients.size();i++){
            cl = ((org.smartregister.commonregistry.CommonPersonObjectClient)clients.get(i));
            if (cl.getDetails().get(fieldName)!=null)
                counter = cl.getDetails().get(fieldName).contains(keyword) ? counter+1:counter;
        }
        return counter;
    }

    private int recapitulation(org.smartregister.commonregistry.CommonPersonObjectClients clients,String fieldName, String keyword, String filter){
        if(!(filter.toLowerCase().contains("under") || filter.toLowerCase().contains("over")))
            return 0;

        String[]cond = filter.split(" ");
        int counter = 0;
        org.smartregister.commonregistry.CommonPersonObjectClient cl;
        for(int i=0;i<clients.size();i++){
            cl = ((org.smartregister.commonregistry.CommonPersonObjectClient)clients.get(i));
            if (hasDate(cl.getDetails().get(fieldName))) {
                counter = cl.getDetails().get(fieldName).contains(keyword)
                          ? cond[0].equalsIgnoreCase("under")
                            ? duration(cl.getDetails().get("tanggal_lahir"),cl.getDetails().get(fieldName)) <= Integer.parseInt(cond[1])
                              ? counter + 1
                              : counter
                            : cond[0].equalsIgnoreCase("over")
                              ? duration(cl.getDetails().get("tanggal_lahir"),cl.getDetails().get(fieldName)) > Integer.parseInt(cond[1])
                                ? counter + 1
                                :counter
                              : counter
                          : counter
                ;
            }
        }
        return counter;
    }

    private int duration(String dateFrom,String dateTo){
        if (dateFrom==null || dateTo==null)
            return -1;
        return (((Integer.parseInt(dateTo.substring(0, 4)) - Integer.parseInt(dateFrom.substring(0, 4)))*360)
                +((Integer.parseInt(dateTo.substring(5, 7)) - Integer.parseInt(dateFrom.substring(5, 7)))*30)
                +(Integer.parseInt(dateTo.substring(8, 10)) - Integer.parseInt(dateFrom.substring(8, 10)))
        );
    }

//    private boolean islarger(String date, String dividerDate){
//        return true;
//    }

    @SuppressLint("SetTextI18n")
    private void updateView(LocalVariable var, org.smartregister.commonregistry.CommonPersonObjectClients clients, int month, int year){
        int counter = 0;

        String keyword = Integer.toString(year)+"-"+(month>9 ? Integer.toString(month):"0"+ Integer.toString(month));

        ((TextView) findViewById(R.id.txt_title_label)).setText(context.getStringResource(R.string.vaksinator_recapitulation_label));
        ((TextView) findViewById(R.id.hb0under)).setText("HB 0 (0-7 "+context.getStringResource(R.string.hari)+")");
        ((TextView) findViewById(R.id.hb0Over)).setText("HB 0 (>7 "+context.getStringResource(R.string.hari)+")");
        ((TextView) findViewById(R.id.campakReporting)).setText(context.getStringResource(R.string.measles));
        ((TextView) findViewById(R.id.deathUnder)).setText(context.getStringResource(R.string.meninggal)+ " <30 "+context.getStringResource(R.string.hari));
        ((TextView) findViewById(R.id.deathOver)).setText(context.getStringResource(R.string.meninggal)+ " >=30 "+context.getStringResource(R.string.hari));
        ((TextView) findViewById(R.id.pindahReporting)).setText(context.getStringResource(R.string.moving));

        ((TextView) findViewById(R.id.hbUnder7Reporting)).setText(Integer.toString(recapitulation(clients, "hb0", keyword, "under 7")));
        ((TextView) findViewById(R.id.hbOver7Reporting)).setText(Integer.toString(recapitulation(clients, "hb0", keyword, "over 7")));
        ((TextView) findViewById(R.id.bcgReporting)).setText(Integer.toString(recapitulation(clients, "bcg", keyword)));
        ((TextView) findViewById(R.id.pol1Reporting)).setText(Integer.toString(recapitulation(clients, "polio1", keyword)));
        ((TextView) findViewById(R.id.hb1Reporting)).setText(Integer.toString(recapitulation(clients, "dptHb1", keyword)));
        ((TextView) findViewById(R.id.pol2Reporting)).setText(Integer.toString(recapitulation(clients, "polio2", keyword) + counter));
        ((TextView) findViewById(R.id.hb2Reporting)).setText(Integer.toString(recapitulation(clients, "dptHb2", keyword)));
        ((TextView) findViewById(R.id.pol3Reporting)).setText(Integer.toString(recapitulation(clients, "polio3", keyword)));
        ((TextView) findViewById(R.id.hb3Reporting)).setText(Integer.toString(recapitulation(clients, "dptHb3", keyword)));
        ((TextView) findViewById(R.id.pol4Reporting)).setText(Integer.toString(recapitulation(clients, "polio4", keyword)));
        ((TextView) findViewById(R.id.ipvReporting)).setText(Integer.toString(recapitulation(clients, "ipv", keyword)));
        ((TextView) findViewById(R.id.measlesReporting)).setText(Integer.toString(recapitulation(clients, "imunisasi_campak", keyword)));
        ((TextView) findViewById(R.id.diedunder30Reporting)).setText(Integer.toString(recapitulation(clients, "tanggal_meninggal", keyword, "under 30")));
        ((TextView) findViewById(R.id.diedover30Reporting)).setText(Integer.toString(recapitulation(clients, "tanggal_meninggal", keyword, "over 30")));
        ((TextView) findViewById(R.id.movingReporting)).setText(Integer.toString(counter));
    }
    private boolean hasDate(String data) {
        return data != null && data.length() > 6;
    }

    private class LocalVariable{
//        private TextView titleLabel = (TextView) findViewById(R.id.txt_title_label);
//        private TextView hbUnderLabel = (TextView) findViewById(R.id.hb0under);
//        private TextView hbOverLabel = (TextView) findViewById(R.id.hb0Over);
//        private TextView campakLabel = (TextView) findViewById(R.id.campakReporting);
//        private TextView deadU = (TextView) findViewById(R.id.deathUnder);
//        private TextView deadO = (TextView) findViewById(R.id.deathOver);
//        private TextView hbUnder7 = (TextView) findViewById(R.id.hbUnder7Reporting);
//        private TextView hbOver7 = (TextView) findViewById(R.id.hbOver7Reporting);
//        private TextView bcg = (TextView) findViewById(R.id.bcgReporting);
//        TextView pol1 = (TextView) findViewById(R.id.pol1Reporting);
//        TextView hb1 = (TextView) findViewById(R.id.hb1Reporting);
//        TextView pol2 = (TextView) findViewById(R.id.pol2Reporting);
//        TextView hb2 = (TextView) findViewById(R.id.hb2Reporting);
//        TextView pol3 = (TextView) findViewById(R.id.pol3Reporting);
//        TextView hb3 = (TextView) findViewById(R.id.hb3Reporting);
//        TextView pol4 = (TextView) findViewById(R.id.pol4Reporting);
//        TextView ipv = (TextView) findViewById(R.id.ipvReporting);
//        TextView measles = (TextView) findViewById(R.id.measlesReporting);
//        TextView diedu30 = (TextView) findViewById(R.id.diedunder30Reporting);
//        TextView diedo30 = (TextView) findViewById(R.id.diedover30Reporting);
//        TextView moving = (TextView) findViewById(R.id.movingReporting);
//        TextView movingLabel = (TextView) findViewById(R.id.pindahReporting);
//        TextView subtitle = (TextView) findViewById(R.id.subtitle);

        private Spinner monthSpinner = (Spinner) findViewById(R.id.monthSpinner);
        private Spinner yearSpinner = (Spinner) findViewById(R.id.yearSpinner);

        private void setDefaultSpinnerDate(){
            String[]date = (new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date())).split("-");
            monthSpinner.setSelection(Integer.parseInt(date[1])-1);
            yearSpinner.setSelection(Integer.parseInt(date[0]) - 2015);
        }
        private void setSubtitle(String villageName){
            ((TextView) findViewById(R.id.subtitle)).setText( context.getStringResource(R.string.headerTextpart01) +" "+ villageName
                            + " " + context.getStringResource(R.string.headerTextpart02) +" "+ monthSpinner.getSelectedItem().toString()
                            + " " + context.getStringResource(R.string.headerTextpart03) +" "+ yearSpinner.getSelectedItem().toString());
        }

    }
}