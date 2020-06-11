package com.university.oldenburg.mamksfzactivitytracker;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * singleton class for writing location labels  in  a specific xml format to a file
 */
public class LocationXmlWriter {

    private static LocationXmlWriter locationWriter = null;
    private static FileWriter locationFW;
    private static BufferedWriter locationBW;
    private String activeLabelGroup = "";
    private File file;


    public LocationXmlWriter(File file) {
        try {


            this.file = file;

            if (!file.exists()) {

                if (file.createNewFile()) {
                    locationFW = new FileWriter(file, true);
                    locationBW = new BufferedWriter(locationFW);

                    writeHeader();
                }

            } else {
                String lastLine= HelperMethods.getLastLine(file);
                boolean needNewLine = false;
                if (lastLine.equals("</labelInformation>")) {
                    needNewLine = true;
                    HelperMethods.deleteLastLine(file, HelperMethods.getLastLineLength(lastLine));
                    lastLine = HelperMethods.getLastLine(file);
                    HelperMethods.deleteLastLine(file, HelperMethods.getLastLineLength(lastLine));

                }

                locationFW = new FileWriter(file, true);
                locationBW = new BufferedWriter(locationFW);
                if (needNewLine)
                    locationBW.newLine();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static synchronized LocationXmlWriter getInstance(File file) {
        if (locationWriter == null) {
            locationWriter = new LocationXmlWriter(file);

        }
        return locationWriter;
    }


    private void writeHeader() {

        try {

            locationBW.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
            locationBW.newLine();
            locationBW.append("<labelInformation schemaVersion=\"label_information2.xsd\">");
            locationBW.newLine();
            locationBW.flush();

        } catch (IOException a) {
            Log.e("Exception", "File write failed: " + a.toString());
        }


    }

    void writeLocation(String location_name, String start, String stop) {


        try {


            activeLabelGroup = HelperMethods.getLastGroupID(file);
            String labelGroup = HelperMethods.getBasicGroupId(location_name);

            if (labelGroup.equals("")) {
                activeLabelGroup = labelGroup;
                locationBW.append("<labelGroup groupID=\"" + activeLabelGroup + "\">");
                locationBW.newLine();
            } else if (!activeLabelGroup.equals(labelGroup)) {
                activeLabelGroup = HelperMethods.getBasicGroupId(location_name);
                locationBW.append("</labelGroup>");
                locationBW.newLine();
                locationBW.append("<labelGroup groupID=\"" + activeLabelGroup + "\">");
                locationBW.newLine();
            }
            locationBW.append("<label>");
            locationBW.newLine();
            locationBW.append("<interval>");
            locationBW.newLine();
            locationBW.append("<start>" + start + "</start>");
            locationBW.newLine();
            locationBW.append("<end>" + stop + "</end>");
            locationBW.newLine();
            locationBW.append("<description>" + location_name + "</description>");
            locationBW.newLine();
            locationBW.append("<labelID>" + HelperMethods.getLabelID(location_name) + "</labelID>");
            locationBW.newLine();
            locationBW.append("</label>");
            locationBW.newLine();
            locationBW.flush();

        } catch (IOException a) {
            Log.e("Exception", "File write failed: " + a.toString());
        }
    }

    void closeLocationXmlWriter() {

        try {
            locationWriter = null;
            String lastline = HelperMethods.getLastLine(file);
            if (lastline.equals("</label>")) {
                locationBW.append("</labelGroup>");
                locationBW.newLine();
                locationBW.append("</labelInformation>");

            } else if (lastline.equals("</labelGroup>")) {
                locationBW.newLine();
                locationBW.append("</labelInformation>");
            }


            locationBW.flush();
            locationBW.close();
            locationFW = null;
            locationBW = null;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
