package de.paulsenik.pptkaraoke;

import de.paulsenik.jpl.ui.PUIElement;
import de.paulsenik.jpl.ui.PUIList;
import de.paulsenik.jpl.ui.PUIText;
import de.paulsenik.jpl.ui.core.PUIAction;
import de.paulsenik.jpl.ui.core.PUIFrame;
import de.paulsenik.pptkaraoke.utils.Presentation;
import de.paulsenik.pptkaraoke.utils.PresentationManager;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JFileChooser;

public class UI extends PUIFrame {

  /**
   * 0 = Settings-Menu 1 = Play-Menu
   */
  private int menu = 0;
  // Buttons
  private PUIElement menuSettingsButton;
  private PUIElement menuPlayButton;
  private PUIText folderButton;
  private JFileChooser folderChooser;

  // Lists
  private PUIList presentationList;

  public UI() {
    initElements();
    folderChooser = new JFileChooser();
    folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    folderChooser.setMultiSelectionEnabled(false);
  }

  private void initElements() {
    menuSettingsButton = new PUIElement(this);
    menuSettingsButton.addActionListener(puiElement -> changeMenu(0));
    menuSettingsButton.setDraw((g, x, y, w, h) -> {
      if (menu == 0) {
        g.setColor(Color.WHITE);
      } else {
        g.setColor(Color.gray);
      }
      g.fillRect(x + 5, y + 5, w - 10, h / 6);
      g.fillRect(x + 5, y + 5 + 2 * h / 6, w - 10, h / 6);
      g.fillRect(x + 5, y + 5 + 4 * h / 6, w - 10, h / 6);
    });

    menuPlayButton = new PUIElement(this);
    menuPlayButton.addActionListener(puiElement -> changeMenu(1));
    menuPlayButton.setDraw((g, x, y, w, h) -> {
      if (menu == 1) {
        g.setColor(Color.WHITE);
      } else {
        g.setColor(Color.gray);
      }
      int[] X = {x + 5, x + 5, x + w - 5};
      int[] Y = {y + 5, y + h - 5, y + h / 2};
      g.fillPolygon(X, Y, 3);
    });

    folderButton = new PUIText(this, "Folder");
    folderButton.addActionListener(puiElement -> {
      int returnVal = folderChooser.showOpenDialog(this);

      if (returnVal != JFileChooser.APPROVE_OPTION) {
        System.err.println("[FileManager] :: FileChooser canceled!");
      } else {
        File f = folderChooser.getSelectedFile();
        Main.presentationManager = new PresentationManager(f.getAbsolutePath());
        updatePresentationList();
      }
    });

    presentationList = new PUIList(this);

    for (PUIElement e : PUIElement.registeredElements) {
      e.doPaintOverOnHover(false);
      e.doPaintOverOnPress(false);
    }
  }

  @Override
  public void updateElements() {
    super.updateElements();
    menuSettingsButton.setBounds(0, 0, 80, 80);
    menuPlayButton.setBounds(80, 0, 80, 80);

    if (menu == 0) { //settings
      folderButton.setEnabled(true);
      presentationList.setEnabled(true);
      folderButton.setBounds(getWidth() * 2 / 3 + 5, 5, getWidth() / 3 - 10, 70);
      presentationList.setBounds(getWidth() * 2 / 3, 80, getWidth() / 3, getHeight() - 80);
      presentationList.setShowedElements(20);
    } else {
      folderButton.setEnabled(false);
      presentationList.setEnabled(false);
    }
    if (menu == 1) { //play

    } else {

    }

    if (menu > 1) {
      throw new IllegalArgumentException("wrong menu-id");
    }
  }

  public void updatePresentationList() {
    ArrayList<PUIElement> elements = new ArrayList<>();

    PUIAction action = puiElement -> {
      // TODO SELECT-Logic
    };

    for (Presentation p : Main.presentationManager.presentations.values()) {
      PUIText t = new PUIText(this, p.name());
      t.addActionListener(action);
      elements.add(t);
    }
    presentationList.clearElements();
    presentationList.addAllElements(elements);
  }

  public void changeMenu(int newMenu) {
    if (newMenu == menu) {
      return;
    }

    // conditions
    if (menu == 0) {
      if (Main.presentationManager == null) {
        return;
      }
    }

    menu = newMenu;
    updateElements();
  }
}