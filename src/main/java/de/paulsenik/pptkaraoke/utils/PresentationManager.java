package de.paulsenik.pptkaraoke.utils;

import de.paulsenik.jpl.io.PFile;
import de.paulsenik.jpl.io.PFolder;
import de.paulsenik.jpl.utils.PSystem;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PresentationManager {

  public static String PPT_SAVE_FILE = "karaoke.json";

  private final List<Presentation> presentations = new ArrayList<>();

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

//    try {
//      Main.open(new Presentation("/home/paulsen/Documents/PPT/karaoke.json"));
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }

    m.savePresentationInfo();
    Thread.sleep(1000);
  }

  private void initPresentations() {
    String[] subFolders = PFolder.getSubFolders(presentationDir);
    JSONArray rawData = getRawPresentationInfo(presentationDir);

    if (subFolders == null) {
      return;
    }

    Map<String, JSONObject> data = rawData == null ? null : getPresentationInfo(rawData);

    for (String folderPath : subFolders) {
      for (String filePath : PFolder.getFiles(folderPath, null)) {
        String name = PFile.getName(filePath);
        JSONObject jsonObj = data == null ? null : data.get(name);

        Presentation p;
        if (jsonObj == null) {
          p = new Presentation(filePath);
          System.out.println("[PresentationManager] :: initialized :" + filePath);
        } else {
          List<String> tags = new ArrayList<>();
          List<String> topics = new ArrayList<>();
          Language language = Language.ENGLISH;

          p = new Presentation(filePath, tags, topics, language);
          System.out.println("[PresentationManager] :: initialized (with data):" + filePath);
        }
        presentations.add(p);
      }
    }
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

    for (Presentation p : presentations) {
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