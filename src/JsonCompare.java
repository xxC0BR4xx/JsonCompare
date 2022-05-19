import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Flow;
import javax.swing.*;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.lang.reflect.Type;


/**
 * JsonCompare Class 
 * One class Application to display differences in two JsonFiles
 * With a simple GUI bases on JFrame Library
 * 
 * Jan Ellenberger
 * 19.05.2022
 * 
 */

public class JsonCompare {

    //ArrayLists to store the Diffs in respective genres
    static ArrayList<String> entriesOnLeft = new ArrayList<String>();
    static ArrayList<String> entriesOnRight = new ArrayList<String>();
    static ArrayList<String> entriesMissmatching = new ArrayList<String>();
    static ArrayList<String> entriesDiffering = new ArrayList<String>();

    public static void main(String[] args) throws Exception {
    // calls the compare function to directly compare the two InputFiles
    compare();
    
      JFrame frame = new JFrame("JSON Diff Displayer");
      frame.setLayout(new FlowLayout());
      JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
      panel.add(Box.createRigidArea(new Dimension(0,5)));


      /*
      --Uncomment this
      if you want to Display in a non-scrollable lable --

      // initialize the labels
      JLabel label;
      JLabel label2;
      JLabel label3;

      label = new JLabel();
      label2 = new JLabel();
      label3 = new JLabel();

      // build String ot of the StringArrayList
      String entriesOnLeftString = String.join("\n ", entriesOnLeft);
      String entriesOnRightString = String.join("\n ", entriesOnRight);
      String entriesMissmatchingString = String.join("\n ", entriesMissmatching);
      String entriesDifferingString = String.join("\n ", entriesDiffering);

      // set the text of the labels
      label.setText(entriesOnLeftString);
      label2.setText(entriesOnRightString);
      label3.setText(entriesMissmatchingString);
      label3.setText(entriesDifferingString);

      // add the labels to the frame
      frame.add(label);
      frame.add(label2);
      frame.add(label3);
      */

      
      //Initialize JLists to display the the ArrayLists
      JList<String> list = new JList<String>(entriesOnLeft.toArray(new String[entriesMissmatching.size()]));
      JList<String> list2 = new JList<String>(entriesOnRight.toArray(new String[entriesMissmatching.size()]));
      JList<String> list3 = new JList<String>(entriesMissmatching.toArray(new String[entriesMissmatching.size()]));
      JList<String> list4 = new JList<String>(entriesDiffering.toArray(new String[entriesDiffering.size()]));


      //Add scrollable panel where we
      //display the lists
      frame.add(panel);
      panel.add(new JScrollPane());
      panel.add(list);
      panel.add(list2);
      panel.add(list3);
      panel.add(list4);


      //additional frame settings
      frame.setSize(1920,1080);
      frame.setVisible(true);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        
    }


    /**
    * Compare function 
    * Mainly used to spot differences in the two Input Files
    * Maps the JsonElement into a Map from Gson Library
    * Output via JFrame through ArrayList && also printed in cmd Line 
    */
    public static void compare(){
        JsonElement Json1=getJson1();
        JsonElement Json2=getJson2();


        Gson g = new Gson();
        Type mapType = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> firstMap = g.fromJson(Json1, mapType);
        Map<String, Object> secondMap = g.fromJson(Json2, mapType);
        MapDifference<String, Object> difference = Maps.difference(firstMap, secondMap);


        // cmdLine output of the spottet Diffs
        System.out.println("\n\nEntries are missing in Json2\n--------------------------\n");
        difference.entriesOnlyOnLeft().forEach((key, value) ->  System.out.println("\n--> " + key + ": " + value));

        System.out.println("\n\nEntries are missing in Json1\n--------------------------\n");
        difference.entriesOnlyOnRight().forEach((key, value) ->  System.out.println("\n--> " + key + ": " + value));

        System.out.println("\n\nEntries Mismatching:\n--------------------------\n");
        difference.entriesDiffering().forEach((key, value) -> System.out.println("\n--> " + key + ": " + value));

        System.out.println("\n\nEntries Common:\n--------------------------\n");
        difference.entriesInCommon().forEach((key, value) -> System.out.println("\n--> " + key + ": " + value));



        /*
        Calls the Helperfunction to Load each Diff into the 
        respective ArrayList 
        */
        difference.entriesOnlyOnLeft().forEach((key, value) -> addToArrayOnlyLeft(key, value));
        difference.entriesOnlyOnRight().forEach((key, value) -> addToArrayOnlyRight(key, value));
        difference.entriesDiffering().forEach((key, value) -> addToArrayMissmatching(key, value));
        difference.entriesDiffering().forEach((key, value) -> addToArrayDiffering(key, value));
    }


    /** 
    * Helperfunction to load each Diff into a respective ArrayList defined earlier
    * @param  key     Stringobject of the respectiv Type in Json
    * @param  value   Object of the respective Attribute in Json
    *
    */
    public static void addToArrayOnlyLeft(String key, Object value){
        String k = key;
        Object v = value;
        if( v != null){
        String s = (k + ": " + v.toString());
        entriesOnLeft.add(s);
        }
    }

    public static void addToArrayOnlyRight(String key, Object value){
        String k = key;
        Object v = value;
        if( v != null){
        String s = (k + ": " + v.toString());
        entriesOnRight.add(s);
        }
    }

    public static void addToArrayMissmatching(String key, Object value){
        String k = key;
        Object v = value;
        if( v != null){
        String s = (k + ": " + v.toString());
        entriesMissmatching.add(s);
        }
    }

    public static void addToArrayDiffering(String key, Object value){
        String k = key;
        Object v = value;
        if( v != null){
        String s = (k + ": " + v.toString());
        entriesDiffering.add(s);
        }
    }


    /** 
    * Helperfunctions to read the JSON file and parse it into a JsonElement
    * from Gson Library to better compare the two files
    */
    public static JsonElement getJson1() {
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement=null;
        try {
            FileReader fileReader = new FileReader("src/comp1.json");//Json1 filePath
            jsonElement = jsonParser.parse(fileReader);
        } catch (Exception e) {
            System.out.println("File not found");
        }
        return jsonElement;
    }
    public static JsonElement getJson2() {
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement=null;
        try {
            FileReader fileReader = new FileReader("src/comp2.json");//Json2 filePath
            jsonElement = jsonParser.parse(fileReader);
        } catch (Exception e) {
            System.out.println("File not found");
        }
        return jsonElement;
    }


}