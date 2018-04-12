package util.uniqueIdGenerator;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;
import org.smartregister.Context;
import org.smartregister.vaksinator.activity.LoginActivity;
import org.smartregister.vaksinator.application.VaksinatorApplication;
import org.smartregister.vaksinator.repository.VaksinatorRepository;
import org.smartregister.vaksinator.repository.UniqueIdRepository;
import org.smartregister.repository.SettingsRepository;
import org.smartregister.util.Cache;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by Null on 2016-10-13.
 */
public class Generator {
    private static final String TAG = Generator.class.getSimpleName();
    private UniqueIdRepository uniqueIdRepository;
    private Cache<List<Long>> uIdsCache;
    private AllSettingsINA allSettingsINA;
    private UniqueIdController uniqueIdController;
    private UniqueIdService uniqueIdService;
    private Context context;
    VaksinatorApplication bidanApplication;

    private String url;
    private String result;
    public static final int UNIQUE_ID_LIMIT = 5;
    public static final int UNIQUE_ID_LENGTH_REQUEST = 15;


    public Generator(Context context, String username, String password){
        this.context = context;
        // TODO
        String  DRISTHI_BASE_URL = context.configuration().dristhiBaseURL().replaceFirst("[^/]*$", "openmrs");
        url =   DRISTHI_BASE_URL+
                "/module/idgen/exportIdentifiers.form?source=1"+
                "&numberToGenerate="+Integer.toString(UNIQUE_ID_LENGTH_REQUEST)+
                "&username="+username+
                "&password="+password;
        if(bidanApplication==null){
            bidanApplication = VaksinatorApplication.getInstance();
        }
    }
    public AllSettingsINA allSettingsINA() {
        context.initRepository();
        if(allSettingsINA == null)
            // TODO: check getSettingsRepository
            allSettingsINA = new AllSettingsINA(context.allSharedPreferences(), new SettingsRepository());

        return allSettingsINA;
    }
    public Cache<List<Long>> uIdsCache() {
        if (uIdsCache == null)
            uIdsCache = new Cache<>();
        return uIdsCache;
    }
    public UniqueIdRepository uniqueIdRepository() {
        if(uniqueIdRepository==null)
            uniqueIdRepository = new UniqueIdRepository(bidanApplication.getRepository());
        return uniqueIdRepository;
    }
    public UniqueIdController uniqueIdController() {
        if(uniqueIdController == null)
            uniqueIdController = new UniqueIdController(uniqueIdRepository(), allSettingsINA(), uIdsCache());
        return uniqueIdController;
    }
    public UniqueIdService uniqueIdService() {
        if (uniqueIdService == null)
            uniqueIdService = new UniqueIdService(context.getHttpAgent(), context.configuration(), uniqueIdController(), allSettingsINA(), context.allSharedPreferences());
        return uniqueIdService;
    }

    public void requestUniqueId(){
        try {
            IdgenModuleAccessor module = new IdgenModuleAccessor();
            module.execute();
//            return new Response<>(module.getResult().equals("")?ResponseStatus.failure:ResponseStatus.success,module.getResult());
        }catch(Exception ex){
            ex.printStackTrace();
//            return new Response<>(ResponseStatus.failure,"");
        }
    }

    private String connectToOpenMRS() {
//        try {
//            StringBuilder builder = new StringBuilder();
//            HttpClient client = new DefaultHttpClient();
//            HttpGet httpGet = new HttpGet(url);
//
//            httpGet.setHeader("Content-Type", "application/json");
//            try {
//                HttpResponse response = client.execute(httpGet);
//                StatusLine statusLine = response.getStatusLine();
//                int statusCode = statusLine.getStatusCode();
//                if (statusCode == 200) {
//                    HttpEntity entity = response.getEntity();
//                    InputStream content = entity.getContent();
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        builder.append(line);
//                    }
//                    System.out.println("builder string = "+builder.toString());
//                    return builder.toString();
//                } else {
//                    Log.e("", "Failed to download file");
//                }
//            } catch (ClientProtocolException e) {
//                System.out.println("failed !!! ClientProtocolException ");
//                e.printStackTrace();
//            } catch (IOException e) {
//                System.out.println("Failed !!! IOException");
//                e.printStackTrace();
//            }
//        } catch (Exception e) {
//            Log.e("", e.getMessage());
//            System.out.println("Failed !!!, Exception");
//        }
        return "";
    }

    private class IdgenModuleAccessor extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            result = connectToOpenMRS();
//            if(result.length()>1)
//                LoginActivity.generator.uniqueIdService().saveJsonResponseToUniqueId(result);

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
