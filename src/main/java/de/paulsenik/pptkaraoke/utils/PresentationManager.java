package de.paulsenik.pptkaraoke.utils;

import de.paulsenik.jpl.io.PFile;
import de.paulsenik.jpl.io.PFolder;
import de.paulsenik.jpl.utils.PSystem;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PresentationManager {

  public static final String PPT_SAVE_FILE = "karaoke.json";

  public final Map<String, Presentation> presentations = new HashMap<>();

  private String presentationDir;
  private String folderName;

  public PresentationManager(String presentationDir) {
    if (presentationDir == null || presentationDir.isBlank() || !(new File(
        presentationDir).exists())) {
      this.presentationDir = System.getProperty("user.dir");
    } else {
      this.presentationDir = new PFile(presentationDir).getAbsolutePath();
    }
    System.out.println("[PresentationManager] :: Presentation-Dir :" + this.presentationDir);
    try {
      initPresentations();
    } catch (Exception e) {
      System.err.println("Initialization Error! Config may not be initialized completely!");
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws InterruptedException {
    PresentationManager m = new PresentationManager("/home/paulsen/Documents/PPT/");
    List<String> l = new ArrayList<>();
    l.add("a123  ");
    l.add("a 234 ");
    l.add("a  345");
    m.presentations.put("test", new Presentation("test", "_", 2020, Language.ENGLISH, l, l));

    Set<Language> langs = new HashSet<>();
    langs.add(Language.ENGLISH);
    langs.add(Language.GERMAN);

    m.savePresentationInfo();
    Set<String> tags = new HashSet<>();
    tags.add("a  345");
    List<Presentation> p = m.filter(null, langs, tags, null);
    System.out.println(p.toString());
  }

  private void initPresentations() {
    String[] subFolders = PFolder.getSubFolders(presentationDir);
    JSONArray rawData = getRawPresentationInfo(presentationDir);

    if (subFolders == null) {
      return;
    }

    Map<String, JSONObject> data = rawData == null ? null : getPresentationInfo(rawData);

    for (String folderPath : subFolders) {
      String[] files = PFolder.getFiles(folderPath, null);
      if (files == null) {
        continue;
      }

      for (String filePath : files) {
        String name = PFile.getName(filePath);
        JSONObject jsonObj = data == null ? null : data.get(name);

        Presentation p;
        if (jsonObj == null) {
          p = new Presentation(filePath);
          System.out.println("[PresentationManager] :: initialized :" + filePath);
        } else {
          Language language = Language.UNDEFINED;
          try {
            language = jsonObj.getEnum(Language.class, "language");
          } catch (JSONException e) {
            e.printStackTrace();
          }

          List<String> tags = new ArrayList<>();
          try {
            for (Object obj : jsonObj.getJSONArray("tags")) {
              tags.add((String) obj);
            }
          } catch (JSONException e) {
            e.printStackTrace();
          }

          List<String> topics = new ArrayList<>();
          try {
            for (Object obj : jsonObj.getJSONArray("topics")) {
              topics.add((String) obj);
            }
          } catch (JSONException e) {
            e.printStackTrace();
          }

          p = new Presentation(filePath, language, tags, topics);
          System.out.println("[PresentationManager] :: initialized (with data):" + filePath);
        }
        presentations.put(p.name(), p);
      }
    }
  }

  public List<Presentation> filter(Set<Integer> years,
      Set<Language> languages, Set<String> tags, Set<String> topics) {

    return presentations.values().stream().filter(p -> {

      if (years != null && !years.contains(p.year())) {
        return false;
      }
      if (languages != null && !languages.contains(p.language())) {
        return false;
      }
      // TODO toggle-able containsAll or contains any
      if (tags != null && !p.tags().containsAll(tags)) {
        return false;
      }
      // TODO toggle-able containsAll or contains any
      if (topics != null && !p.topics().containsAll(topics)) {
        return false;
      }

      return true;
    }).toList();
  }

  private Map<String, JSONObject> getPresentationInfo(JSONArray data) {
    if (data == null || data.isEmpty()) {
      return null;
    }

    Map<String, JSONObject> map = new HashMap<>();
    for (Object obj : data) {
      if (obj instanceof JSONObject) {
        JSONObject jsonObj = (JSONObject) obj;
        map.put(jsonObj.getString("name"), jsonObj);
      }
    }

    return map;
  }

  /**
   * Reads the configuration for the presentations
   *
   * @param presentationDir
   * @return the JSON-data - null if no valid file can be found
   */
  private JSONArray getRawPresentationInfo(String presentationDir) {
    PFile file = new PFile(presentationDir + PSystem.getFileSeparator() + PPT_SAVE_FILE);
    if (!file.exists()) {
      return null;
    }

    String content = file.getFileAsString();
    if (content == null || content.isBlank()) {
      return null;
    }

    try {
      return new JSONArray(content);
    } catch (JSONException e) {
      e.printStackTrace();
      System.err.println("Content:\n" + content);
      return null;
    }
  }

  /**
   * Saves all presentation-Infos
   *
   * @return true if successful
   */
  private boolean savePresentationInfo() {
    JSONArray storage = new JSONArray();

    for (Presentation p : presentations.values()) {
      storage.put(p.getSerialized());
    }

    PFile file = new PFile(presentationDir + PSystem.getFileSeparator() + PPT_SAVE_FILE);
    try {
      file.writeFile(storage.toString(2));
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

}