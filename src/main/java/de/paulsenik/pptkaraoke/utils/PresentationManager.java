package de.paulsenik.pptkaraoke.utils;

import de.paulsenik.jpl.io.PFile;
import de.paulsenik.jpl.io.PFolder;
import de.paulsenik.jpl.utils.PSystem;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
    // Not pretty :(
    presentationTypes.add("ppt");
    presentationTypes.add("pps");
    presentationTypes.add("pptx");
    presentationTypes.add("ppsx");
    presentationTypes.add("pptm");
    presentationTypes.add("odp");
    presentationTypes.add("otp");
    presentationTypes.add("pdf");

    allLanguages.addAll(Arrays.stream(Language.values()).toList());

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
      throw e;
    }
  }

  private void initPresentations() {
    String[] subFolders = PFolder.getSubFolders(presentationDir);
    JSONArray rawData = getRawPresentationInfo(presentationDir);

    if (subFolders == null) {
      return;
    }

    Map<String, JSONObject> data = rawData == null ? null : getPresentationInfo(rawData);

    for (String yearFolder : subFolders) {
      ArrayList<String> files = new ArrayList<>(Arrays.asList(PFolder.getFiles(yearFolder, null)));
      for (File f : PFolder.getAllFoldersOfRoot(new File(yearFolder))) {
        files.addAll(Arrays.asList(PFolder.getFiles(f.getAbsolutePath(), null)));
      }

      if (files.isEmpty()) {
        continue;
      }

      for (String filePath : files) {
        if (!presentationTypes.contains(PFile.getFileType(filePath).toLowerCase())) {
          System.err.println(filePath);
          continue;
        }

        String name = PFile.getName(filePath);
        JSONObject jsonObj = data == null ? null : data.get(name);
        Presentation p;

        if (jsonObj == null) {
          p = new Presentation(filePath, yearFolder);
          System.out.println("[PresentationManager] :: initialized :" + filePath);
        } else {
          Language language = Language.UNDEFINED;
          try {
            language = jsonObj.getEnum(Language.class, "language");
          } catch (JSONException e) {
            e.printStackTrace();
          }

          Set<String> tags = new HashSet<>();
          try {
            for (Object obj : jsonObj.getJSONArray("tags")) {
              tags.add((String) obj);
            }
          } catch (JSONException e) {
            e.printStackTrace();
          }

          Set<String> topics = new HashSet<>();
          try {
            for (Object obj : jsonObj.getJSONArray("topics")) {
              topics.add((String) obj);
            }
          } catch (JSONException e) {
            e.printStackTrace();
          }

          p = new Presentation(filePath, yearFolder, language, tags, topics);
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

    System.out.println("[PresentationManager] :: initialized " + presentations.size()
        + " different Presentations from " + presentationDir);
  }

  public List<Presentation> filter(Set<String> years,
      Set<Language> languages, Set<String> tags, Set<String> topics) {

    return Presentation.getSortedPresentations(presentations.values()).stream().filter(p -> {

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