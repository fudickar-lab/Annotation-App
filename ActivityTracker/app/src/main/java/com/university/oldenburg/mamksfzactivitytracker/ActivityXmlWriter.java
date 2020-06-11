package com.university.oldenburg.mamksfzactivitytracker;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * singleton class for writing activity labels in  a specific xml format to a file
 */
public class ActivityXmlWriter {

    private static ActivityXmlWriter activityWriter = null;
    private static FileWriter activityFW;
    private static BufferedWriter activityBW;
    private static String lastLabelGroupID;
    private  File file;
    private String activeLabelGroup = "";
    private boolean firstLabel = false;



    public ActivityXmlWriter(File file) {


        try {


            this.file = file;

            if (!file.exists()) {

                if (file.createNewFile()) {
                    activityFW = new FileWriter(file, true);
                    activityBW = new BufferedWriter(activityFW);

                    writeHeader();
                }

            } else {
                firstLabel = true;
                String lastline = HelperMethods.getLastLine(file);
                if (lastline.equals("</labelInformation>")) {
                    HelperMethods.deleteLastLine(file, HelperMethods.getLastLineLength(lastline));

                }
                lastLabelGroupID = HelperMethods.getLastGroupID(file);
                activityFW = new FileWriter(file, true);
                activityBW = new BufferedWriter(activityFW);

            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static synchronized ActivityXmlWriter getInstance(File file) {
        if (activityWriter == null) {
            activityWriter = new ActivityXmlWriter(file);
            Log.i("activityXmlWriter", "getNewInstance: ");

        }
        Log.i("activityXmlWriter", "getInstance: ");

        return activityWriter;
    }




    private void writeHeader() {


        try {

            activityBW.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
            activityBW.newLine();
            activityBW.append("<labelInformation schemaVersion=\"label_information2.xsd\">");
            activityBW.newLine();
            activityBW.flush();

        } catch (IOException a) {
            Log.e("Exception", "File write failed: " + a.toString());
        }


    }

    void writeLabel(String activity_name, String start, String end) {


        try {

            String labelGroup = HelperMethods.getBasicGroupId(activity_name);
            lastLabelGroupID = HelperMethods.getLastGroupID(file);


            Log.i("ActivityXml", "labelgroup: " + labelGroup + " " + lastLabelGroupID);

            if (firstLabel && labelGroup.equals(lastLabelGroupID)){

                String lastline = HelperMethods.getLastLine(file);
                Log.i("ActivityXml", "lastLine: " +lastline);

                HelperMethods.deleteLastLine(file, HelperMethods.getLastLineLength(lastline));
                activityBW.newLine();
               firstLabel = false;
               activeLabelGroup = lastLabelGroupID;
           }



            Log.i("ActivityXml", "writeLabel: " + activity_name + " " + activeLabelGroup);
            if (activeLabelGroup.equals("")) {
                activeLabelGroup = labelGroup;
                activityBW.append("<labelGroup groupID=\"" + activeLabelGroup + "\">");
                activityBW.newLine();
            } else if (!activeLabelGroup.equals(labelGroup)) {
                activeLabelGroup = labelGroup;
                activityBW.append("</labelGroup>");
                activityBW.newLine();
                activityBW.append("<labelGroup groupID=\"" + activeLabelGroup + "\">");
                activityBW.newLine();
            }
            activityBW.append("<label>");
            activityBW.newLine();
            activityBW.append("<interval>");
            activityBW.newLine();
            activityBW.append("<start>" + start + "</start>");
            activityBW.newLine();
            activityBW.append("<end>" + end + "</end>");
            activityBW.newLine();
            activityBW.append("</interval>");
            activityBW.newLine();
            activityBW.append("<description>" + activity_name + "</description>");
            activityBW.newLine();
            activityBW.append("<labelID>" + HelperMethods.getLabelID(activity_name) + "</labelID>");
            activityBW.newLine();
            activityBW.append("</label>");
            activityBW.newLine();
            activityBW.flush();

        } catch (IOException a) {
            Log.e("Exception", "File write failed: " + a.toString());
        }

    }

    void closeActivityXmlWriter() {

        try {
            activityWriter = null;
            String lastline = HelperMethods.getLastLine(file);
            if (lastline.equals("</label>")) {
                activityBW.append("</labelGroup>");
                activityBW.newLine();
                activityBW.append("</labelInformation>");
            } else if (lastline.equals("</labelGroup>")) {
                activityBW.newLine();
                activityBW.append("</labelInformation>");
            }
            activityBW.flush();
            activityBW.close();
            activityFW = null;
            activityBW = null;

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    /**
     * Removes all the lines of the file connected to the given label number
     * @param file
     * @param labelNr
     * @throws IOException
     */
    public static void removeLabel(final File file, int labelNr)throws IOException{
        InputStream is = null;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        Document doc = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            doc      = dBuilder.parse(is);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        Element element = (Element) doc.getElementsByTagName("label").item(labelNr);
        Log.i("element", "XML:"+getValue("description",element));
        element.getParentNode().removeChild(element);
        //doc.normalize();


        try {
            prettyPrint(file,doc);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }



    public static final void prettyPrint(File file, Document document) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(file);
        transformer.transform(domSource, streamResult);
        removeEmptyLabelGroups(file);

    }


    private static void removeEmptyLabelGroups(File file) {

        InputStream is = null;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        Document doc = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            doc      = dBuilder.parse(is);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        NodeList nList = doc.getElementsByTagName("labelGroup");

        for (int i=0; i<nList.getLength(); i++) {
            Log.i("RemoveE", ": "+nList.getLength());

            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                Log.i("RemoveE", ": "+element.getTagName());

                if (element.getElementsByTagName("label").getLength()==0){
                    element.getParentNode().removeChild(element);
                    Log.i("RemoveE", "removeEmptyLabelGroupsByTag: ");


                }
                if (!element.hasChildNodes()) {
                    element.getParentNode().removeChild(element);
                    Log.i("RemoveE", "removeEmptyLabelGroups: ");
                }


            }
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        DOMSource domSource = new DOMSource(doc);
        StreamResult streamResult = new StreamResult(file);
        try {
            transformer.transform(domSource, streamResult);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        try {
            removeEmptyLine(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove a given line
     * @param file
     * @throws IOException
     */
    public static void removeEmptyLine(final File file) throws IOException{
        final List<String> lines = new LinkedList<>();
        final Scanner reader = new Scanner(new FileInputStream(file), "UTF-8");
        while(reader.hasNextLine()) {
            String line = reader.nextLine();
            if (!line.equals(""))
                lines.add(line);
        }
        reader.close();
        final BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
        for(int i =0;i<lines.size()-1;i++){
            writer.append(lines.get(i));
            writer.newLine();
        }
        writer.append(lines.get(lines.size()-1));
        writer.flush();
        writer.close();
        ActivityTrackerApplication application = ActivityTrackerApplication.getInstance();
        application.setActivityFile(file);

    }
    private static String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodeList.item(0);
        return node.getNodeValue();
    }
}
