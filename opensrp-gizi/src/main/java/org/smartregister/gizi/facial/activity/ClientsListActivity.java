package org.smartregister.gizi.facial.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.smartregister.gizi.R;
import org.smartregister.gizi.facial.utils.ClientAdapter;
import org.smartregister.gizi.facial.utils.FaceConstants;
import org.smartregister.gizi.facial.utils.Tools;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Created by wildan on 1/16/17.
 */
public class ClientsListActivity extends Activity{
    private static final String TAG = ClientsListActivity.class.getSimpleName();

    private GridView gv_clientList;
    private String[] names;
    private HashMap<String, String> hash;

    private Tools tools;

    private boolean deleteUser = false;
    private boolean updateUser = false;
    private byte[] albumBuffer;


    protected void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);

        setContentView(R.layout.activity_fr_clients);

        final OpenCameraActivity ssa = new OpenCameraActivity();
        final PhotoConfirmationActivity im = new PhotoConfirmationActivity();

        hash = OpenCameraActivity.retrieveHash(getApplicationContext());

        gv_clientList = (GridView) findViewById(R.id.gv_list);

        refreshClients();

        deleteUser = true;

        gv_clientList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // Get the username associated with the clicked cell.
                final String base_id = (String) parent.getItemAtPosition(position);

                // Delete the user
                if (deleteUser && !updateUser) {
                    new AlertDialog.Builder(ClientsListActivity.this)
                            .setMessage(
                                    "Are you sure you want to DELETE "
                                            + base_id + " from the album?")
                            .setCancelable(true)
                            .setPositiveButton("No", null)
                            .setNegativeButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog, int id) {
                                            String keyValue = hash
                                                    .get(base_id);
                                            // Getting the faceId of the registered user and converting it to integer.
                                            int faceId = Integer
                                                    .parseInt(keyValue);
                                            OpenCameraActivity.faceProc
                                                    .deletePerson(faceId); // Deleting the user from the database
                                            saveAlbum();
                                            hash.remove(base_id);

                                            photo_remove(base_id);

                                            refreshClients();

                                            im.saveHash(hash, getApplicationContext());
                                            Toast.makeText(getApplicationContext(),
                                                    base_id + " deleted successfully.",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }).show();
                } else if (updateUser && !deleteUser) // Update the user.
                {
//                    Intent intent = new Intent(ClientsListActivity.this, AddPhoto.class);
//                    intent.putExtra("Username", username);
//                    intent.putExtra("PersonId", clientList.get(username));
//                    intent.putExtra("UpdatePerson", true);
//                    startActivity(intent);
                }
            }
        });
    }
    
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.client_activity, menu);

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int i = item.getItemId();
        if (i == R.id.action_reset) {//                OpenCameraActivity ss = new OpenCameraActivity();
            resetAlbum();
//                Tools.alertDialog(ClientsListActivity.this, AppConstant.RESET_OPT);
//                Storage

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void photo_remove(String base_id) {

    }

    private void saveAlbum() {
        Log.e(TAG, "saveAlbum: "+"saving" );
        albumBuffer = OpenCameraActivity.faceProc.serializeRecogntionAlbum();
        Log.e(TAG, "saveAlbum: "+albumBuffer.toString() );
        SharedPreferences settings = getSharedPreferences(FaceConstants.ALBUM_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(FaceConstants.ALBUM_ARRAY, Arrays.toString(albumBuffer));
        editor.apply();
    }

    private void refreshClients(){
        names = new String[hash.size()];
        int i = 0;
        for (Entry<String, String> entry : hash.entrySet()
        ){
            names[i] = entry.getKey();
            i++;
        }
        gv_clientList.setAdapter(new ClientAdapter(this, names));
    }

    public void resetAlbum() {

        AlertDialog.Builder builder= new AlertDialog.Builder(this);

        builder.setTitle("Are you Sure?");
        builder.setMessage("All photos and media will lose!");
        builder.setNegativeButton("CANCEL", null);
        builder.setPositiveButton("ERASE", doEmpty);
        builder.show();
    }

    private DialogInterface.OnClickListener doEmpty = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
            boolean result = OpenCameraActivity.faceProc.resetAlbum();

//            Log.e(TAG, "onClick: "+result );
//            new Tools().resetAlbum();

            if (result) {
                HashMap<String, String> hashMap = OpenCameraActivity.retrieveHash(getApplicationContext());
                hashMap.clear();

                OpenCameraActivity ss = new OpenCameraActivity();

                // Clear List Clients
                ss.saveHash(hashMap, getApplicationContext());

                saveAlbum();

                Toast.makeText(getApplicationContext(),
                        "Album Reset Successful.",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "Internal Error. Reset album failed",
                        Toast.LENGTH_LONG).show();
            }
        }
    };
}
