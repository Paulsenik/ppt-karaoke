package de.paulsenik.pptkaraoke.utils;

import de.paulsenik.jpl.io.PFile;
import de.paulsenik.jpl.io.PFolder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public record Presentation(
        String name,
        String folderLocation,
        int year,
        List<String> tags,
        List<String> topics, Language language) {


    public Presentation(String fileLocation) {
        this(PFile.getName(fileLocation), PFile.getParentFolder(fileLocation), getYear(PFile.getParentFolder(fileLocation)), new ArrayList<>(), new ArrayList<>(), Language.ENGLISH);
    }

    public Presentation(String fileLocation, List<String> tags, List<String> topic, Language language) {
        this(PFile.getName(fileLocation), PFile.getParentFolder(fileLocation), getYear(fileLocation), tags, topic, language);
    }

    private static int getYear(String folderLocation) {
        int year;
        try {
            year = Integer.parseInt(PFolder.getName(folderLocation));
        } catch (NumberFormatException e) {
            year = Calendar.getInstance().getWeekYear();
        }

        return year;
    }

    public static void main(String[] args) {

        ArrayList<String> test = new ArrayList<>();
        test.add("qa35uhz");
        test.add("qa35uh275z");
        test.add("2457qa35uhz");
        test.add("qa324575uhz");
        test.add("qa35uh3246z");
        System.out.println(new Presentation("asdf", "adf", 234, test, test, Language.ENGLISH).getSerialized().toString(3));
    }

    @Override
    public String toString() {
        return getSerialized().toString();
    }

    public JSONObject getSerialized() {
        JSONObject obj = new JSONObject();
        obj.append("tags", new JSONArray(tags));
        obj.append("topics", new JSONArray(topics));
        obj.append("language", language);
        return obj;
    }

}
