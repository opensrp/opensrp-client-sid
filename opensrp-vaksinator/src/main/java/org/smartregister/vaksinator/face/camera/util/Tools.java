package org.smartregister.vaksinator.face.camera.util;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.qualcomm.snapdragon.sdk.face.FacialProcessing;

import org.apache.commons.lang3.ArrayUtils;
import org.smartregister.Context;
import org.smartregister.domain.ProfileImage;
import org.smartregister.repository.ImageRepository;
import org.smartregister.util.OpenSRPImageLoader;
import org.smartregister.vaksinator.R;
import org.smartregister.vaksinator.face.camera.ClientsList;
import org.smartregister.vaksinator.face.camera.ImageConfirmation;
import org.smartregister.vaksinator.face.camera.SmartShutterActivity;
import org.smartregister.vaksinator.vaksinator.VaksinatorDetailActivity;
import org.smartregister.view.activity.DrishtiApplication;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import util.formula.Support;


/**
 * Created by wildan on 1/4/17.
 */
public class Tools {

    private static final String TAG = Tools.class.getSimpleName();
    public static final int CONFIDENCE_VALUE = 58;
    public static org.smartregister.Context appContext;
    public static android.content.Context androContext;
    private static String[] splitStringArray;
    private static Bitmap dummyImage = null;
    private static byte[] headerOfVector;
    private static byte[] bodyOfVector;
    private static byte[] lastContentOfVector;
    //    private static String headerOne;
    private static byte[] albumVectors;
    private Bitmap helperImage = null;
    private Canvas canvas = null;
    SmartShutterActivity ss = new SmartShutterActivity();
    ClientsList cl = new ClientsList();
    private static HashMap<String, String> hash;
    private String albumBuffer;
    private List<ProfileImage> list;
    private static String anmId = Context.getInstance().allSharedPreferences().fetchRegisteredANM();
    private static ProfileImage profileImage = new ProfileImage();
    private static ImageRepository imageRepo;
//    private FaceRepository faceRepo = (FaceRepository) new FaceRepository().faceRepository();

    static String emptyAlbum = "[32, 0, 0, 0, 76, 65, -68, -20, 77, 116, 46, 83, 105, 110, 97, 105, 6, 0, 0, 0, -24, 3, 0, 0, 10, 0, 0, 0, 0, 0, 0, 0]";
    private static String headerOne = emptyAlbum;
    static String singleHeader = "[76, 1, 0, 0, 76, 65, -68, -20, 77, 116, 46, 83, 105, 110, 97, 105, 6, 0, 0, 0, -24, 3, 0, 0, 10, 0, 0, 0, 1, 0, 0, 0]";
//    FaceRepository faceRepo = (FaceRepository) faceRepository();

    public Tools() {
        Log.e(TAG, "Tools: 1");
       // imageRepo = (ImageRepository) org.smartregister.Context.imageRepository();
//        hash = retrieveHash(appContext.applicationContext());
    }

    public Tools(org.smartregister.Context appContext) {
        imageRepo = (ImageRepository) appContext.imageRepository();
        Tools.appContext = appContext;
//        helperImage = BitmapFactory.decodeResource( appContext.applicationContext().getResources(), R.drawable.h8);//ok
//        hash = retrieveHash(appContext.applicationContext());
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

//            MediaScannerConnection.scanFile(context, new String[]{
//                            pictureFile.toString()}, null,
//                    new MediaScannerConnection.OnScanCompletedListener() {
//                        public void onScanCompleted(String path, Uri uri) {
//                            Log.i("ExternalStorage", "Scanned " + path + ":");
//                            Log.i("ExternalStorage", "-> uri=" + uri);
//                        }
//                    });
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
//            saveStaticImageToDisk(entityId, ThumbImage, Arrays.toString(faceVector), updated);

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
            profileImage.setImageid(UUID.randomUUID().toString());
            profileImage.setAnmId(anmId);
            profileImage.setEntityID(entityId);
            profileImage.setContenttype("jpeg");
            profileImage.setFilepath(absoluteFileName);
            profileImage.setFilecategory("profilepic");
            //profileImage.setFilevector(faceVector);
            profileImage.setSyncStatus(ImageRepository.TYPE_Unsynced);

            imageRepo.add(profileImage
                    //, entityId
            );
        } else {
            //imageRepo.updateByEntityId(entityId, faceVector);
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
        String imgFolder = (mode == 0) ? DrishtiApplication.getAppDir() :
                DrishtiApplication.getAppDir() + File.separator + ".thumbs";
//        String imgFolder = (mode == 0) ? "OPENSRP_SID":"OPENSRP_SID"+File.separator+".thumbs";
//        File mediaStorageDir = new File(
//                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), imgFolder);
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
        return new File(String.format("%s%s%s.JPEG", mediaStorageDir.getPath(), File.separator, entityId));
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

        Log.e(TAG, "drawRectFace: rect " + rect);
        Log.e(TAG, "drawRectFace: bitmap " + mutableBitmap);
        Log.e(TAG, "drawRectFace: pixelDensity " + pixelDensity);

        // Extra padding around the faceRects
        rect.set(rect.left -= 20, rect.top -= 20, rect.right += 20, rect.bottom += 20);
        Canvas canvas = new Canvas(mutableBitmap);

        // Draw rect fill
        Paint paintForRectFill = new Paint();
        paintForRectFill.setStyle(Paint.Style.FILL);
        paintForRectFill.setColor(Color.WHITE);
        paintForRectFill.setAlpha(80);

        // Draw rect strokes
        Paint paintForRectStroke = new Paint();
        paintForRectStroke.setStyle(Paint.Style.STROKE);
        paintForRectStroke.setColor(Color.GREEN);
        paintForRectStroke.setStrokeWidth(5);

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

            albumArray = new byte[splitStringArray.length];


            for (int i = 0; i < splitStringArray.length; i++) {
                albumArray[i] = Byte.parseByte(splitStringArray[i]);
            }

            boolean result = SmartShutterActivity.faceProc.deserializeRecognitionAlbum(albumArray);

            if (result) Log.e(TAG, "loadAlbum: "+"Succes" );

        } else {
            Log.e(TAG, "loadAlbum: " + "is it your first record ? if no, there is problem happen.");
        }
    }

    public static void alertDialog(android.content.Context context, int opt) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        Tools tools = new Tools();
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
            boolean result = SmartShutterActivity.faceProc.resetAlbum();
            if (result) {
//                HashMap<String, String> hashMap = SmartShutterActivity.retrieveHash(getApplicationContext());
//                HashMap<String, String> hashMap = retrieveHash(getApplicationContext());
//                HashMap<String, String> hashMap = retrieveHash();
//                hashMap.clear();
//                SmartShutterActivity ss = new SmartShutterActivity();
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
        boolean result = SmartShutterActivity.faceProc.resetAlbum();

        if (result) {
            // Clear data
            // TODO: Null getApplication COntext
//            HashMap<String, String> hashMap = SmartShutterActivity.retrieveHash(new ClientsList().getApplicationContext());
            HashMap<String, String> hashMap = SmartShutterActivity.retrieveHash(appContext.applicationContext().getApplicationContext());
            hashMap.clear();
//            saveHash(hashMap, cl.getApplicationContext());
            saveHash(hashMap, appContext.applicationContext().getApplicationContext());
//            saveAlbum();

//            Toast.makeText(cl.getApplicationContext(), "Reset Succesfully done!", Toast.LENGTH_LONG).show();
            Toast.makeText(appContext.applicationContext().getApplicationContext(), "Reset Succesfully done!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(appContext.applicationContext().getApplicationContext(), "Reset Failed!", Toast.LENGTH_LONG).show();

        }
        Log.e(TAG, "resetAlbum: " + "finish");
    }

    /**
     * Fetch data from API (json
     */
    public static void setVectorfromAPI(final android.content.Context context) {
        Log.e(TAG, "setVectorfromAPI: " );
//        AllSharedPreferences allSharedPreferences;
        Support.ONSYNC = true;
        String DRISTHI_BASE_URL = appContext.configuration().dristhiBaseURL();
        String user = appContext.allSharedPreferences().fetchRegisteredANM();
        String location = appContext.allSharedPreferences().getPreference("locationId");
        String pwd = appContext.allSettings().fetchANMPassword();
        //TODO : cange to based locationId
//        String api_url = DRISTHI_BASE_URL + "/multimedia-file?anm-id=" + user;
        final String api_url = DRISTHI_BASE_URL + "/multimedia-file?locationid=" + location;

//        AsyncHttpClient client = new AsyncHttpClient();
//
//        client.setBasicAuth(user, pwd);
//
////        client.get(api_url, new JsonHttpResponseHandler(){
////        });
//
//        client.get(api_url, new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                Log.e(TAG, "onSuccess: " + statusCode);
//                insertOrUpdate(responseBody);
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                Log.e(TAG, "onFailure: " + api_url);
//            }
//        });
        try {
            WebUtils.fetch(api_url, user, pwd);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    static void insertOrUpdate(byte[] responseBody) {

        try {
            JSONArray response = new JSONArray(new String(responseBody));

            for (int i = 0; i < response.length(); i++) {
                JSONObject data = response.getJSONObject(i);

                String uid = data.getString("caseId");
                String anmId = data.getString("providerId");
//                        String uid = data.getString("caseId");

                // To AlbumArray
                String faceVector = data.getJSONObject("attributes").getString("faceVector");

                // Update Table ImageList on existing record based on entityId where faceVector== null
                ProfileImage profileImage = new ProfileImage();
//                profileImage.setImageid(UUID.randomUUID().toString());
                // TODO : get anmID from ?
                profileImage.setAnmId(anmId);
                profileImage.setEntityID(uid);
//                profileImage.setFilepath(null);
//                profileImage.setFilecategory("profilepic");
//                profileImage.setSyncStatus(ImageRepository.TYPE_Synced);

                // TODO : fetch vector from imagebitmap
                //profileImage.setFilevector(faceVector);

                //imageRepo.createOrUpdate(profileImage, uid);

            }
            //download_images();
            //setVectorsBuffered();

        } catch (JSONException e) {
            e.printStackTrace();
        }

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

    public static void saveAndClose(android.content.Context context, String entityId,
                                    boolean updated, FacialProcessing objFace, int arrayPossition,
                                    Bitmap storedBitmap,
                                    String className) {

        byte[] faceVector;

        if (!updated) {

//            Log.e(TAG, "saveAndClose: "+ "updated : false" );
            int result = objFace.addPerson(arrayPossition);
            faceVector = objFace.serializeRecogntionAlbum();

//            Log.e(TAG, "saveAndClose: length "+ faceVector.length ); // 32
//
//            Log.e(TAG, "saveAndClose: " + result);

            hash = retrieveHash(context);

            hash.put(entityId, Integer.toString(result));
//
            // Save Hash
            saveHash(hash, context);

//        byte[] albumBuffer = SmartShutterActivity.faceProc.serializeRecogntionAlbum();

            Log.e(TAG, "saveAndClose: " + faceVector.length);

            saveAlbum(Arrays.toString(faceVector), context);

            String albumBufferArr = Arrays.toString(faceVector);

            String[] faceVectorContent = albumBufferArr.substring(1, albumBufferArr.length() - 1).split(", ");

            // Get Face Vector Contnt Only by removing Header
            faceVectorContent = Arrays.copyOfRange(faceVectorContent, faceVector.length - 300, faceVector.length);

            WritePictureToFile(storedBitmap, entityId, faceVectorContent, updated);

            // Reset Album to get Single Face Vector
//            SmartShutterActivity.faceProc.resetAlbum();

        } else {

            int update_result = objFace.updatePerson(Integer.parseInt(hash.get(entityId)), 0);

            if (update_result == 0) {

                Log.e(TAG, "saveAndClose: " + "success");

            } else {

                Log.e(TAG, "saveAndClose: " + "Maximum Reached Limit for Face");

            }

            faceVector = objFace.serializeRecogntionAlbum();

            // TODO : update only face vector
            saveAlbum(Arrays.toString(faceVector), context);
        }

        new ImageConfirmation().finish();

        Class<?> origin_class = null;


        if(className.equals(VaksinatorDetailActivity.class.getSimpleName())){
            origin_class = VaksinatorDetailActivity.class;
        }
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

        Intent resultIntent = new Intent(appContext.applicationContext(), origin_class);
//        setResult(RESULT_OK, resultIntent);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        appContext.applicationContext().startActivity(resultIntent);

        Log.e(TAG, "saveAndClose: " + "end");
    }

//    public static void setVectorsBuffered() {
//
//        List<ProfileImage> vectorList = imageRepo.getAllVectorImages();
//        Log.e(TAG, "setVectorsBuffered: "+ vectorList.size() );
//
//        if (vectorList.size() != 0) {
//
//            hash = retrieveHash(appContext.applicationContext().getApplicationContext());
////            Log.e(TAG, "setVectorsBuffered: hash size " + hash.size());
//
//            String[] albumBuffered = new String[0];
//
//            int i = 0;
//            for (ProfileImage profileImage : vectorList) {
//                String[] vectorFace;
//                if (profileImage.getFilevector() != null) {
//
//                    vectorFace = profileImage.getFilevector().substring(1, profileImage.getFilevector().length() - 1).split(", ");
////                    vectorFace[0] = String.valueOf(i);
//                    vectorFace[0] = String.valueOf((i%128) % 256 - 128);
//
//
//                    albumBuffered = ArrayUtils.addAll(albumBuffered, vectorFace);
//                    hash.put(profileImage.getEntityID(), String.valueOf(i));
//
//                } else {
//                    Log.e(TAG, "setVectorsBuffered: Profile Image Null");
//                }
////                Log.e(TAG, "setVectorsBuffered: "+ i +" - "+ vectorFace.length);
//                i++;
//                if (i > 127) i = -128;
//
//            }
//
//            albumBuffered = ArrayUtils.addAll(getHeaderBaseUserCount(vectorList.size()), albumBuffered);
//
////            Log.e(TAG, "setVectorsBuffered: hash size" + hash.size() + " album size "+ albumBuffered.length);
//            saveAlbum(Arrays.toString(albumBuffered), appContext.applicationContext());
//            saveHash(hash, appContext.applicationContext());
//
//        } else {
//            Log.e(TAG, "setVectorsBuffered: "+ "Multimedia Table Not ready" );
//        }
//
//    }

    private static String[] getHeaderBaseUserCount(int i) {
//        String headerNew = imageRepo.findByUserCount(i);
//        return headerNew.substring(1, headerNew.length() -1).split(", ");
        Log.e(TAG, "getHeaderBaseUserCount: Number User"+ i );

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

//    public static void download_images() {
//        Log.e(TAG, "download_images: START" );
//        try {
//            List<String> images = imageRepo.findAllUnDownloaded();
//            for (String uid : images){
//                ImageView iv = new ImageView(appContext.applicationContext());
//                // TODO setTag+"The key must be an application-specific resource id"
//                iv.setTag(R.id.entity_id, uid);
//                DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(uid, OpenSRPImageLoader.getStaticImageListener(iv, 0, 0));
//                Log.e(TAG, "download_images: done "+ uid );
//
//            }
//        } catch (Exception e){
//            Log.e(TAG, "download_images: "+ e.getMessage() );
//        }
//        Log.e(TAG, "download_images: FINISHED" );
//    }

    public static void setAppContext(Context context) {
        Tools.appContext = context;
    }

    public static Context getAppContext(){
        return Tools.appContext;
    }

}
