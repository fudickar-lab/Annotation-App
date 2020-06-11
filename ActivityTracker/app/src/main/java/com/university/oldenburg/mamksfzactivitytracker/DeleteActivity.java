package com.university.oldenburg.mamksfzactivitytracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


public class DeleteActivity extends AppCompatActivity {

    ListView activityList;
    ArrayList<String> array_list_activities;
    static ArrayList<Integer> checkedActivityNumbers;
    Button deleteButton;
    RelativeLayout loadingPanel;
    RelativeLayout deleteView;
    ArrayAdapter activityListArrayAdapter=null;
    ActivityTrackerApplication application;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_activities);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        application = (ActivityTrackerApplication) getApplication();
        array_list_activities=new ArrayList<>();
        checkedActivityNumbers=new ArrayList<>();
        activityList=(ListView) findViewById(R.id.activityList);
        deleteButton=(Button) findViewById(R.id.deleteButton);
        loadingPanel=(RelativeLayout) findViewById(R.id.loadingPanel);
        deleteView=(RelativeLayout) findViewById(R.id.deleteView);
        loadingPanel.setVisibility(View.GONE);
        createActivityListItems();
        activityListArrayAdapter=new TextViewAdapter(this,R.layout.custom_list_view, R.id.text1, array_list_activities);
        activityList.setAdapter(activityListArrayAdapter);
        activityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(checkedActivityNumbers.contains((int)id)){
                    for(int i=0;i<checkedActivityNumbers.size();i++){
                        if(checkedActivityNumbers.get(i)==(int)id){
                            System.out.println(checkedActivityNumbers.get(i));
                            checkedActivityNumbers.remove(i);

                        }
                    }
                    activityListArrayAdapter.notifyDataSetChanged();
                }else {
                    System.out.println(position);
                    checkedActivityNumbers.add((int)id);
                    activityListArrayAdapter.notifyDataSetChanged();

                }

            }
        });
        scrollMyListViewToBottom();



    }

    public void createActivityListItems(){
        File file= application.getActivityFile();

        String currentLine;

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            while ((currentLine = br.readLine()) != null) {

                if(currentLine.equals("<label>")){
                    br.readLine();
                    String start=br.readLine();
                    String end=br.readLine();
                    br.readLine();
                    String activity=br.readLine();
                    br.readLine();
                    String [] startArray;
                    String[] endArray;
                    String[] activityArray;
                    startArray=start.split(">",2);
                    startArray=startArray[1].split("<",2);
                    start=startArray[0];
                    endArray=end.split(">",2);
                    endArray=endArray[1].split("<",2);
                    end=endArray[0];
                    activityArray=activity.split(">",2);
                    activityArray=activityArray[1].split("<",2);
                    activity=activityArray[0];
                    String activityString=activity+" "+start+" "+end;
                    array_list_activities.add(activityString);
                }

            }
        }catch (IOException a){
            a.printStackTrace();
        }
    }

    public void back(View view){
        Intent back=new Intent(this, ActivityTrackerActivity.class);
        startActivity(back);
    }

    public void deletingItems(){

        Collections.sort(checkedActivityNumbers);
        for (int i = checkedActivityNumbers.size(); i > 0; i--) {
            try {
                ActivityXmlWriter.removeLabel(application.getActivityFile(), checkedActivityNumbers.get(i - 1));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Intent back = new Intent(this, ActivityTrackerActivity.class);
        startActivity(back);


    }

    public void delete(View view){
        deleteButton.setEnabled(false);
        deleteButton.setBackgroundResource(R.color.pressed_list_item);
        loadingPanel.setVisibility(View.VISIBLE);
        deleteView.invalidate();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                deletingItems();
            }
        }, 1000);


    }

    private void scrollMyListViewToBottom() {
        activityList.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                activityList.setSelection(activityListArrayAdapter.getCount() - 1);
            }
        });
    }


}
