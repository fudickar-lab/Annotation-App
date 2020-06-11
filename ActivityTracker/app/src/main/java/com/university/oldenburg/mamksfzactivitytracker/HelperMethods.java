package com.university.oldenburg.mamksfzactivitytracker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Hashtable;


/**
 * Created by maren on 05.05.17.
 */

public class HelperMethods {

    private static Hashtable<String, String> xmlCodeMappingTable = new Hashtable<String, String>() {

        {
//////////////////////////ASSESSMENTS //////////////////////////

            // 4100 as Geriatrics Assessments
            // 4200 as Sport Assessments
            // ...
            put("ASSESSMENT_FRAILTY_SPEED", "4.1.00");
            put("ASSESSMENT_TUG", "4.1.01");
            put("ASSESSMENT_SPPB_BALANCE", "4.1.02");
            put("ASSESSMENT_SPPB_SPEED", "4.1.03");
            put("ASSESSMENT_SPPB_CHAIRRISE", "4.1.04");
            put("ASSESSMENT_SCPT", "4.1.05");
            put("ASSESSMENT_DEMMI_BED_BRIDGE", "4.1.06");
            put("ASSESSMENT_DEMMI_BED_ROLL_TO_SIDE", "4.1.07");
            put("ASSESSMENT_DEMMI_BED_SIT_UP", "4.1.08");
            put("ASSESSMENT_DEMMI_CHAIR_SIT", "4.1.09");
            put("ASSESSMENT_DEMMI_CHAIR_STAND_UP", "4.1.10");
            put("ASSESSMENT_DEMMI_CHAIR_STAND_UP_WITHOUT_HELP", "4.1.11");
            put("ASSESSMENT_DEMMI_BALANCE_STATIC", "4.1.12");
            put("ASSESSMENT_DEMMI_WALK", "4.1.13");
            put("ASSESSMENT_DEMMI_PENCIL", "4.1.14");
            put("ASSESSMENT_DEMMI_WALK_BACKWARDS", "4.1.15");
            put("ASSESSMENT_DEMMI_JUMP", "4.1.16");
            put("ASSESSMENT_CMJ", "4.1.17");
            put("ASSESSMENT_WALKSIXMINUTES", "4.1.18");
//////////////////////////GENERAL MOVEMENT //////////////////////////
            /**
             * second number: 1 = STATIC, 2 = TRANSITION, 3 = DYNAMIC
             */
            put("WALK", "1.3.30");
            put("STAND", "1.1.10");
            put("STAND_SIT", "1.2.20");
            put("STAND_LIE", "1.2.21");
            put("SIT", "1.1.11");
            put("SIT_STAND", "1.2.22");
            put("SIT_LIE", "1.2.23");
            put("LIE_ON_BACK", "1.1.12");
            put("LIE_ON_SIDE", "1.1.13");
            put("LIE_ON_BACK_LIE_ON_SIDE", "1.2.24");
            put("LIE_ON_SIDE_LIE_ON_BACK", "1.2.25");
            put("LIE_SIT", "1.2.26");
            put("STAIR_CLIMB", "1.3.31");
            put("STAIR_CLIMB_DOWN", "1.3.32");
            put("BEND_OVER_OR_SQUAT", "1.3.33");
            put("WALK_BACKWARDS", "1.3.34");
            put("JUMP", "1.3.35");
            put("STEP", "1.3.36");
            put("TURN_AROUND_LEFT", "1.3.37");
            put("TURN_AROUND_RIGHT", "1.3.38");
            put("TURN_AROUND", "1.3.39");
//////////////////////////LOCATION LABELS //////////////////////////
            put("HOME", "3.1.1");
            put("INSIDE", "3.1.2");
            put("OUTSIDE", "3.1.3");
//////////////////////////OTHER LABELS //////////////////////////
            put("UNKNOWN", "0.0.1");
            put("START", "44.1.1");

        }
    };

    public static void deleteLastLine(File file, int lengthOfLIne) {
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            long length = raf.length();
            System.out.println("File Length=" + raf.length());
            raf.setLength(length - lengthOfLIne - 1);
            System.out.println("File Length=" + raf.length());
            raf.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Reads the last line of the XML file and returns it
     *
     * @return lastLine
     */

    public static String getLastLine(File file) {
        String lastLine = "";
        String currentLine;

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            while ((currentLine = br.readLine()) != null) {
                lastLine = currentLine;
            }
        } catch (IOException a) {
            a.printStackTrace();
        }
        return lastLine;
    }

    public static String getLastGroupID(File file) {
        String lastLine = "";
        String currentLine;

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            while ((currentLine = br.readLine()) != null) {
                if (currentLine.contains("<labelGroup groupID"))
                    lastLine = currentLine;
            }
        } catch (IOException a) {
            a.printStackTrace();
        }
        if (lastLine.length() > 1)
            return lastLine.split("=")[1].replace("\"", "").replace(">", "");
        else return "";
    }

    /**
     * Calculates length of last line
     *
     * @param lastLine
     * @return length of line
     */

    public static int getLastLineLength(String lastLine) {
        return lastLine.length();

    }

    public static String getCurrentTime() {
        return String.valueOf(System.currentTimeMillis());
    }

    static String getLabelID(String activity) {

        String labelID = xmlCodeMappingTable.get(activity);
        if (labelID != null)
            return labelID;
        else return "98.0.0";
    }

    static String getBasicGroupId(String activity) {

        String labelID = xmlCodeMappingTable.get(activity);
        if (labelID != null)
            return labelID.split("\\.")[0];
        else return "98";

    }
}
