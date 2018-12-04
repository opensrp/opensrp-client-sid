package org.smartregister.bidan.utils;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.smartregister.Context;
import org.smartregister.bidan.R;
import org.smartregister.bidan.activity.DetailMotherActivity;
import org.smartregister.bidan.activity.MapActivity;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.repository.DetailsRepository;

import static org.smartregister.util.StringUtil.humanize;

public class MyInfoWindow extends InfoWindow {
    CommonPersonObjectClient client;
    LinearLayout layout = (LinearLayout) mView.findViewById(R.id.bubble_layout);
    Button btnMoreInfo = (Button) mView.findViewById(R.id.bubble_moreinfo);
    TextView txtTitle = (TextView) mView.findViewById(R.id.bubble_title);
    TextView txtDescription = (TextView) mView.findViewById(R.id.bubble_description);
    Activity activity;
    public MyInfoWindow(int layoutResId, MapView mapView, CommonPersonObjectClient client, Activity activity) {
        super(layoutResId, mapView);
        this.client=client;
        this.activity=activity;
        btnMoreInfo.setTag(client);
        setTxtTitle();
        setTxtDescription();
        setBtnMoreInfoListener();
    }
    public void onClose() {
    }

    public LinearLayout getLayout() {
        return layout;
    }

    public Button getBtnMoreInfo() {
        return btnMoreInfo;
    }

    public TextView getTxtTitle() {
        return txtTitle;
    }

    public TextView getTxtDescription() {
        return txtDescription;
    }

    public void onOpen(Object arg0) {
        layout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Override Marker's onClick behaviour here
            }
        });

    }

    public void setTxtDescription(String description){
        txtDescription.setText(description);
    }

    public void setTxtDescription(){
        DetailsRepository detailsRepository = Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(client);
        String snippet = "";
        snippet = snippet.concat("\n"+"Nomor Ibu : "+(client.getDetails().get("noIbu") != null ? client.getDetails().get("noIbu") : ""));
        snippet = snippet.concat("\n"+"NIK : "+(client.getDetails().get("nik") != null ? client.getDetails().get("nik") : "-"));
        snippet = snippet.concat("\n"+"Dusun : "+getStrValue("address1",client));
        String tgl = client.getDetails().get("tanggalLahir") != null ? client.getDetails().get("tanggalLahir") : "-";

        String tgl_lahir = "null";
        if (tgl != null && !tgl.isEmpty()) {
            tgl_lahir = tgl.substring(0, tgl.indexOf("T"));
        }
        snippet = snippet.concat("\n"+"Tanggal Lahir : "+tgl_lahir);
        snippet = snippet.concat("\n"+"Nama Suami : "+(client.getColumnmaps().get("namaSuami") != null ? client.getColumnmaps().get("namaSuami") : "-"));
        snippet = snippet.concat("\n"+"Golongan Darah : "+getStrValue("golonganDarah",client));
        txtDescription.setText(snippet);
    }

    public void setTxtTitle(String title){
        txtTitle.setText(title);
    }

    public void setTxtTitle(){
        txtTitle.setText(client.getColumnmaps().get("namalengkap"));
    }

    public void setBtnMoreInfoListener(View.OnClickListener listener){
        btnMoreInfo.setOnClickListener(listener);
    }

    public void setBtnMoreInfoListener(){
        btnMoreInfo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Implement onClick behaviour
                DetailMotherActivity.motherClient = (CommonPersonObjectClient) v.getTag();
                Intent intent = new Intent(activity, DetailMotherActivity.class);
                activity.startActivity(intent);
            }
        });
        btnMoreInfo.setVisibility(Button.VISIBLE);
    }



    private String getStrValue(String str, CommonPersonObjectClient client) {
        return String.format(": %s", humanize(client.getDetails().get(str) != null ? client.getDetails().get(str) : "-"));
    }
}
