package com.university.oldenburg.mamksfzactivitytracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Entrypoint of the app
 */
public class MainActivity extends AppCompatActivity {


    EditText participantsID;
    String ID;


    List<String> filenamesForID = new ArrayList<>();
    ActivityTrackerApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        application = (ActivityTrackerApplication) getApplication();


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().clear().apply();

        participantsID = (EditText) findViewById(R.id.probandID);
        participantsID.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    ID = participantsID.getText().toString();
                    createFile();

                    return true;
                }
                return false;
            }

        });


    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_participant_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    public void createFile() {


        File root = new File(getExternalFilesDir(null) + File.separator + "ActivityData");
        if (!root.exists()) {
            if (root.mkdir()) {
                Log.i("FILE", "Directory created");
            }
        }
        File location = new File(getExternalFilesDir(null) + File.separator + "LocationData");
        if (!location.exists()) {
            if (location.mkdir()) {
                Log.i("FILE", "Directory LocationData created");
            }
        }


        String path = getExternalFilesDir(null).toString() + "/ActivityData";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        filenamesForID = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            String filename = files[i].getName();
            String[] filePrefix = filename.split("_", 2);
            if (filePrefix[0].equals(ID)) {
                filenamesForID.add(filename);

            }
        }


        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle(R.string.measurment);
        alertDialog.setMessage(R.string.new_Measurement);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String timestamp = HelperMethods.getCurrentTime();
                        application.setActivityFile(new File(getExternalFilesDir(null) + File.separator + "ActivityData", ID + "_" + timestamp + ".xml"));
                        application.setLocationFile(new File(getExternalFilesDir(null) + File.separator + "LocationData", ID + "_" + timestamp + ".xml"));
                        dialog.dismiss();
                        Intent startTracking = new Intent(MainActivity.this, ActivityTrackerActivity.class);
                        startActivity(startTracking);
                    }
                });


        alertDialog.setNegativeButton(R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd:kk:mm:ss:SSS");
                        List<Date> dateList = new ArrayList<>();
                        String usedFilename = "";
                        for (int i = 0; i < filenamesForID.size(); i++) {
                            String filename[] = filenamesForID.get(i).split("_", 2);
                            System.out.println(filename[1]);
                            filename = filename[1].split(".xml");
                            String dateTime = filename[0];
                            System.out.println(dateTime);
                            try {
                                Date date = new Date(Long.valueOf(dateTime));
                                String formattedDate = sdf.format(date);
                                System.out.println("Filedate1:"+formattedDate);
                                Date date2 = sdf.parse(formattedDate);
                                System.out.println("Filedate:"+date2);
                                dateList.add(date2);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        Date lastFile = Collections.max(dateList);
                        System.out.println(lastFile);
                        for (int i = 0; i < dateList.size(); i++) {
                            if (lastFile.equals(dateList.get(i))) {
                                usedFilename = filenamesForID.get(i);

                            }
                        }

                        application.setActivityFile(new File(getExternalFilesDir(null) + File.separator + "ActivityData", usedFilename));
                        application.setLocationFile(new File(getExternalFilesDir(null) + File.separator + "LocationData", usedFilename));
                        dialog.dismiss();
                        Intent startTracking = new Intent(MainActivity.this, ActivityTrackerActivity.class);
                        startActivity(startTracking);
                    }
                });

        if (filenamesForID.isEmpty()) {
            String timestamp = HelperMethods.getCurrentTime();
            application.setActivityFile(new File(getExternalFilesDir(null) + File.separator + "ActivityData", ID + "_" + timestamp + ".xml"));
            application.setLocationFile(new File(getExternalFilesDir(null) + File.separator + "LocationData", ID + "_" + timestamp + ".xml"));
            Intent startTracking = new Intent(this, ActivityTrackerActivity.class);
            startTracking.putExtra("newFile",true);
            startActivity(startTracking);
        } else {
            AlertDialog alert = alertDialog.create();
            alert.show();
        }


    }

    public void start(View view) {
        ActivityTrackerActivity.activityStart = "";
        ActivityTrackerActivity.activityStop = "";
        ID = participantsID.getText().toString();
        createFile();


    }

}
