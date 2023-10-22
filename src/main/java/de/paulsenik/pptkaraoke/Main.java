package de.paulsenik.pptkaraoke;

import de.paulsenik.jpl.io.PFile;
import de.paulsenik.jpl.io.PFolder;
import de.paulsenik.jpl.utils.PSystem;
import de.paulsenik.pptkaraoke.utils.Language;
import de.paulsenik.pptkaraoke.utils.Presentation;
import de.paulsenik.pptkaraoke.utils.PresentationManager;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Main {

  public static PresentationManager presentationManager;

  public static Set<String> filterYears = new HashSet<>();
  public static Set<Language> filterLanguages = new HashSet<>();
  public static Set<String> filterTags = new HashSet<>();
  public static Set<String> filterTopics = new HashSet<>();

  public static void main(String[] args) {
    filterLanguages.add(Language.GERMAN);
    filterYears.add("2023");
    UI ui = new UI();
  }

  public static void open(Presentation p) throws IOException {
    if (p.folderLocation() == null) {
      return;
    }

    String fileType = "";
    String[] files = PFolder.getFiles(p.folderLocation(), null);
    int characterShift = p.folderLocation().length() + 1;

    for (String file : files) {
      if (PFile.getName(file.substring(characterShift)).equals(p.name())) {
        fileType = PFile.getFileType(file);
        break;
      }
    }

    String path = p.folderLocation() + PSystem.getFileSeparator() + p.name() + '.' + fileType;
    File file = new File(path);

    if (!Desktop.isDesktopSupported()) {
      System.out.println("Desktop not supported");
      return;
    }

    Desktop desktop = Desktop.getDesktop();
    if (file.exists()) {
      desktop.open(file);
    }
  }

  public static Presentation getRandomPresentation() {
    if (presentationManager == null) {
      return null;
    }
    List<Presentation> presentations = presentationManager.filter(filterYears, filterLanguages,
        filterTags, filterTopics);
    return presentations.get(new Random().nextInt(presentations.size()));
  }
}
