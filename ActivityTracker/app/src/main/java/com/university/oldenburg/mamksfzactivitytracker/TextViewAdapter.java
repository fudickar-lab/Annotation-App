package com.university.oldenburg.mamksfzactivitytracker;

/**
 * Created by maren on 15.07.17.
 */

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maren on 06.01.17.
 */
class TextViewAdapter extends ArrayAdapter {

    private Context context;
    private int resource;
    private List<String> list;
    public List<String> startList=new ArrayList<>();
    public List<String> endList=new ArrayList<>();
    public List<String> activityList=new ArrayList<>();

    private int textViewResourceId;

    public TextViewAdapter(Context context,
                           int resource, int textViewResourceId,
                           List<String> objects){
        super(context,resource,textViewResourceId,objects);
        this.context=context;
        this.resource=resource;
        this.list=objects;
        this.textViewResourceId=textViewResourceId;


        for(int i=0;i<list.size();i++) {
            String row= list.get(i);
            String[] rowArray=row.split(" ");

            activityList.add(rowArray[0]);
            startList.add(rowArray[1]);
            endList.add(rowArray[2]);
        }
    }




    @Override

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        TextView text1 = (TextView) view.findViewById(R.id.text1);
        TextView text2 = (TextView) view.findViewById(R.id.text2);
        TextView text3 = (TextView) view.findViewById(R.id.text3);
        text1.setText(activityList.get(position));
        text2.setText("Start: "+startList.get(position));
        text3.setText("Ende: "+endList.get(position));

        if(DeleteActivity.checkedActivityNumbers.contains(position)){
            view.setBackgroundColor(Color.parseColor("#9fa8da"));
        }else{
            view.setBackgroundColor(Color.WHITE);
        }
        return view;

    }
}