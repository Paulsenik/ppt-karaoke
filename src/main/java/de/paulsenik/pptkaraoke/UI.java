package de.paulsenik.pptkaraoke;

import de.paulsenik.jpl.ui.PUIElement;
import de.paulsenik.jpl.ui.PUIList;
import de.paulsenik.jpl.ui.PUIText;
import de.paulsenik.jpl.ui.core.PUIAction;
import de.paulsenik.jpl.ui.core.PUIFrame;
import de.paulsenik.pptkaraoke.utils.Language;
import de.paulsenik.pptkaraoke.utils.Presentation;
import de.paulsenik.pptkaraoke.utils.PresentationManager;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JFileChooser;

public class UI extends PUIFrame {

  /**
   * 0 = Settings-Menu; 1 = Play-Menu; 2 = Filter-Menu
   */
  private int menu = 0;
  private int selectedProperty = 0;
  private Presentation selectedPresentation;
  private boolean initElements = false;

  // MISC
  private JFileChooser folderChooser;

  // Buttons
  private PUIElement menuSettingsButton;
  private PUIElement menuPlayButton;
  private PUIText folderButton;
  private PUIText presentationDisplay;
  private PUIText shuffleButton;
  private PUIElement menuFilterButton;
  private PUIText addPropertyButton;

  // Lists
  private PUIList presentationList;
  private PUIList filteredPresentationList;
  private PUIList properties;
  private PUIList propertyDisplay;


  public UI() {
    super();
    PUIElement.setDefaultColor(1, Color.WHITE);
    PUIElement.setDefaultColor(10, new Color(219, 130, 36));
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
    menuPlayButton.addActionListener(puiElement -> changeMenu(2));
    menuPlayButton.setDraw((g, x, y, w, h) -> {
      if (menu == 2) {
        g.setColor(Color.WHITE);
      } else {
        g.setColor(Color.gray);
      }
      int[] X = {x + 5, x + 5, x + w - 5};
      int[] Y = {y + 5, y + h - 5, y + h / 2};
      g.fillPolygon(X, Y, 3);
    });

    menuFilterButton = new PUIElement(this);
    menuFilterButton.setDraw((g, x, y, w, h) -> {
      if (menu == 1) {
        g.setColor(Color.WHITE);
      } else {
        g.setColor(Color.gray);
      }
      int[] X = {x + 5, x + w - 10, x + w / 2};
      int[] Y = {y + 5, y + 5, y + h / 3 * 2};
      g.fillPolygon(X, Y, 3);
      g.fillRect(x + w / 5 * 2, y + 5, w / 5, h - 10);
    });
    menuFilterButton.addActionListener(puiElement -> {
      changeMenu(1);
    });

    folderButton = new PUIText(this, "Folder");
    folderButton.setBackgroundColor(new Color(199, 186, 39));
    folderButton.addActionListener(puiElement -> {
      int returnVal = folderChooser.showOpenDialog(this);

      if (returnVal != JFileChooser.APPROVE_OPTION) {
        System.err.println("[FileManager] :: FileChooser canceled!");
      } else {
        File f = folderChooser.getSelectedFile();
        Main.presentationManager = new PresentationManager(f.getAbsolutePath());
        updatePresentationList();
        updateFilteredPresentationList();
      }
    });

    presentationList = new PUIList(this);
    presentationList.setShowedElements(20);
    presentationList.setSliderWidth(10);

    filteredPresentationList = new PUIList(this);
    filteredPresentationList.setShowedElements(20);
    filteredPresentationList.setSliderWidth(10);

    presentationDisplay = new PUIText(this, "...");
    presentationDisplay.setTextColor(Color.red);
    presentationDisplay.setBackgroundColor(Color.white);
    presentationDisplay.addActionListener(puiElement -> {
      if (presentationDisplay.getMetadata() != null
          && presentationDisplay.getMetadata() instanceof Presentation) {
        try {
          Main.open((Presentation) presentationDisplay.getMetadata());
        } catch (IOException e) {
          System.err.println("[UI] :: presentation could not be opened!");
          e.printStackTrace();
        }
      }
    });

    shuffleButton = new PUIText(this, "GET");
    shuffleButton.setTextColor(Color.GREEN);
    shuffleButton.setBackgroundColor(new Color(0, 0, 0, 0));
    shuffleButton.addActionListener(puiElement -> {
      Presentation p = Main.getRandomPresentation();
      if (p != null) {
        presentationDisplay.setText(p.name());
        presentationDisplay.setMetadata(p);
        updateElements();
      }
    });

    // Properties
    PUIAction propertyChange = puiElement -> {
      switch (((PUIText) puiElement).getText()) {
        case "Year":
          selectProperty(0);
          break;
        case "Lang":
          selectProperty(1);
          break;
        case "Tag":
          selectProperty(2);
          break;
        case "Topic":
          selectProperty(3);
          break;
        default:
          throw new IllegalArgumentException("property not defined");
      }
    };

    properties = new PUIList(this);
    properties.setShowedElements(10);
    properties.setSliderWidth(10);

    PUIText yearButton = new PUIText(this, "Year");
    PUIText languageButton = new PUIText(this, "Lang");
    PUIText tagButton = new PUIText(this, "Tag");
    PUIText topicButton = new PUIText(this, "Topic");
    yearButton.addActionListener(propertyChange);
    languageButton.addActionListener(propertyChange);
    tagButton.addActionListener(propertyChange);
    topicButton.addActionListener(propertyChange);
    properties.addElement(yearButton);
    properties.addElement(languageButton);
    properties.addElement(tagButton);
    properties.addElement(topicButton);

    propertyDisplay = new PUIList(this);
    propertyDisplay.setShowedElements(10);
    propertyDisplay.setSliderWidth(10);

    addPropertyButton = new PUIText(this, "+");
    addPropertyButton.setBackgroundColor(new Color(199, 186, 39));
    addPropertyButton.addActionListener(puiElement -> {
      addProperty();
    });

    for (PUIElement e : PUIElement.registeredElements) {
      e.doPaintOverOnHover(false);
      e.doPaintOverOnPress(false);
    }

    // Menu-Button
    menuSettingsButton.setBounds(0, 0, 80, 80);
    menuFilterButton.setBounds(80, 0, 80, 80);
    menuPlayButton.setBounds(160, 0, 80, 80);

    initElements = true;
  }

  @Override
  public void updateElements() {
    super.updateElements();

    if (!hasInit() || !initElements) {
      return;
    }

    if (menu == 0) { //settings
      presentationList.setEnabled(true);
      {
        presentationList.setBounds(20, 190, w() / 3 - 30, h() - 210);
      }
    } else {
      presentationList.setEnabled(false);
    }

    if (menu == 0 || menu == 1) {
      folderButton.setEnabled(true);
      properties.setEnabled(true);
      propertyDisplay.setEnabled(true);
      addPropertyButton.setEnabled(true);
      {
        folderButton.setBounds(20, 100, w() / 3 - 30, 90);
        properties.setBounds(w() / 3 + 10, 190, w() / 3 - 20, h() - 210);
        propertyDisplay.setBounds(w() / 3 * 2 + 10, 190, w() / 3 - 30, h() - 210);
        addPropertyButton.setBounds(w() / 3 * 2 + 10, 100, w() / 3 - 30, 90);
      }
    } else {
      folderButton.setEnabled(false);
      properties.setEnabled(false);
      propertyDisplay.setEnabled(false);
      addPropertyButton.setEnabled(false);
    }

    if (menu == 1) { // filter
      filteredPresentationList.setEnabled(true);
      {
        filteredPresentationList.setBounds(20, 190, w() / 3 - 30, h() - 210);
      }
    } else {
      filteredPresentationList.setEnabled(false);
    }

    if (menu == 2) { //play
      presentationDisplay.setEnabled(true);
      shuffleButton.setEnabled(true);
      {
        int textLength = Math.min(30, Math.max(presentationDisplay.getText().length() / 2, 10));
        int textHeight = w() / textLength;
        presentationDisplay.setBounds(10, (h() - textHeight) / 2, w() - 20, textHeight);
        shuffleButton.setBounds((w() - 150) / 2, presentationDisplay.getY() + textHeight + 10, 150,
            80);
      }
    } else {
      presentationDisplay.setEnabled(false);
      shuffleButton.setEnabled(false);
    }

    if (menu > 2) {
      throw new IllegalArgumentException("wrong menu-id");
    }
  }

  public void selectProperty(int property) {
    ArrayList<PUIElement> elements = properties.getElements();
    elements.get(selectedProperty).setBackgroundColor(PUIElement.getDefaultColor(0));
    elements.get(property).setBackgroundColor(PUIElement.getDefaultColor(10));
    this.selectedProperty = property;

    updatePropertyDisplay();

    updateElements();
  }

  public void updateFilteredPresentationList() {
    ArrayList<PUIElement> presentations = new ArrayList<>();
    Main.presentationManager.filter(Main.filterYears,
        Main.filterLanguages, Main.filterTags, Main.filterTopics).forEach(p -> {
      PUIText t = new PUIText(this, p.name());
      t.setBackgroundColor(PUIElement.getDefaultColor(2));
      t.setTextColor(PUIElement.getDefaultColor(3));
      presentations.add(t);
    });
    filteredPresentationList.clearElements();
    filteredPresentationList.addAllElements(presentations);
  }

  public void updatePropertyDisplay() {
    if (menu == 0) { // edit
      if (selectedPresentation == null) {
        return;
      }

      ArrayList<PUIElement> displayables = new ArrayList<>();
      switch (((PUIText) properties.getElements().get(selectedProperty)).getText()) {
        case "Year":
          displayables.add(new PUIText(this, selectedPresentation.year()));
          break;
        case "Lang":
          displayables.add(new PUIText(this, String.valueOf(selectedPresentation.language())));
          break;
        case "Tag":
          for (String tag : selectedPresentation.tags()) {
            PUIText t = new PUIText(this, tag);
            t.addActionListener(puiElement -> {
              selectedPresentation.tags().remove(tag);
              updatePropertyDisplay();
            });
            displayables.add(t);
          }
          break;
        case "Topic":
          for (String topic : selectedPresentation.topics()) {
            PUIText t = new PUIText(this, topic);
            t.addActionListener(puiElement -> selectedPresentation.topics().remove(topic));
            displayables.add(t);
          }
          break;
        default:
          throw new IllegalArgumentException("property not defined");
      }
      propertyDisplay.clearElements();
      propertyDisplay.addAllElements(displayables);

    } else if (menu == 1) { // filter
      ArrayList<PUIElement> displayables = new ArrayList<>();
      switch (((PUIText) properties.getElements().get(selectedProperty)).getText()) {
        case "Year":
          for (String s : Main.filterYears) {
            PUIText yearText = new PUIText(this, s);
            yearText.addActionListener(puiElement -> {
              Main.filterYears.remove(s);
              updatePropertyDisplay();
              updateFilteredPresentationList();
            });
            displayables.add(yearText);
          }
          break;
        case "Lang":
          for (Language l : Main.filterLanguages) {
            PUIText langText = new PUIText(this, l.toString());
            langText.addActionListener(puiElement -> {
              Main.filterLanguages.remove(Language.valueOf(((PUIText) puiElement).getText()));
              updatePropertyDisplay();
              updateFilteredPresentationList();
            });
            displayables.add(langText);
          }
          break;
        case "Tag":
          // TODO
          break;
        case "Topic":
          // TODO
          break;
        default:
          throw new IllegalArgumentException("property not defined");
      }
      propertyDisplay.clearElements();
      propertyDisplay.addAllElements(displayables);
    }
  }

  public void addProperty() {
    if (menu == 0) { // edit-mode
      // TODO
    } else if (menu == 1) { // filter-mode
      switch (((PUIText) properties.getElements().get(selectedProperty)).getText()) {
        case "Year": {
          ArrayList<String> values = new ArrayList<>(Main.presentationManager.allYears);

          if (!Main.presentationManager.allYears.isEmpty()) {
            int selected = getUserSelection("Years", values);
            if (selected >= 0) {
              Main.filterYears.add(values.get(selected));
            }
          }
        }
        break;
        case "Lang": {
          ArrayList<String> values = new ArrayList<>();
          Main.presentationManager.allLanguages.forEach(l -> values.add(l.name()));

          if (!values.isEmpty()) {
            int selected = getUserSelection("Languages", values);

            if (selected >= 0) {
              Main.filterLanguages.add(Language.valueOf(values.get(selected)));
            }
          }
        }
        break;
        case "Tag":
          break;
        case "Topic":
          break;
        default:
          throw new IllegalArgumentException("property not defined");
      }
      updatePropertyDisplay();
    } else {
      System.err.println("Properties can only be added in edit- and filter-mode!");
    }
  }

  public void updatePresentationList() {
    ArrayList<PUIElement> elements = new ArrayList<>();

    PUIAction action = puiElement -> {
      for (PUIElement e : presentationList.getElements()) {
        e.setBackgroundColor(PUIElement.getDefaultColor(0));
      }
      puiElement.setBackgroundColor(PUIElement.getDefaultColor(10));
      selectedPresentation = Main.presentationManager.presentations.get(
          ((PUIText) puiElement).getText());
      updatePropertyDisplay();
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
    if (newMenu == 1 || newMenu == 2) {
      if (Main.presentationManager == null) {
        return;
      }
    }

    menu = newMenu;
    updateElements();

    // 1-time visual updates
    if (newMenu == 0 || newMenu == 1) {
      updatePropertyDisplay();
    }
  }
}