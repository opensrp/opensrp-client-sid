package org.smartregister.bidan.provider;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.smartregister.bidan.R;
import org.smartregister.bidan.utils.BidanConstants;
import org.smartregister.bidan.utils.ImageUtils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.SmartRegisterCLientsProviderForCursorAdapter;
import org.smartregister.service.AlertService;
import org.smartregister.util.DateUtil;
import org.smartregister.util.OpenSRPImageLoader;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static org.smartregister.util.Utils.fillValue;
import static org.smartregister.util.Utils.getName;
import static org.smartregister.util.Utils.getValue;

/**
 * Created by sid-tech on 11/15/17.
 */

public class IbuSmartClientsProvider implements SmartRegisterCLientsProviderForCursorAdapter {

    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;
    private final AlertService alertService;
    private final AbsListView.LayoutParams clientViewLayoutParams;
    private final CommonRepository commonRepository;

    public IbuSmartClientsProvider(Context context, View.OnClickListener onClickListener, 
                                   AlertService alertService, CommonRepository commonRepository) {
        this.onClickListener = onClickListener;
        this.context = context;
        this.alertService = alertService;
        this.commonRepository = commonRepository;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT, (int) context.getResources().getDimension(org.smartregister.R.dimen.list_item_height));
    }
    
    @Override
    public void getView(Cursor cursor, SmartRegisterClient smartRegisterClient, View convertView) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) smartRegisterClient;

//        fillValue((TextView) convertView.findViewById(R.id.child_zeir_id), getValue(pc.getColumnmaps(), BidanConstants.KEY.BIDAN_ID, false));

        String firstName = getValue(pc.getColumnmaps(), BidanConstants.KEY.FIRST_NAME, true);
        String lastName = getValue(pc.getColumnmaps(), BidanConstants.KEY.LAST_NAME, true);
        String childName = getName(firstName, lastName);

        String motherFirstName = getValue(pc.getColumnmaps(), BidanConstants.KEY.MOTHER_FIRST_NAME, true);
        if (StringUtils.isBlank(childName) && StringUtils.isNotBlank(motherFirstName)) {
            childName = "B/o " + motherFirstName.trim();
        }
//        fillValue((TextView) convertView.findViewById(R.id.child_name), childName);

        String motherName = getValue(pc.getColumnmaps(), BidanConstants.KEY.MOTHER_FIRST_NAME, true) + " " + getValue(pc, BidanConstants.KEY.MOTHER_LAST_NAME, true);
        if (!StringUtils.isNotBlank(motherName)) {
            motherName = "M/G: " + motherName.trim();
        }
//        fillValue((TextView) convertView.findViewById(R.id.child_mothername), motherName);

        DateTime birthDateTime;
        String dobString = getValue(pc.getColumnmaps(), BidanConstants.KEY.DOB, false);
        String durationString = "";
        if (StringUtils.isNotBlank(dobString)) {
            try {
                birthDateTime = new DateTime(dobString);
                String duration = DateUtil.getDuration(birthDateTime);
                if (duration != null) {
                    durationString = duration;
                }
            } catch (Exception e) {
                Log.e(getClass().getName(), e.toString(), e);
            }
        }
//        fillValue((TextView) convertView.findViewById(R.id.child_age), durationString);

//        fillValue((TextView) convertView.findViewById(R.id.child_card_number), pc.getColumnmaps(), BidanConstants.KEY.EPI_CARD_NUMBER, false);

        String gender = getValue(pc.getColumnmaps(), BidanConstants.KEY.GENDER, true);

//        final ImageView profilePic = (ImageView) convertView.findViewById(R.id.child_profilepic);
        final ImageView profilePic = (ImageView) convertView.findViewById(R.id.img_profile);
        int defaultImageResId = ImageUtils.profileImageResourceByGender(gender);
        profilePic.setImageResource(defaultImageResId);
        if (pc.entityId() != null) { //image already in local storage most likely ):
            //set profile image by passing the client id.If the image doesn't exist in the image repository then download and save locally
            profilePic.setTag(org.smartregister.R.id.entity_id, pc.entityId());
            DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(pc.entityId(), OpenSRPImageLoader.getStaticImageListener(profilePic, 0, 0));
        }

        // TODO change custom layout ibu_profile_info_layout
        convertView.findViewById(R.id.profile_info_layout).setTag(smartRegisterClient);
        convertView.findViewById(R.id.profile_info_layout).setOnClickListener(onClickListener);

        String lostToFollowUp = getValue(pc.getColumnmaps(), BidanConstants.KEY.LOST_TO_FOLLOW_UP, false);
        String inactive = getValue(pc.getColumnmaps(), BidanConstants.KEY.INACTIVE, false);

//        try {
//            Utils.startAsyncTask(new WeightAsyncTask(convertView, pc.entityId(), lostToFollowUp, inactive, smartRegisterClient, cursor), null);
//            Utils.startAsyncTask(new VaccinationAsyncTask(convertView, pc.entityId(), dobString, lostToFollowUp, inactive, smartRegisterClient, cursor), null);
//        } catch (Exception e) {
//            Log.e(getClass().getName(), e.getMessage(), e);
//        }
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption filterOption, ServiceModeOption serviceModeOption, FilterOption filterOption1, SortOption sortOption) {
        return null;
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {

    }

    @Override
    public OnClickFormLauncher newFormLauncher(String s, String s1, String s2) {
        return null;
    }

    @Override
    public LayoutInflater inflater() {
        return null;
    }

    @Override
    public View inflatelayoutForCursorAdapter() {
        return null;
    }
}
