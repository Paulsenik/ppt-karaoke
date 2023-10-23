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
  public static Set<String> presentationTypes = new HashSet<>();
  public final Map<String, Presentation> presentations = new HashMap<>();

  public final Set<String> allYears = new HashSet<>();
  public final Set<Language> allLanguages = new HashSet<>();
  public final Set<String> allTags = new HashSet<>();
  public final Set<String> allTopics = new HashSet<>();

  private String presentationDir;
  private String folderName;

  public PresentationManager(String presentationDir) {
    presentationTypes.add("pptx");
    presentationTypes.add("ppsx");
    presentationTypes.add("pptm");
    presentationTypes.add("odp");
    presentationTypes.add("otp");
    presentationTypes.add("pdf");

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
        if (!presentationTypes.contains(PFile.getFileType(filePath))) {
          continue;
        }

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

        // property-index
        allYears.add(p.year());
        allLanguages.add(p.language());
        allTags.addAll(p.tags());
        allTopics.addAll(p.topics());

        presentations.put(p.name(), p);
      }
    }
  }

  public List<Presentation> filter(Set<String> years,
      Set<Language> languages, Set<String> tags, Set<String> topics) {

    return presentations.values().stream().filter(p -> {

      if (years != null && !years.isEmpty() && !years.contains(p.year())) {
        return false;
      }
      if (languages != null && !languages.isEmpty() && !languages.contains(p.language())) {
        return false;
      }
      // TODO toggle-able containsAll or contains any
      if (tags != null && !tags.isEmpty() && !p.tags().containsAll(tags)) {
        return false;
      }
      // TODO toggle-able containsAll or contains any
      if (topics != null && !topics.isEmpty() && !p.topics().containsAll(topics)) {
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
  public boolean savePresentationInfo() {
    System.out.println("[PresentationManager] :: saving PresentationInfo");
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