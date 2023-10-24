package de.paulsenik.pptkaraoke.utils;

import de.paulsenik.jpl.io.PFile;
import de.paulsenik.jpl.io.PFolder;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONObject;

public class Presentation {


  protected String name;
  protected String folderLocation;
  protected String year; //String is easier to read and work with
  protected Language language;
  Set<String> tags;
  Set<String> topics;

  public Presentation(String name, String folderLocation, String year, Language language,
      Set<String> tags,
      Set<String> topics) {
    this.name = name;
    this.folderLocation = folderLocation;
    this.year = year;
    this.language = language;
    this.tags = tags;
    this.topics = topics;
  }


  public Presentation(String fileLocation) {
    this(PFile.getName(fileLocation), PFile.getParentFolder(fileLocation),
        getYear(PFile.getParentFolder(fileLocation)),
        Language.UNDEFINED, new HashSet<>(), new HashSet<>());
  }

  public Presentation(String fileLocation,
      Language language, Set<String> tags, Set<String> topics) {
    this(PFile.getName(fileLocation), PFile.getParentFolder(fileLocation), getYear(fileLocation),
        language, tags, topics);
  }

  private static String getYear(String folderLocation) {
    int year;
    try {
      year = Integer.parseInt(PFolder.getName(PFile.getParentFolder(folderLocation)));
    } catch (NumberFormatException | NullPointerException e) {
      year = Calendar.getInstance().getWeekYear();
    }

    return String.valueOf(year);
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

  public String name() {
    return name;
  }

  public String year() {
    return year;
  }

  public Language language() {
    return language;
  }

  public void setLanguage(Language language) {
    this.language = language;
  }

  public Set<String> tags() {
    return tags;
  }

  public Set<String> topics() {
    return topics;
  }

  public String folderLocation() {
    return folderLocation;
  }
}
