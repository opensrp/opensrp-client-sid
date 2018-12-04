package org.smartregister.bidan.facial.utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.qualcomm.snapdragon.sdk.face.FacialProcessing;

import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.Context;
import org.smartregister.bidan.facial.FacialRecognitionLibrary;
import org.smartregister.bidan.R;
import org.smartregister.bidan.facial.activity.ClientsListActivity;
import org.smartregister.bidan.facial.activity.OpenCameraActivity;
import org.smartregister.bidan.facial.activity.PhotoConfirmationActivity;
import org.smartregister.bidan.facial.domain.ProfileImage;
import org.smartregister.bidan.facial.repository.ImageRepository;
import org.smartregister.util.OpenSRPImageLoader;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;
import okhttp3.ResponseBody;

import static org.apache.commons.lang3.StringUtils.capitalize;


/**
 * Created by wildan on 1/4/17.
 */
public class Tools {

    private static final String TAG = Tools.class.getSimpleName();
    public static final int CONFIDENCE_VALUE = 58;
    private static String[] splitStringArray;
    private static Bitmap dummyImage = null;
    private static byte[] headerOfVector;
    private static byte[] bodyOfVector;
    private static byte[] lastContentOfVector;
    //    private static String headerOne;
    private static byte[] albumVectors;
    private static Tools tools;
    private Bitmap helperImage = null;
    private Canvas canvas = null;
    OpenCameraActivity ss = new OpenCameraActivity();
    ClientsListActivity cl = new ClientsListActivity();
    private static HashMap<String, String> hash;
    private String albumBuffer;
    private List<ProfileImage> list;
//    private static String anmId = Context.getInstance().allSharedPreferences().fetchRegisteredANM();
    private static ProfileImage profileImage = new ProfileImage();
    private static ImageRepository imageRepo;
//    private FaceRepository faceRepo = (FaceRepository) new FaceRepository().faceRepository();

    static String emptyAlbum = "[32, 0, 0, 0, 76, 65, -68, -20, 77, 116, 46, 83, 105, 110, 97, 105, 6, 0, 0, 0, -24, 3, 0, 0, 10, 0, 0, 0, 0, 0, 0, 0]";
    private static String headerOne = emptyAlbum;
    static String singleHeader = "[76, 1, 0, 0, 76, 65, -68, -20, 77, 116, 46, 83, 105, 110, 97, 105, 6, 0, 0, 0, -24, 3, 0, 0, 10, 0, 0, 0, 1, 0, 0, 0]";

    private byte[] allFileVector;
    static Context mContext;

    public Tools(Context mContext) {
        imageRepo = ImageRepository.getInstance();
//        Tools.mContext = mContext;
    }

    /**
     * Method to Stored Bitmap as File and Buffer
     *
     * @param bitmap     Photo bitmap
     * @param entityId   Base user id
     * @param faceVector Vector from face
     * @param updated    capture mode
     * @return Boolean
     */
    public static boolean WritePictureToFile(Bitmap bitmap, String entityId, String[] faceVector, boolean updated) {

        File pictureFile = getOutputMediaFile(0, entityId);
        File thumbs_photo = getOutputMediaFile(1, entityId);

        if (pictureFile == null || thumbs_photo == null) {
            Log.e(TAG, "Error creating media file, check storage permissions!");
            return false;
        }

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            Log.e(TAG, "Wrote image to " + pictureFile);

            String photoPath = pictureFile.toString();
            Log.e(TAG, "Photo Path = " + photoPath);

//            Create Thumbs
            FileOutputStream tfos = new FileOutputStream(thumbs_photo);
            final int THUMBSIZE = FaceConstants.THUMBSIZE;

            Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(photoPath),
                    THUMBSIZE, THUMBSIZE);
            ThumbImage.compress(Bitmap.CompressFormat.PNG, 100, tfos);
            tfos.close();
            Log.e(TAG, "Wrote Thumbs image to " + thumbs_photo);

//           FIXME File & Database Stored
            saveStaticImageToDisk(entityId, ThumbImage, Arrays.toString(faceVector), updated);

            saveToDb(entityId, thumbs_photo.toString(), Arrays.toString(faceVector), updated);

            return true;

        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
        return false;
    }

    private static void saveToDb(String entityId, String absoluteFileName, String faceVector, boolean updated) {

        Log.e(TAG, "saveToDb: " + "start");
        // insert into the db local
        if (!updated) {
            profileImage.setId(Long.valueOf(UUID.randomUUID().toString()));
//            profileImage.setAnmId(anmId);
            profileImage.setBaseEntityId(entityId);
            profileImage.setContenttype("jpeg");
            profileImage.setFilepath(absoluteFileName);
            profileImage.setFilecategory("profilepic");
            profileImage.setFilevector(faceVector);
            profileImage.setSyncStatus((String) ImageRepository.TYPE_Unsynced);

            imageRepo.add(profileImage, entityId);
        } else {
            imageRepo.updateByEntityId(entityId, faceVector);
        }
        Log.e(TAG, "saveToDb: " + "done");

    }

    /**
     * Method create new file
     *
     * @param mode     capture mode.
     * @param entityId Base user id.
     * @return File
     */
    @Nullable
    private static File getOutputMediaFile(Integer mode, String entityId) {
        // Mode 0 = Original
        // Mode 1 = Thumbs

        // Location use app_dir
        String imgFolder = (mode == 0) ?
                DrishtiApplication.getAppDir() :
                DrishtiApplication.getAppDir() + File.separator + ".thumbs";

        File mediaStorageDir = new File(imgFolder);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            Log.e(TAG, "failed to find directory " + mediaStorageDir.getAbsolutePath());
            if (!mediaStorageDir.mkdirs()) {
                Log.e(TAG, "Created new directory " + mediaStorageDir.getAbsolutePath());
                return null;
            }
        }

        // Create a media file name
        return new File(String.format("%s%s%s.jpg", mediaStorageDir.getPath(), File.separator, entityId));
    }

    /**
     * Methof for Draw Information of existing Person
     *
     * @param rect          Rectangular
     * @param mutableBitmap Bitmap
     * @param pixelDensity  Pixel group area
     * @param personName    name
     */
    public static void drawInfo(Rect rect, Bitmap mutableBitmap, float pixelDensity, String personName) {
//        Log.e(TAG, "drawInfo: rect " + rect);
//        Log.e(TAG, "drawInfo: bitmap" + mutableBitmap);

        // Extra padding around the faceRects
        rect.set(rect.left -= 20, rect.top -= 20, rect.right += 20, rect.bottom += 20);
        Canvas canvas = new Canvas(mutableBitmap);
        Paint paintForRectFill = new Paint();

        // Draw rect fill
        paintForRectFill.setStyle(Paint.Style.FILL);
        paintForRectFill.setColor(Color.WHITE);
        paintForRectFill.setAlpha(80);

        // Draw rectangular strokes
        Paint paintForRectStroke = new Paint();
        paintForRectStroke.setStyle(Paint.Style.STROKE);
        paintForRectStroke.setColor(Color.GREEN);
        paintForRectStroke.setStrokeWidth(5);
        canvas.drawRect(rect, paintForRectFill);
        canvas.drawRect(rect, paintForRectStroke);

//        float pixelDensity = getResources().getDisplayMetrics().density;
        int textSize = (int) (rect.width() / 25 * pixelDensity);

        Paint paintForText = new Paint();
        Paint paintForTextBackground = new Paint();
        Typeface tp = Typeface.SERIF;
        Rect backgroundRect = new Rect(rect.left, rect.bottom, rect.right, (rect.bottom + textSize));

        paintForText.setColor(Color.WHITE);
        paintForText.setTextSize(textSize);
        paintForTextBackground.setStyle(Paint.Style.FILL);
        paintForTextBackground.setColor(Color.BLACK);
        paintForText.setTypeface(tp);
        paintForTextBackground.setAlpha(80);

        if (personName != null) {
            canvas.drawRect(backgroundRect, paintForTextBackground);
            canvas.drawText(personName, rect.left, rect.bottom + (textSize), paintForText);
        } else {
            canvas.drawRect(backgroundRect, paintForTextBackground);
            canvas.drawText("Not identified", rect.left, rect.bottom + (textSize), paintForText);
        }

//        confirmationView.setImageBitmap(mutableBitmap);

    }

    /**
     * Draw Area that detected as Face
     *
     * @param rect          Rectangular
     * @param mutableBitmap Modified Bitmap
     * @param pixelDensity  Pixel area density
     */
    public static void drawRectFace(Rect rect, Bitmap mutableBitmap, float pixelDensity) {

        // Extra padding around the faceRects
        rect.set(rect.left -= 20, rect.top -= 20, rect.right += 20, rect.bottom += 20);
        Canvas canvas = new Canvas(mutableBitmap);

        // Draw rect fill
        Paint paintForRectFill = new Paint();
        paintForRectFill.setStyle(Paint.Style.FILL);
        paintForRectFill.setColor(Color.BLACK);
        paintForRectFill.setAlpha(80);

        // Draw rect strokes
        Paint paintForRectStroke = new Paint();
        paintForRectStroke.setStyle(Paint.Style.STROKE);
        paintForRectStroke.setColor(Color.GREEN);
        paintForRectStroke.setStrokeWidth(3);

        // Draw Face detected Area
        canvas.drawRect(rect, paintForRectFill);
        canvas.drawRect(rect, paintForRectStroke);

    }

    /**
     * Stored list detected Base entity ID to Shared Preference for buffered
     *
     * @param hashMap HashMap
     * @param context context
     */
    public static void saveHash(HashMap<String, String> hashMap, android.content.Context context) {
        SharedPreferences settings = context.getSharedPreferences(FaceConstants.HASH_NAME, 0);

        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
//        Log.e(TAG, "Hash Save Size = " + hashMap.size());
        for (String s : hashMap.keySet()) {
//            Log.e(TAG, "saveHash: " + s);
            editor.putString(s, hashMap.get(s));
        }
        editor.apply();
    }

    /**
     * Get Existing Hash
     *
     * @param context Context
     * @return hash
     */
    public static HashMap<String, String> retrieveHash(android.content.Context context) {
        SharedPreferences settings = context.getSharedPreferences(FaceConstants.HASH_NAME, 0);
        HashMap<String, String> hash = new HashMap<>();
        hash.putAll((Map<? extends String, ? extends String>) settings.getAll());
        return hash;
    }

    /**
     * Save Vector array to xml
     */
    public static void saveAlbum(String albumBuffer, android.content.Context context) {
        SharedPreferences settings = context.getSharedPreferences(FaceConstants.ALBUM_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(FaceConstants.ALBUM_ARRAY, albumBuffer);
        editor.apply();
    }

    public static void loadAlbum(android.content.Context context) {

        SharedPreferences settings = context.getSharedPreferences(FaceConstants.ALBUM_NAME, 0);
        String arrayOfString = settings.getString(FaceConstants.ALBUM_ARRAY, null);
        byte[] albumArray;

        if (arrayOfString != null) {

            splitStringArray = arrayOfString.substring(1, arrayOfString.length() - 1).split(", ");
            Log.e(TAG, "splitStringArray="+splitStringArray);

            albumArray = new byte[splitStringArray.length];


            for (int i = 0; i < splitStringArray.length; i++) {
                albumArray[i] = Byte.parseByte(splitStringArray[i]);
            }

            boolean result = FacialRecognitionLibrary.faceProc.deserializeRecognitionAlbum(albumArray);

            if (result) Log.e(TAG, "loadAlbum: "+"Succes" );

        } else {
            Log.e(TAG, "loadAlbum: " + "is it your first record ? if no, there is problem happen.");
        }
    }

    public static void alertDialog(android.content.Context context, int opt) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
//        Tools tools = new Tools();
//        alertDialog.setMessage(message);
        String message = "";
        switch (opt) {
            case 0:
                message = "Are you sure to empty The Album?";
//                doEmpty;
                break;
            case 1:
                message = "Are you sure to delete item";
                break;
            default:
                break;
        }
        alertDialog.setMessage(message);
//        alertDialog.setButton("OK", do);
        alertDialog.setPositiveButton("ERASE", tools.doEmpty);
        alertDialog.show();
    }

    private DialogInterface.OnClickListener doEmpty = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
            boolean result = OpenCameraActivity.faceProc.resetAlbum();
            if (result) {
//                HashMap<String, String> hashMap = OpenCameraActivity.retrieveHash(getApplicationContext());
//                HashMap<String, String> hashMap = retrieveHash(getApplicationContext());
//                HashMap<String, String> hashMap = retrieveHash();
//                hashMap.clear();
//                OpenCameraActivity ss = new OpenCameraActivity();
//                saveHash(hashMap, getApplicationContext());
//                saveAlbum();
//                Toast.makeText(getApplicationContext(),
//                        "Album Reset Successful.",
//                        Toast.LENGTH_LONG).show();
            } else {
//                Toast.makeText(
//                        getApplicationContext(),
//                        "Internal Error. Reset album failed",
//                        Toast.LENGTH_LONG).show();
            }
        }
    };

    public void resetAlbum() {

        Log.e(TAG, "resetAlbum: " + "start");
        boolean result = OpenCameraActivity.faceProc.resetAlbum();

        if (result) {
            // Clear data
            // TODO: Null getApplication COntext
//            HashMap<String, String> hashMap = OpenCameraActivity.retrieveHash(new ClientsListActivity().getApplicationContext());
            HashMap<String, String> hashMap = OpenCameraActivity.retrieveHash(mContext.applicationContext().getApplicationContext());
            hashMap.clear();
//            saveHash(hashMap, cl.getApplicationContext());
            saveHash(hashMap, mContext.applicationContext().getApplicationContext());
//            saveAlbum();

//            Toast.makeText(cl.getApplicationContext(), "Reset Succesfully done!", Toast.LENGTH_LONG).show();
            Toast.makeText(mContext.applicationContext().getApplicationContext(), "Reset Succesfully done!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mContext.applicationContext().getApplicationContext(), "Reset Failed!", Toast.LENGTH_LONG).show();

        }
        Log.e(TAG, "resetAlbum: " + "finish");
    }

    /**
     * Fetch data from API (json
     * @param mContext
     * @param facialRepository
     */
    public static void setVectorfromAPI(Context mContext, ImageRepository facialRepository) {
        Log.i(TAG, "setVectorfromAPI: Start" );
        // Get URL Information
//        String DRISTHI_BASE_URL = mContext.configuration().dristhiBaseURL();
//        String user = mContext.allSharedPreferences().fetchRegisteredANM();
//        String location = mContext.allSharedPreferences().getPreference("locationId");
//
//        final String pwd = mContext.allSettings().fetchANMPassword();
//        final String api_url = DRISTHI_BASE_URL + "/multimedia-file?locationid=" + location;
//
//        if( (DRISTHI_BASE_URL != null && !DRISTHI_BASE_URL.isEmpty()) && (user != null && !user.isEmpty()) && (pwd != null && !pwd.isEmpty())){
//            try {
//                WebUtils.fetch(api_url, user, pwd);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
//            Log.e(TAG, "setVectorfromAPI: URL, Username or Password is Null or Empty. Please Check your Configuration" );
//        }

        setAppContext(mContext);

    }

    private static void insertUpdateVector(ResponseBody rBody) {
        PatientFace[] pFace = null;

//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            pFace = mapper.readValue(rBody.bytes(), PatientFace[].class);

//            Log.e(TAG, "insertUpdateVector: pFace "+ Arrays.toString(pFace));

//            String response = new String(bytes.bytes());
//            Log.e(TAG, "insertUpdateVector: response "+ response );
//            JSONArray ja = new JSONArray(response);
//
//            for (int i = 0; i < ja.length(); i++) {
//                JSONObject data = ja.getJSONObject(i);
//
//                String uid = data.getString("caseId");
//                String anmId = data.getString("providerId");
////                        String uid = data.getString("caseId");
//
//                Log.e(TAG, "insertOrUpdate: uid "+ uid );
//                // To AlbumArray
//                String faceVector = data.getJSONObject("attributes").getString("faceVector");
//
//                // Update Table ImageList on existing record based on entityId where faceVector== null
//                ProfileImage profileImage = new ProfileImage();
////                profileImage.setId(UUID.randomUUID().toString());
//                // TODO : get anmID from ?
//                profileImage.setAnmId(anmId);
//                profileImage.setBaseEntityId(uid);
////                profileImage.setFilepath(null);
////                profileImage.setFilecategory("profilepic");
////                profileImage.setSyncStatus(ImageRepository.TYPE_Synced);
//
//                // TODO : fetch vector from imagebitmap
//                profileImage.setFilevector(faceVector);
//
////                imageRepo.createOrUpdate(profileImage, uid);
//                imageRepo.add(profileImage, uid);
//
//            }
//
//        } catch ( IOException e) {
//            e.printStackTrace();
//        }
    }


    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }
    //and use it as getClient().get(Context, URL, RequestParams, Callback

    public static AsyncHttpClient getClient() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(20000);
        client.setMaxRetriesAndTimeout(1, 5000);
        return client;
    }

    private static void getImages(final AsyncHttpClient client, final String api_url) {

        client.get(api_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e(TAG, "onSuccess: " + statusCode);

                insertOrUpdate(responseBody);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(TAG, "onFailure: reconnect " + api_url);
                getImages(client, api_url);
            }
        });
    }

    static void insertOrUpdate(byte[] responseBody) {
        Log.e(TAG, "insertOrUpdate: START" );

        try {
            JSONArray response = new JSONArray(new String(responseBody));

            for (int i = 0; i < response.length(); i++) {
                JSONObject data = response.getJSONObject(i);

                String uid = data.getString("caseId");
                String anmId = data.getString("providerId");
//                        String uid = data.getString("caseId");

                Log.e(TAG, "insertOrUpdate: uid "+ uid );
                // To AlbumArray
                String faceVector = data.getJSONObject("attributes").getString("faceVector");

                // Update Table ImageList on existing record based on entityId where faceVector== null
                ProfileImage profileImage = new ProfileImage();
//                profileImage.setId(UUID.randomUUID().toString());
                // TODO : get anmID from ?
//                profileImage.setAnmId(anmId);
                profileImage.setBaseEntityId(uid);
//                profileImage.setFilepath(null);
//                profileImage.setFilecategory("profilepic");
//                profileImage.setSyncStatus(ImageRepository.TYPE_Synced);

                // TODO : fetch vector from imagebitmap
                profileImage.setFilevector(faceVector);

//                imageRepo.createOrUpdate(profileImage, uid);
                imageRepo.add(profileImage, uid);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // TODO : check mContext , imageRepo not NULL
        setVectorsBuffered(mContext, imageRepo);
        download_images();
        Log.e(TAG, "insertOrUpdate: END " );

    }

    /**
     * Method to Parse String
     *
     * @param arrayOfString
     * @return
     */
    private String[] parseArray(String arrayOfString) {

        return arrayOfString.substring(1,
                arrayOfString.length() - 1).split(", ");
    }

    /**
     * Save to Buffer from Local DB
     *
     * @param context
     */

    public static void saveAndClose(
            android.content.Context context,
            String entityId,
            boolean updated,
            FacialProcessing objFace,
            int arrayPossition,
            Bitmap storedBitmap,
            String className) {

        byte[] faceVector;

        setAppContext(mContext);

        // New record
        if (!updated && objFace != null) {

            int result = objFace.addPerson(arrayPossition);
            faceVector = objFace.serializeRecogntionAlbum();

//            hash = retrieveHash(context);
//            hash.put(entityId, Integer.toString(result));

            // Save Hash to Device
//            saveHash(hash, context);

            // Save to Device buffer
//            saveAlbum(Arrays.toString(faceVector), context);

            String albumBufferArr = Arrays.toString(faceVector);
            String[] faceVectorContent = albumBufferArr.substring(1, albumBufferArr.length() - 1).split(", ");

            // Get Face Vector Contnt Only by removing Header
            faceVectorContent = Arrays.copyOfRange(faceVectorContent, faceVector.length - 300, faceVector.length);
            WritePictureToFile(storedBitmap, entityId, faceVectorContent, updated);

            // Reset Album to get Single Face Vector

        } else if(updated && objFace != null){

            int update_result = objFace.updatePerson(Integer.parseInt(hash.get(entityId)), 0);

            if (update_result == 0) {

                Log.e(TAG, "saveAndClose: " + "success");

            } else {

                Log.e(TAG, "saveAndClose: " + "Maximum Reached Limit for Face");

            }

            faceVector = objFace.serializeRecogntionAlbum();

            // TODO : update only face vector
            saveAlbum(Arrays.toString(faceVector), context);
        } else {
            Log.e(TAG, "saveAndClose: "+ "Face Not Found" );
        }

        new PhotoConfirmationActivity().finish();

        Class<?> origin_class = null;

//        if(className.equals(KIDetailActivity.class.getSimpleName())){
//            origin_class = KIDetailActivity.class;
//        }
//        else if(className.equals(KBDetailActivity.class.getSimpleName())){
//            origin_class = KBDetailActivity.class;
//        } else if(className.equals(ANCDetailActivity.class.getSimpleName())){
//            origin_class = ANCDetailActivity.class;
//        } else if(className.equals(PNCDetailActivity.class.getSimpleName())){
//            origin_class = PNCDetailActivity.class;
//        }
//        else if(className.equals(AnakDetailActivity.class.getSimpleName())){
//            origin_class = AnakDetailActivity.class;
//        }

        // TODO Crash saved after long time no use
        if (mContext == null) {
            Log.e(TAG, "saveAndClose: Context NULL" );

//            mContext = getAppContext();
//            Intent resultIntent = new Intent(mContext.applicationContext(), origin_class);
//            Intent resultIntent = new Intent(context, origin_class);
//            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            mContext.applicationContext().startActivity(resultIntent);
        } else {
            Log.e(TAG, "saveAndClose: Context Opensrp "+ mContext.applicationContext() );
            Log.e(TAG, "saveAndClose: Context Android "+ context );
//            Intent resultIntent = new Intent(mContext.applicationContext(), origin_class);
//            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            mContext.applicationContext().startActivity(resultIntent);
        }
        Log.e(TAG, "saveAndClose: " + "end");
    }


    public static void setVectorsBuffered(Context mContext, ImageRepository imageRepo) {
        Log.i(TAG, "setVectorsBuffered: Stored vector as Album xml file.");

        if (imageRepo == null) {
            imageRepo = ImageRepository.getInstance();
        }

        List<ProfileImage> vectorList = imageRepo.getAllFaceVectorImages();
        Log.d(TAG, "setVectorsBuffered: vectorList="+vectorList);
        Log.d(TAG, "setVectorsBuffered: vectorListSize="+vectorList.size());

        if (vectorList.size() != 0) {

            hash = retrieveHash(mContext.applicationContext().getApplicationContext());

            Log.d(TAG, "setVectorsBuffered: hash="+hash);

            String[] albumBuffered = new String[0];

//            int i = 0;
            for (ProfileImage profileImage : vectorList) {
                String[] vectorFace;
                if (profileImage.getFilevector() != null) {

                    vectorFace = profileImage.getFilevector().substring(1, profileImage.getFilevector().length() - 1).split(", ");

                    // First index value of Vector Body
//                    vectorFace[0] = String.valueOf(i);

//                    vectorFace[0] = String.valueOf((i%128) % 256 - 128);

                    albumBuffered = ArrayUtils.addAll(albumBuffered, vectorFace);
                    hash.put(profileImage.getBaseEntityId(), vectorFace[0]);

                } else {
                    Log.e(TAG, "setVectorsBuffered: Profile Image Null");
                }
//                i++;
//                if (i > 127) i = -128;

            }

            albumBuffered = ArrayUtils.addAll(getHeaderBaseUserCount(vectorList.size()), albumBuffered);

            /**
             * Save vector body to Buffered
             */
            saveAlbum(Arrays.toString(albumBuffered), mContext.applicationContext());
            saveHash(hash, mContext.applicationContext());
            Log.d(TAG, "setVectorsBuffered: saveHash="+hash);

        } else {
            Log.e(TAG, "setVectorsBuffered: "+ "Multimedia Table Not ready" );
        }

    }

    private static String[] getHeaderBaseUserCount(int i) {
//        String headerNew = imageRepo.findByUserCount(n);
//        return headerNew.substring(1, headerNew.length() -1).split(", ");

        Log.i(TAG, "getHeaderBaseUserCount: Number User"+ i );

//        Init value
        int n = i-1;
        // start formula
        int n0 = 76;
        int max = 128;
        int min = -128;
        int range = max - min;
        int idx0,idx1, idx2,idx3,idx4;

        idx0 = (((n0 + max) + (n * 44)) % range) + min;
        idx1 = (1+n)+(((n0) + (n * 44)) / range);
        idx2 = (idx1+128) % 256 - 128;
        idx3 = n / 218;
        idx4 = (1+n+128) % 256 - 128;
        // end formula

        String[] newHeader = singleHeader.substring(1, singleHeader.length() - 1).split(", ");

        newHeader[0] = String.valueOf(idx0);
        newHeader[1] = String.valueOf(idx2);
        newHeader[2] = String.valueOf(idx3);
        newHeader[28] = String.valueOf(idx4);

        return newHeader;
    }

    public static void saveStaticImageToDisk(String entityId, Bitmap image, String contentVector, boolean updated) {
        String anmId = Context.getInstance().allSharedPreferences().fetchRegisteredANM();

        String[] res = contentVector.substring(1, contentVector.length() - 1).split(",");

        Log.e(TAG, "saveStaticImageToDisk: " + res.length);
        String[] faceVector = Arrays.copyOfRange(res, 32, 332);
        Log.e(TAG, "saveStaticImageToDisk: " + faceVector.length);

        if (image != null) {
            OutputStream os = null;
            try {

                if (entityId != null && !entityId.isEmpty()) {
                    final String absoluteFileName = DrishtiApplication.getAppDir() + File.separator + entityId + ".JPEG";

                    File outputFile = new File(absoluteFileName);
                    os = new FileOutputStream(outputFile);
                    Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
                    if (compressFormat != null) {
                        image.compress(compressFormat, 100, os);
                    } else {
                        throw new IllegalArgumentException(
                                "Failed to save static image, could not retrieve image compression format from name "
                                        + absoluteFileName);
                    }

                    // insert into the db local
                    ProfileImage profileImage = new ProfileImage();

                    profileImage.setId(Long.valueOf(UUID.randomUUID().toString()));
//                    profileImage.setAnmId(anmId);
                    profileImage.setBaseEntityId(entityId);
                    profileImage.setContenttype("jpeg");
                    profileImage.setFilepath(absoluteFileName);
                    profileImage.setFilecategory("profilepic");
                    profileImage.setFilevector(Arrays.toString(faceVector));
                    profileImage.setSyncStatus(String.valueOf(ImageRepository.TYPE_Unsynced));

                    imageRepo.add(profileImage, entityId);
                }

            } catch (FileNotFoundException e) {
                Log.e(TAG, "Failed to save static image to disk");
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to close static images output stream after attempting to write image");
                    }
                }
            }
        }
    }


    public static void download_images() {
        Log.e(TAG, "download_images: START" );
        try {
            List<String> images = imageRepo.findAllUnDownloaded();
            for (String uid : images){
                ImageView iv = new ImageView(mContext.applicationContext());
                // TODO setTag+"The key must be an application-specific resource id"
                iv.setTag(R.id.entity_id, uid);
                DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(uid, OpenSRPImageLoader.getStaticImageListener(iv, 0, 0));
                Log.e(TAG, "download_images: undownload "+ uid );

            }
        } catch (Exception e){
            Log.e(TAG, "download_images: "+ e.getMessage() );
        }
        Log.e(TAG, "download_images: FINISHED" );
    }

    public static void setAppContext(Context context) {
        Tools.mContext = context;
    }

    public static Context getAppContext(){
        return Tools.mContext;
    }

    public void setAlbumBuffer(String albumBuffer) {
        this.albumBuffer = albumBuffer;
    }

    public String getAlbumBuffer() {

        return albumBuffer;
    }


    public static boolean isSupport() {
        if(FacialProcessing.isFeatureSupported(FacialProcessing.FEATURE_LIST.FEATURE_FACIAL_PROCESSING)){
            return true;
        } else {
        return false;
        }
    }


}
