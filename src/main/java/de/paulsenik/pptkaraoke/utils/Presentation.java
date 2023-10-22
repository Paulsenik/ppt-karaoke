package de.paulsenik.pptkaraoke.utils;

import de.paulsenik.jpl.io.PFile;
import de.paulsenik.jpl.io.PFolder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.json.JSONObject;

public record Presentation(
    String name,
    String folderLocation,
    int year,
    List<String> tags,
    List<String> topics, Language language) {


  public Presentation(String fileLocation) {
    this(PFile.getName(fileLocation), PFile.getParentFolder(fileLocation),
        getYear(PFile.getParentFolder(fileLocation)), new ArrayList<>(), new ArrayList<>(),
        Language.ENGLISH);
  }

  public Presentation(String fileLocation, List<String> tags, List<String> topic,
      Language language) {
    this(PFile.getName(fileLocation), PFile.getParentFolder(fileLocation), getYear(fileLocation),
        tags, topic, language);
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

  public JSONObject getSerialized() {
    JSONObject obj = new JSONObject();
    obj.put("name", name);
    obj.put("language", language);
    obj.put("tags", tags);
    obj.put("topics", topics);
    return obj;
  }

  @Override
  public String toString() {
    return getSerialized().toString();
  }

}
