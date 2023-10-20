package de.paulsenik.pptkaraoke.utils;

import de.paulsenik.jpl.io.PFile;
import de.paulsenik.jpl.io.PFolder;
import de.paulsenik.jpl.utils.PSystem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PresentationManager {

    public List<Presentation> presentations = new ArrayList<>();

    private String presentationDir;
    private String folderName;

    public PresentationManager(String presentationDir) {
        initPresentations(presentationDir);
    }

    private void initPresentations(String presentationDir) {
        for (String folderPath : PFolder.getSubFolders(presentationDir)) {
            JSONArray storage = getPresentationInfo(presentationDir, PFolder.getName(folderPath));
            for (String filePath : PFolder.getFiles(folderPath, null)) {
                // TODO
            }
        }
    }

    private JSONArray getPresentationInfo(String presentationDir, String folderName) {
        PFile file = new PFile(presentationDir + PSystem.getFileSeparator() + folderName + ".json");
        if (!file.exists()) {
            return null;
        }

        try {
            return new JSONArray(file.getFileAsString());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean SavePresentationInfo(String presentationDir) {
        Map<String, JSONArray> storage = new HashMap<>();

        for (Presentation p : presentations) {
            JSONArray json = storage.get(p.name());
            if (json == null) {
                json = new JSONArray();
                storage.put(p.name(), json);
            }

            JSONObject obj = new JSONObject();
        }

        return true;
    }


}
