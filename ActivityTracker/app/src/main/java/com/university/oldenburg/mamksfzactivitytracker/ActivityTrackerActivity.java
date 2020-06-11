package com.university.oldenburg.mamksfzactivitytracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ActivityTrackerActivity extends AppCompatActivity {

    static int currentActivity;
    static String activityStart;
    static String activityStop;
    static String locationStart;
    static String locationStop;
    static int[] duration = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    static int[] counter = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    Button walking;
    Button turning;
    Button stairs;
    Button crouching;
    Button walking_backwards;
    Button jumping;
    Button stairs_down;
    Button sitting;
    Button lying;
    Button lying_side;
    Button standing;
    Button stand_up;
    Button sit_down;
    Button lie_sit;
    Button sit_lie;
    Button other;
    Button home;
    Button outside;
    Button inside;
    ArrayList<Button> buttonList;
    ArrayList<Integer> drawableList;
    ArrayList<String> activity_names;
    LayerDrawable layers;
    GradientDrawable shape;
    String activity_name = "";
    String timestamp;
    SharedPreferences preferences;
    boolean endAlreadyWritten;
    String filename;
    String locationFilename;
    ActivityTrackerApplication application;

    int activeLocation = 0;
    private ActivityXmlWriter activityXmlWriter;
    private LocationXmlWriter locationXmlWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_tracker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        duration = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        counter = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        application = (ActivityTrackerApplication) getApplication();




        walking = (Button) findViewById(R.id.button_walking);
        turning = (Button) findViewById(R.id.button_turning);
        stairs = (Button) findViewById(R.id.button_stairs);
        crouching = (Button) findViewById(R.id.button_crouching);
        walking_backwards = (Button) findViewById(R.id.button_walking_backwards);
        jumping = (Button) findViewById(R.id.button_jumping);
        stairs_down = (Button) findViewById(R.id.button_stairs_down);
        sitting = (Button) findViewById(R.id.button_sitting);
        lying = (Button) findViewById(R.id.button_lying);
        lying_side = (Button) findViewById(R.id.button_lying_side);
        standing = (Button) findViewById(R.id.button_standing);
        stand_up = (Button) findViewById(R.id.button_stand_up);
        sit_down = (Button) findViewById(R.id.button_sit_down);
        sit_lie = (Button) findViewById(R.id.button_sit_lie);
        lie_sit = (Button) findViewById(R.id.button_lie_sit);
        other = (Button) findViewById(R.id.button_unknown);
        home = (Button) findViewById(R.id.button_home);
        outside = (Button) findViewById(R.id.button_outside);
        inside = (Button) findViewById(R.id.button_inside);


        updateTexts();

        buttonList = new ArrayList<Button>();
        buttonList.add(walking);
        buttonList.add(turning);
        buttonList.add(stairs);
        buttonList.add(crouching);
        buttonList.add(walking_backwards);
        buttonList.add(jumping);
        buttonList.add(stairs_down);
        buttonList.add(other);
        buttonList.add(standing);
        buttonList.add(sitting);
        buttonList.add(lying);
        buttonList.add(lying_side);
        buttonList.add(stand_up);
        buttonList.add(sit_down);
        buttonList.add(lie_sit);
        buttonList.add(sit_lie);


        drawableList = new ArrayList<>();
        drawableList.add(R.drawable.button_walking);
        drawableList.add(R.drawable.button_turning);
        drawableList.add(R.drawable.button_stairs);
        drawableList.add(R.drawable.button_crouching);
        drawableList.add(R.drawable.button_walking_backwards);
        drawableList.add(R.drawable.button_jumping);
        drawableList.add(R.drawable.button_stairs_down);
        drawableList.add(R.drawable.button_other);
        drawableList.add(R.drawable.button_standing);
        drawableList.add(R.drawable.button_sitting);
        drawableList.add(R.drawable.button_lying);
        drawableList.add(R.drawable.button_lying_side);
        drawableList.add(R.drawable.button_stand_up);
        drawableList.add(R.drawable.button_sit_down);
        drawableList.add(R.drawable.button_lie_sit);
        drawableList.add(R.drawable.button_sit_lie);


        activity_names = new ArrayList<>();
        activity_names.add("WALK");
        activity_names.add("TURN_AROUND");
        activity_names.add("STAIR_CLIMB");
        activity_names.add("BEND_OVER_OR_SQUAT");
        activity_names.add("WALK_BACKWARDS");
        activity_names.add("JUMP");
        activity_names.add("STAIR_CLIMB_DOWN");
        activity_names.add("UNKNOWN");
        activity_names.add("STAND");
        activity_names.add("SIT");
        activity_names.add("LIE_ON_BACK");
        activity_names.add("LIE_ON_SIDE");
        activity_names.add("SIT_STAND");
        activity_names.add("STAND_SIT");
        activity_names.add("LIE_SIT");
        activity_names.add("SIT_LIE");

        Intent intent = getIntent();
        if (intent.getBooleanExtra("newFile",false)){
            activityXmlWriter = ActivityXmlWriter.getInstance(application.getActivityFile());
            locationXmlWriter = LocationXmlWriter.getInstance(application.getLocationFile());
            String startTime = String.valueOf(System.currentTimeMillis());
            activityXmlWriter.writeLabel("START",startTime, startTime);
            locationXmlWriter.writeLocation("START",startTime,startTime);
        }


    }


    @Override
    public void onStart() {
        super.onStart();



        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        activityStart = preferences.getString("activityStart", "");
        currentActivity = preferences.getInt("currentActivity", 0);
        activity_name = preferences.getString("activityName", "");
        filename = preferences.getString("filename", "");
        locationStart = preferences.getString("locationStart", "");
        activeLocation = preferences.getInt("currentLocation", 0);
        locationFilename = preferences.getString("locationFilename", "");


        if (!application.getActivityFile().getName().equals(filename)) {
            if (currentActivity > 0) {
                changeButtonColorRed();
            }
            currentActivity = 0;
            activityStart = "";
        }

        if (currentActivity > 0) {
            changeButtonColorGreen();
        }

        if (!application.getLocationFile().getName().equals(locationFilename)) {
            if (activeLocation > 0) {
                inside.setBackgroundResource(android.R.drawable.btn_default);
                outside.setBackgroundResource(android.R.drawable.btn_default);
                home.setBackgroundResource(android.R.drawable.btn_default);
            }
            activeLocation = 0;
            locationStart = "";
        }

        if (activeLocation > 0) {
            if (activeLocation == 1) {
                inside.setBackgroundResource(android.R.drawable.btn_default);
                outside.setBackgroundResource(android.R.drawable.btn_default);
                home.setBackgroundResource(R.color.pressed_list_item);
            } else if (activeLocation == 2) {
                inside.setBackgroundResource(android.R.drawable.btn_default);
                home.setBackgroundResource(android.R.drawable.btn_default);
                outside.setBackgroundResource(R.color.pressed_list_item);
            } else if (activeLocation == 3) {
                home.setBackgroundResource(android.R.drawable.btn_default);
                outside.setBackgroundResource(android.R.drawable.btn_default);
                inside.setBackgroundResource(R.color.pressed_list_item);
            }
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("onStop wird aufgerufen");
        preferences.edit().putString("filename", application.getActivityFile().getName()).apply();
        preferences.edit().putString("activityStart", activityStart).apply();
        preferences.edit().putString("activityName", activity_name).apply();
        preferences.edit().putInt("currentActivity", currentActivity).apply();
        preferences.edit().putString("locationFilename", application.getLocationFile().getName()).apply();
        preferences.edit().putString("locationStart", locationStart).apply();
        preferences.edit().putInt("currentLocation", activeLocation).apply();


        if (!endAlreadyWritten) {
            activityXmlWriter.closeActivityXmlWriter();
            locationXmlWriter.closeLocationXmlWriter();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        preferences.edit().putString("filename",application.getActivityFile().getName()).apply();
        preferences.edit().putString("activityStart", activityStart).apply();
        preferences.edit().putInt("currentActivity", currentActivity).apply();
        preferences.edit().putString("activityName", activity_name).apply();
        preferences.edit().putString("locationFilename", application.getLocationFile().getName()).apply();
        preferences.edit().putString("locationStart", locationStart).apply();
        preferences.edit().putInt("currentLocation", activeLocation).apply();

        locationXmlWriter.closeLocationXmlWriter();
        activityXmlWriter.closeActivityXmlWriter();

        endAlreadyWritten = true;
    }

    @Override
    public void onResume(){
        super.onResume();
        endAlreadyWritten = false;
        duration = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        counter = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        readFileForActivityCount();
        updateTexts();
        activityXmlWriter = ActivityXmlWriter.getInstance(application.getActivityFile());
        locationXmlWriter = LocationXmlWriter.getInstance(application.getLocationFile());
    }
    public void updateTexts() {


        String activity0 = getString(R.string.walking, counter[0], duration[0] / 1000 / 60);
        String activity1 = getString(R.string.turning, counter[1], duration[1] / 1000 / 60);
        String activity2 = getString(R.string.stairs, counter[2], duration[2] / 1000 / 60);
        String activity3 = getString(R.string.crouching, counter[3], duration[3] / 1000 / 60);
        String activity4 = getString(R.string.walking_backwards, counter[4], duration[4] / 1000 / 60);
        String activity5 = getString(R.string.jumping, counter[5], duration[5] / 1000 / 60);
        String activity6 = getString(R.string.stairs_down, counter[6], duration[6] / 1000 / 60);
        String activity7 = getString(R.string.other, counter[7], duration[7] / 1000 / 60);
        String activity8 = getString(R.string.standing, counter[8], duration[8] / 1000 / 60);
        String activity9 = getString(R.string.sitting, counter[9], duration[9] / 1000 / 60);
        String activity10 = getString(R.string.lying, counter[10], duration[10] / 1000 / 60);
        String activity11 = getString(R.string.lying_side, counter[11], duration[11] / 1000 / 60);
        String activity12 = getString(R.string.stand_up, counter[12], duration[12] / 1000 / 60);
        String activity13 = getString(R.string.sit_down, counter[13], duration[13] / 1000 / 60);
        String activity14 = getString(R.string.lie_sit, counter[14], duration[14] / 1000 / 60);
        String activity15 = getString(R.string.sit_lie, counter[15], duration[15] / 1000 / 60);

        walking.setText(activity0);
        turning.setText(activity1);
        stairs.setText(activity2);
        crouching.setText(activity3);
        walking_backwards.setText(activity4);
        jumping.setText(activity5);
        stairs_down.setText(activity6);
        other.setText(activity7);
        standing.setText(activity8);
        sitting.setText(activity9);
        lying.setText(activity10);
        lying_side.setText(activity11);
        stand_up.setText(activity12);
        sit_down.setText(activity13);
        lie_sit.setText(activity14);
        sit_lie.setText(activity15);
    }

    public void readFileForActivityCount() {
        File file = application.getActivityFile();
        String currentLine;

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            while ((currentLine = br.readLine()) != null) {

                if (currentLine.equals("<label>")) {
                    br.readLine();
                    String start = br.readLine();
                    String end = br.readLine();
                    br.readLine();
                    String activity = br.readLine();
                    String[] startArray;
                    String[] endArray;
                    String[] activityArray;
                    startArray = start.split(">", 2);
                    startArray = startArray[1].split("<", 2);
                    start = startArray[0];
                    endArray = end.split(">", 2);
                    endArray = endArray[1].split("<", 2);
                    end = endArray[0];
                    System.out.println("start: " + start);
                    System.out.println("ende: " + end);
                    System.out.println(activity);
                    activityArray = activity.split(">", 2);
                    activityArray = activityArray[1].split("<", 2);
                    activity = activityArray[0];
                    long durationInMillis = Long.valueOf(end) - Long.valueOf(start);
                    int activityNumber = 0;
                    switch (activity) {
                        case "START":
                            break;
                        case "WALK":
                            activityNumber = 1;
                            break;
                        case "TURN_AROUND":
                            activityNumber = 2;
                            break;
                        case "STAIR_CLIMB":
                            activityNumber = 3;
                            break;
                        case "BEND_OVER_OR_SQUAT":
                            activityNumber = 4;
                            break;
                        case "WALK_BACKWARDS":
                            activityNumber = 5;
                            break;
                        case "JUMP":
                            activityNumber = 6;
                            break;
                        case "STAIR_CLIMB_DOWN":
                            activityNumber = 7;
                            break;
                        case "UNKNOWN":
                            activityNumber = 8;
                            break;
                        case "SIT":
                            activityNumber = 10;
                            break;
                        case "STAND":
                            activityNumber = 9;
                            break;
                        case "LIE_ON_BACK":
                            activityNumber = 11;
                            break;
                        case "LIE_ON_SIDE":
                            activityNumber = 12;
                            break;
                        case "SIT_STAND":
                            activityNumber = 13;
                            break;
                        case "STAND_SIT":
                            activityNumber = 14;
                            break;
                        case "LIE_SIT":
                            activityNumber = 15;
                            break;
                        case "SIT_LIE":
                            activityNumber = 16;
                            break;
                        default:
                            activityNumber = 8;
                            break;
                    }
                    if (activityNumber > 0) {
                        duration[activityNumber - 1] += durationInMillis;
                        counter[activityNumber - 1] += 1;
                    }
                }

            }
        } catch (IOException a) {
            a.printStackTrace();
        }
    }


    public void delete_last_entry(View view) {

        Intent delete_entry = new Intent(this, DeleteActivity.class);
        startActivity(delete_entry);


    }

    public void walking(View view) {

        timestamp = HelperMethods.getCurrentTime();
        System.out.println(timestamp);
        System.out.println(currentActivity);
        if (currentActivity == 0) {
            currentActivity = 1;
            activityStart = timestamp;
            changeButtonColorGreen();

        } else if (currentActivity == 1) {
            changeButtonColorRed();
            activityStop = timestamp;
            writeActivityDataToFile(false);
            activityStart = "";
            activityStop = "";
            currentActivity = 0;

        } else {
            changeButtonColorRed();
            activityStop = timestamp;
            writeActivityDataToFile(false);
            activityStart = "";
            activityStop = "";
            currentActivity = 1;
            changeButtonColorGreen();
            activityStart = timestamp;
        }
    }

    public void stairs(View view) {
        timestamp = HelperMethods.getCurrentTime();
        System.out.println(timestamp);
        System.out.println(currentActivity);
        if (currentActivity == 0) {
            currentActivity = 3;
            activityStart = timestamp;
            changeButtonColorGreen();

        } else if (currentActivity == 3) {
            changeButtonColorRed();
            activityStop = timestamp;
            writeActivityDataToFile(false);
            activityStart = "";
            activityStop = "";
            currentActivity = 0;

        } else {
            changeButtonColorRed();
            activityStop = timestamp;
            writeActivityDataToFile(false);
            activityStart = "";
            activityStop = "";
            currentActivity = 3;
            changeButtonColorGreen();
            activityStart = timestamp;

        }
    }

    public void jumping(View view) {
        timestamp = HelperMethods.getCurrentTime();
        System.out.println(timestamp);
        System.out.println(currentActivity);
        if (currentActivity == 0) {
            currentActivity = 6;
            activityStart = timestamp;
            changeButtonColorGreen();

        } else if (currentActivity == 6) {
            changeButtonColorRed();
            activityStop = timestamp;
            writeActivityDataToFile(false);
            activityStart = "";
            activityStop = "";
            currentActivity = 0;
        } else {
            changeButtonColorRed();
            activityStop = timestamp;
            writeActivityDataToFile(false);
            activityStart = "";
            activityStop = "";
            currentActivity = 6;
            changeButtonColorGreen();
            activityStart = timestamp;
        }
    }

    public void sitting(View view) {
        timestamp = HelperMethods.getCurrentTime();
        System.out.println(timestamp);
        System.out.println(currentActivity);
        if (currentActivity == 0) {
            currentActivity = 10;
            activityStart = timestamp;
            changeButtonColorGreen();

        } else if (currentActivity == 10) {
            changeButtonColorRed();
            activityStop = timestamp;
            writeActivityDataToFile(false);
            activityStart = "";
            activityStop = "";
            currentActivity = 0;
        } else {
            changeButtonColorRed();
            activityStop = timestamp;
            writeActivityDataToFile(false);
            activityStart = "";
            activityStop = "";
            currentActivity = 10;
            changeButtonColorGreen();
            activityStart = timestamp;
        }
    }

    public void standing(View view) {
        timestamp = HelperMethods.getCurrentTime();
        System.out.println(timestamp);
        System.out.println(currentActivity);
        if (currentActivity == 0) {
            currentActivity = 9;
            activityStart = timestamp;
            changeButtonColorGreen();

        } else if (currentActivity == 9) {
            changeButtonColorRed();
            activityStop = timestamp;
            writeActivityDataToFile(false);
            activityStart = "";
            activityStop = "";
            currentActivity = 0;
        } else {
            changeButtonColorRed();
            activityStop = timestamp;
            writeActivityDataToFile(false);
            activityStart = "";
            activityStop = "";
            currentActivity = 9;
            changeButtonColorGreen();
            activityStart = timestamp;
        }
    }

    public void lying(View view) {
        timestamp = HelperMethods.getCurrentTime();
        System.out.println(timestamp);
        System.out.println(currentActivity);
        if (currentActivity == 0) {
            currentActivity = 11;
            activityStart = timestamp;
            changeButtonColorGreen();

        } else if (currentActivity == 11) {
            changeButtonColorRed();
            activityStop = timestamp;
            writeActivityDataToFile(false);
            activityStart = "";
            activityStop = "";
            currentActivity = 0;
        } else {
            changeButtonColorRed();
            activityStop = timestamp;
            writeActivityDataToFile(false);
            activityStart = "";
            activityStop = "";
            currentActivity = 11;
            changeButtonColorGreen();
            activityStart = timestamp;
        }
    }

    public void standup(View view) {
        timestamp = HelperMethods.getCurrentTime();
        System.out.println(timestamp);
        System.out.println(currentActivity);
        if (currentActivity == 0) {
            currentActivity = 13;
            activityStart = timestamp;
            changeButtonColorGreen();

        } else if (currentActivity == 13) {
            changeButtonColorRed();
            activityStop = timestamp;
            writeActivityDataToFile(false);
            activityStart = "";
            activityStop = "";
            currentActivity = 0;
        } else {
            changeButtonColorRed();
            activityStop = timestamp;
            writeActivityDataToFile(false);
            activityStart = "";
            activityStop = "";
            currentActivity = 13;
            changeButtonColorGreen();
            activityStart = timestamp;
        }
    }

    public void sitdown(View view) {
        timestamp = HelperMethods.getCurrentTime();
        System.out.println(timestamp);
        System.out.println(currentActivity);
        if (currentActivity == 0) {
            currentActivity = 14;
            activityStart = timestamp;
            changeButtonColorGreen();

        } else if (currentActivity == 14) {
            changeButtonColorRed();
            activityStop = timestamp;
            writeActivityDataToFile(false);
            activityStart = "";
            activityStop = "";
            currentActivity = 0;
        } else {
            changeButtonColorRed();
            activityStop = timestamp;
            writeActivityDataToFile(false);
            activityStart = "";
            activityStop = "";
            currentActivity = 14;
            changeButtonColorGreen();
            activityStart = timestamp;
        }
    }

    public void other(View view) {

        timestamp = HelperMethods.getCurrentTime();
        System.out.println(timestamp);
        System.out.println(currentActivity);
        if (currentActivity == 0) {
            currentActivity = 8;
            activityStart = timestamp;
            changeButtonColorGreen();
            create_dialog();

        } else if (currentActivity == 8) {
            changeButtonColorRed();
            activityStop = timestamp;
            Log.i("activity_tracker", "other:  writeActivity");
            writeActivityDataToFile(false);
            activityStart = "";
            activityStop = "";
            currentActivity = 0;
        } else {
            changeButtonColorRed();
            activityStop = timestamp;
            writeActivityDataToFile(false);
            activityStart = "";
            activityStop = "";
            currentActivity = 8;
            changeButtonColorGreen();
            activityStart = timestamp;
            create_dialog();
        }
    }

    public void turning(View view) {
        timestamp = HelperMethods.getCurrentTime();
        System.out.println(timestamp);
        System.out.println(currentActivity);
        if (currentActivity == 0) {
            currentActivity = 2;
            activityStart = timestamp;
            changeButtonColorGreen();

        } else if (currentActivity == 2) {
            changeButtonColorRed();
            activityStop = timestamp;
            writeActivityDataToFile(false);
            activityStart = "";
            activityStop = "";
            currentActivity = 0;
        } else {
            changeButtonColorRed();
            activityStop = timestamp;
            writeActivityDataToFile(false);
            activityStart = "";
            activityStop = "";
            currentActivity = 2;
            changeButtonColorGreen();
            activityStart = timestamp;
        }

    }

    public void crouching(View view) {
        timestamp = HelperMethods.getCurrentTime();
        System.out.println(timestamp);
        System.out.println(currentActivity);
        if (currentActivity == 0) {
            currentActivity = 4;
            activityStart = timestamp;
            changeButtonColorGreen();

        } else if (currentActivity == 4) {
            changeButtonColorRed();
            activityStop = timestamp;
            writeActivityDataToFile(false);
            activityStart = "";
            activityStop = "";
            currentActivity = 0;
        } else {
            changeButtonColorRed();
            activityStop = timestamp;
            writeActivityDataToFile(false);
            activityStart = "";
            activityStop = "";
            currentActivity = 4;
            changeButtonColorGreen();
            activityStart = timestamp;
        }
    }

    public void lying_side(View view) {
        timestamp = HelperMethods.getCurrentTime();
        System.out.println(timestamp);
        System.out.println(currentActivity);
        if (currentActivity == 0) {
            currentActivity = 12;
            activityStart = timestamp;
            changeButtonColorGreen();

        } else if (currentActivity == 12) {
            changeButtonColorRed();
            activityStop = timestamp;
            writeActivityDataToFile(false);
            activityStart = "";
            activityStop = "";
            currentActivity = 0;
        } else {
            changeButtonColorRed();
            activityStop = timestamp;
            writeActivityDataToFile(false);
            activityStart = "";
            activityStop = "";
            currentActivity = 12;
            changeButtonColorGreen();
            activityStart = timestamp;
        }
    }

    public void stairs_down(View view) {
        timestamp = HelperMethods.getCurrentTime();
        System.out.println(timestamp);
        System.out.println(currentActivity);
        if (currentActivity == 0) {
            currentActivity = 7;
            activityStart = timestamp;
            changeButtonColorGreen();

        } else if (currentActivity == 7) {
            changeButtonColorRed();
            activityStop = timestamp;
            writeActivityDataToFile(false);
            activityStart = "";
            activityStop = "";
            currentActivity = 0;
        } else {
            changeButtonColorRed();
            activityStop = timestamp;
            writeActivityDataToFile(false);
            activityStart = "";
            activityStop = "";
            currentActivity = 7;
            changeButtonColorGreen();
            activityStart = timestamp;
        }
    }

    public void lie_sit(View view) {
        timestamp = HelperMethods.getCurrentTime();
        System.out.println(timestamp);
        System.out.println(currentActivity);
        if (currentActivity == 0) {
            currentActivity = 15;
            activityStart = timestamp;
            changeButtonColorGreen();

        } else if (currentActivity == 15) {
            changeButtonColorRed();
            activityStop = timestamp;
            writeActivityDataToFile(false);
            activityStart = "";
            activityStop = "";
            currentActivity = 0;
        } else {
            changeButtonColorRed();
            activityStop = timestamp;
            writeActivityDataToFile(false);
            activityStart = "";
            activityStop = "";
            currentActivity = 15;
            changeButtonColorGreen();
            activityStart = timestamp;
        }
    }

    public void sit_lie(View view) {
        timestamp = HelperMethods.getCurrentTime();
        System.out.println(timestamp);
        System.out.println(currentActivity);
        if (currentActivity == 0) {
            currentActivity = 16;
            activityStart = timestamp;
            changeButtonColorGreen();

        } else if (currentActivity == 16) {
            changeButtonColorRed();
            activityStop = timestamp;
            writeActivityDataToFile(false);
            activityStart = "";
            activityStop = "";
            currentActivity = 0;
        } else {
            changeButtonColorRed();
            activityStop = timestamp;
            writeActivityDataToFile(false);
            activityStart = "";
            activityStop = "";
            currentActivity = 16;
            changeButtonColorGreen();
            activityStart = timestamp;
        }
    }

    public void walking_backwards(View view) {
        timestamp = HelperMethods.getCurrentTime();
        System.out.println(timestamp);
        System.out.println(currentActivity);
        if (currentActivity == 0) {
            currentActivity = 5;
            activityStart = timestamp;
            changeButtonColorGreen();

        } else if (currentActivity == 5) {
            changeButtonColorRed();
            activityStop = timestamp;
            writeActivityDataToFile(false);
            activityStart = "";
            activityStop = "";
            currentActivity = 0;
        } else {
            changeButtonColorRed();
            activityStop = timestamp;
            writeActivityDataToFile(false);
            activityStart = "";
            activityStop = "";
            currentActivity = 5;
            changeButtonColorGreen();
            activityStart = timestamp;
        }
    }


    public void stop(View view) {
        timestamp = HelperMethods.getCurrentTime();
        if (currentActivity > 0) {
            changeButtonColorRed();
            activityStop = timestamp;
            writeActivityDataToFile(false);
            activityStart = "";
            activityStop = "";
            currentActivity = 0;
        }
        if (activeLocation > 0) {
            inside.setBackgroundResource(android.R.drawable.btn_default);
            outside.setBackgroundResource(android.R.drawable.btn_default);
            home.setBackgroundResource(android.R.drawable.btn_default);
            locationStop = timestamp;
            writeActivityDataToFile(true);
            locationStart = "";
            locationStop = "";
            activeLocation = 0;
        }
        activityXmlWriter.closeActivityXmlWriter();
        locationXmlWriter.closeLocationXmlWriter();




        endAlreadyWritten = true;
        Intent backToStart = new Intent(this, MainActivity.class);
        startActivity(backToStart);
    }


    /**
     * Writes the data of the finished activity to XML file
     */

    public void writeActivityDataToFile(boolean locationFile) {



        if (!locationFile) {
            if (activity_name.equals("")) {
                activity_name = activity_names.get(currentActivity - 1);
            }

            activityXmlWriter.writeLabel(activity_name, activityStart, activityStop);
            long durationInMinutes = (Long.valueOf(activityStop) - Long.valueOf(activityStart)) / 60000;
            if (currentActivity > 0) {
                duration[currentActivity - 1] += durationInMinutes;
                counter[currentActivity - 1] += 1;
            }

            updateTexts();
        } else {
            if (activity_name.equals("")) {

                if (activeLocation == 1) {
                    activity_name = "HOME";
                } else if (activeLocation == 2) {
                    activity_name = "OUTSIDE";
                } else if (activeLocation == 3) {
                    activity_name = "INSIDE";
                }
            }


            locationXmlWriter.writeLocation(activity_name, locationStart, locationStop);


        }

        activity_name="";

    }

    /**
     * Changes the color of the button to red
     */
    public void changeButtonColorRed() {
        layers = (LayerDrawable) ContextCompat.getDrawable(this, drawableList.get(currentActivity - 1));
        shape = (GradientDrawable) (layers.findDrawableByLayerId(R.id.shape_walking));
        shape.setColor(ContextCompat.getColor(this, R.color.red_Background));
        buttonList.get(currentActivity - 1).setBackgroundResource(0);
        buttonList.get(currentActivity - 1).setBackgroundResource(drawableList.get(currentActivity - 1));

    }

    /**
     * Changes color of the button to green
     */
    public void changeButtonColorGreen() {
        layers = (LayerDrawable) ContextCompat.getDrawable(this, drawableList.get(currentActivity - 1));
        shape = (GradientDrawable) (layers.findDrawableByLayerId(R.id.shape_walking));
        shape.setColor(ContextCompat.getColor(this, R.color.green_Background));
        buttonList.get(currentActivity - 1).setBackgroundResource(0);
        buttonList.get(currentActivity - 1).setBackgroundResource(drawableList.get(currentActivity - 1));
    }


    /**
     * Creates a dialog to choose an activity name for category other
     */

    public void create_dialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ActivityTrackerActivity.this);
        alertDialog.setTitle(R.string.activity);
        alertDialog.setMessage(R.string.name_activity);

        alertDialog.setCancelable(false);
        final EditText input = new EditText(ActivityTrackerActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);


        alertDialog.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });


        alertDialog.setNegativeButton(R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        activity_name = "";
                        dialog.dismiss();
                    }
                });


        //alertDialog.show();
        final AlertDialog dialog = alertDialog.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity_name = input.getText().toString();
                if (activity_name.matches("")) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_activity_given), Toast.LENGTH_LONG).show();
                } else {
                    dialog.dismiss();
                }

            }
        });
    }

    public void home(View view) {
        timestamp = HelperMethods.getCurrentTime();
        System.out.println(timestamp);
        if (activeLocation == 0) {
            activeLocation = 1;
            locationStart = timestamp;
            home.setBackgroundResource(R.color.pressed_list_item);
            outside.setBackgroundResource(android.R.drawable.btn_default);
            inside.setBackgroundResource(android.R.drawable.btn_default);
        } else if (activeLocation == 1) {
            locationStop = timestamp;
            writeActivityDataToFile(true);
            locationStart = "";
            locationStop = "";
            activeLocation = 0;
            home.setBackgroundResource(android.R.drawable.btn_default);
        } else {
            locationStop = timestamp;
            writeActivityDataToFile(true);
            locationStart = "";
            locationStop = "";
            activeLocation = 1;
            locationStart = timestamp;
            home.setBackgroundResource(R.color.pressed_list_item);
            outside.setBackgroundResource(android.R.drawable.btn_default);
            inside.setBackgroundResource(android.R.drawable.btn_default);
        }

    }

    public void outside(View view) {
        timestamp = HelperMethods.getCurrentTime();
        System.out.println(timestamp);
        if (activeLocation == 0) {
            activeLocation = 2;
            locationStart = timestamp;
            home.setBackgroundResource(android.R.drawable.btn_default);
            outside.setBackgroundResource(R.color.pressed_list_item);
            inside.setBackgroundResource(android.R.drawable.btn_default);
        } else if (activeLocation == 2) {
            locationStop = timestamp;
            writeActivityDataToFile(true);
            locationStart = "";
            locationStop = "";
            activeLocation = 0;
            outside.setBackgroundResource(android.R.drawable.btn_default);
        } else {
            locationStop = timestamp;
            writeActivityDataToFile(true);
            locationStart = "";
            locationStop = "";
            activeLocation = 2;
            locationStart = timestamp;
            home.setBackgroundResource(android.R.drawable.btn_default);
            outside.setBackgroundResource(R.color.pressed_list_item);
            inside.setBackgroundResource(android.R.drawable.btn_default);
        }
    }

    public void inside(View view) {
        timestamp = HelperMethods.getCurrentTime();
        System.out.println(timestamp);
        if (activeLocation == 0) {
            activeLocation = 3;
            locationStart = timestamp;
            inside.setBackgroundResource(R.color.pressed_list_item);
            outside.setBackgroundResource(android.R.drawable.btn_default);
            home.setBackgroundResource(android.R.drawable.btn_default);
        } else if (activeLocation == 3) {
            locationStop = timestamp;
            writeActivityDataToFile(true);
            locationStart = "";
            locationStop = "";
            activeLocation = 0;
            inside.setBackgroundResource(android.R.drawable.btn_default);
        } else {
            locationStop = timestamp;
            writeActivityDataToFile(true);
            locationStart = "";
            locationStop = "";
            activeLocation = 3;
            locationStart = timestamp;
            inside.setBackgroundResource(R.color.pressed_list_item);
            outside.setBackgroundResource(android.R.drawable.btn_default);
            home.setBackgroundResource(android.R.drawable.btn_default);
        }
    }

}