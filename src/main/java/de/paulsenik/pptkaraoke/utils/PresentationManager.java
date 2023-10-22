package de.paulsenik.pptkaraoke.utils;

import de.paulsenik.jpl.io.PFile;
import de.paulsenik.jpl.io.PFolder;
import de.paulsenik.jpl.utils.PSystem;
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
    if (presentationDir == null || presentationDir.isBlank()) {
      this.presentationDir = System.getProperty("user.dir");
    } else {
      this.presentationDir = new PFile(presentationDir).getAbsolutePath();
    }
    System.out.println(this.presentationDir);
    try {
      initPresentations();
    } catch (Exception e) {
      System.err.println("Initialization Error! Config may not be initialized completely!");
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {

    ArrayList<String> test = new ArrayList<>();
    test.add("qa35uhz");
    test.add("qa35uh275z");
    test.add("2457qa35uhz");
    test.add("qa324575uhz");
    test.add("qa35uh3246z");
    Presentation p = new Presentation("asdf", "adf", 234, test, test, Language.ENGLISH);
    Presentation p2 = new Presentation("fhatj", "2436afhze", 246, test, test, Language.ENGLISH);

    PresentationManager m = new PresentationManager("/home/paulsen/Documents/PPT/");
    m.presentations.add(p);
    m.presentations.add(p2);
    m.savePresentationInfo();
  }

  private void initPresentations() {
    String[] subFolders = PFolder.getSubFolders(presentationDir);
    JSONArray rawData = getRawPresentationInfo(presentationDir);

    if (subFolders == null || rawData == null) {
      return;
    }

    Map<String, JSONObject> data = getPresentationInfo(rawData);

    for (String folderPath : subFolders) {
      for (String filePath : PFolder.getFiles(folderPath, null)) {
        // TODO
        System.out.println(filePath);
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