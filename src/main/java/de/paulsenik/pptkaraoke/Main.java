package de.paulsenik.pptkaraoke;

import de.paulsenik.jpl.io.PFile;
import de.paulsenik.jpl.io.PFolder;
import de.paulsenik.jpl.ui.PUICheckBox;
import de.paulsenik.jpl.ui.PUIElement;
import de.paulsenik.jpl.utils.PSystem;
import de.paulsenik.pptkaraoke.utils.Presentation;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class Main {

  public static void main(String[] args) throws IOException {

    if (PUIElement.useGBC != PUIElement.useGBC) {
      UI ui = new UI();
      PUICheckBox c = new PUICheckBox(ui);
      c.setBounds(100, 100, 400, 400);
    }
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

}
