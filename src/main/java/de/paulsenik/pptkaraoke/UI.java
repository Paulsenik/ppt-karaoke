package de.paulsenik.pptkaraoke;

import de.paulsenik.jpl.ui.PUIElement;
import de.paulsenik.jpl.ui.PUIList;
import de.paulsenik.jpl.ui.PUIText;
import de.paulsenik.jpl.ui.core.PUIAction;
import de.paulsenik.jpl.ui.core.PUICanvas;
import de.paulsenik.jpl.ui.core.PUIFrame;
import de.paulsenik.jpl.utils.PSystem;
import de.paulsenik.pptkaraoke.utils.Language;
import de.paulsenik.pptkaraoke.utils.Presentation;
import de.paulsenik.pptkaraoke.utils.PresentationManager;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import javax.swing.JFileChooser;

public class UI extends PUIFrame {

  public static final String frameTitle = "PowerPoint-Karaoke - Presentation Manager";

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
  private PUIElement saveButton;

  // Lists
  private PUIList presentationList;
  private PUIList filteredPresentationList;
  private PUIList properties;
  private PUIList propertyDisplay;

  public UI() {
    super(frameTitle, 1000, 700);
    PUIElement.setDefaultColor(1, Color.WHITE);
    PUIElement.setDefaultColor(10, new Color(47, 124, 154));
    initElements();
    folderChooser = new JFileChooser();
    folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    folderChooser.setMultiSelectionEnabled(false);
  }

  private void initElements() {

    new PUICanvas(this, (g, x, y, w, h) -> {
      g.setColor(new Color(255, 255, 255, 10));
      g.fillRect(0, 0, w, 80);
    });

    menuSettingsButton = new PUIElement(this);
    menuSettingsButton.addActionListener(puiElement -> changeMenu(0));
    menuSettingsButton.setDraw((g, x, y, w, h) -> {
      int space = w / 5;
      if (menu == 0) {
        g.setColor(Color.GRAY);
        g.fillRoundRect(x + space / 2, y + space / 2, w - space, h - space, h / 8, h / 8);
        g.setColor(Color.WHITE);
      } else {
        g.setColor(Color.GRAY);
      }
      int gHeigt = (h - space * 2) / 5;
      g.fillRect(x + space, y + space, w - space * 2, gHeigt);
      g.fillRect(x + space, y + space + gHeigt * 2, w - space * 2, gHeigt);
      g.fillRect(x + space, y + space + gHeigt * 4, w - space * 2, gHeigt);
    });

    menuPlayButton = new PUIElement(this);
    menuPlayButton.addActionListener(puiElement -> changeMenu(2));
    menuPlayButton.setDraw((g, x, y, w, h) -> {
      int space = w / 5;
      if (menu == 2) {
        g.setColor(Color.GRAY);
        g.fillRoundRect(x + space / 2, y + space / 2, w - space, h - space, h / 8, h / 8);
        g.setColor(Color.WHITE);
      } else {
        g.setColor(Color.gray);
      }
      int[] X = {x + space, x + space, x + w - space};
      int[] Y = {y + space, y + h - space, y + h / 2};
      g.fillPolygon(X, Y, 3);
    });

    menuFilterButton = new PUIElement(this);
    menuFilterButton.setDraw((g, x, y, w, h) -> {
      int space = w / 5;
      if (menu == 1) {
        g.setColor(Color.GRAY);
        g.fillRoundRect(x + space / 2, y + space / 2, w - space, h - space, h / 8, h / 8);
        g.setColor(Color.WHITE);
      } else {
        g.setColor(Color.gray);
      }
      int[] X = {x + space, x + w - space, x + w / 2};
      int[] Y = {y + space, y + space, y + h / 7 * 4};
      g.fillPolygon(X, Y, 3);
      g.fillRect(x + w / 2 - w / 14, y + space, w / 7, h - space * 2);
    });
    menuFilterButton.addActionListener(puiElement -> {
      changeMenu(1);
    });

    folderButton = new PUIText(this, "Folder");
    folderButton.setBackgroundColor(PUIElement.getDefaultColor(10));
    folderButton.addActionListener(puiElement -> {
      int returnVal = folderChooser.showOpenDialog(this);

      if (returnVal != JFileChooser.APPROVE_OPTION) {
        System.err.println("[FileManager] :: FileChooser canceled!");
      } else {
        selectedPresentation = null;
        File f = folderChooser.getSelectedFile();
        Main.presentationManager = new PresentationManager(f.getAbsolutePath());
        folderButton.setText(PSystem.getFileSeparator() + f.getName());
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
    presentationDisplay.setTextColor(PUIElement.getDefaultColor(10));
    presentationDisplay.setDraw(null);
    presentationDisplay.addActionListener(puiElement -> {
      if (presentationDisplay.getMetadata() != null
          && presentationDisplay.getMetadata() instanceof Presentation) {
        try {
          Presentation p = (Presentation) presentationDisplay.getMetadata();
          Main.open(p);
          setTitle(frameTitle + " :: " + p.name());
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
        case "Language":
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
    PUIText languageButton = new PUIText(this, "Language");
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
    addPropertyButton.setBackgroundColor(PUIElement.getDefaultColor(10));
    addPropertyButton.addActionListener(puiElement -> {
      addProperty();
    });

    saveButton = new PUIText(this, "Save");
    saveButton.setDraw(null);
    saveButton.setTextColor(PUIElement.getDefaultColor(10));
    saveButton.addActionListener(puiElement -> {
      if (Main.presentationManager != null) {
        ((PUIText) saveButton).setText("ooo");
        Main.presentationManager.savePresentationInfo();
        ((PUIText) saveButton).setText("Save");
      }
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
      saveButton.setEnabled(true);
      {
        presentationList.setBounds(20, 190, w() / 3 - 30, h() - 210);
        saveButton.setBounds(w() - 160, 0, 160, 80);
      }
    } else {
      presentationList.setEnabled(false);
      saveButton.setEnabled(false);
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
        addPropertyButton.setBounds(w() / 3 * 2 + 10, 100, 90, 90);
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
        shuffleButton.setBounds((w() - 150) / 2,
            Math.max(presentationDisplay.getY() + textHeight + 10, h() / 4 * 3 - 40), 150,
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
    if (menu == 0 && ((PUIText) properties.getElements().get(selectedProperty)).getText()
        .equals("Year")) {
      addPropertyButton.setBackgroundColor(PUIElement.getDefaultColor(2));
      addPropertyButton.setTextColor(PUIElement.getDefaultColor(3));
    } else {
      addPropertyButton.setBackgroundColor(PUIElement.getDefaultColor(0));
      addPropertyButton.setTextColor(PUIElement.getDefaultColor(1));
    }

    if (menu == 0) { // edit
      if (selectedPresentation == null) {
        return;
      }

      ArrayList<PUIElement> displayables = new ArrayList<>();
      switch (((PUIText) properties.getElements().get(selectedProperty)).getText()) {
        case "Year" -> {
          PUIText t = new PUIText(this, selectedPresentation.year());
          t.setBackgroundColor(PUIElement.getDefaultColor(2));
          t.setTextColor(PUIElement.getDefaultColor(3));
          displayables.add(t);
        }
        case "Language" -> {
          PUIText t = new PUIText(this, String.valueOf(selectedPresentation.language()));
          t.addActionListener(puiElement -> addProperty());
          displayables.add(t);
        }
        case "Tag" -> {
          for (String tag : selectedPresentation.tags()) {
            PUIText t = new PUIText(this, tag);
            t.addActionListener(puiElement -> {
              selectedPresentation.tags().remove(tag);
              updatePropertyDisplay();
            });
            displayables.add(t);
          }
        }
        case "Topic" -> {
          for (String topic : selectedPresentation.topics()) {
            PUIText t = new PUIText(this, topic);
            t.addActionListener(puiElement -> selectedPresentation.topics().remove(topic));
            displayables.add(t);
          }
        }
        default -> {
          throw new IllegalArgumentException("property not defined");
        }
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
        case "Language":
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
          for (String s : Main.filterTags) {
            PUIText text = new PUIText(this, s);
            text.addActionListener(puiElement -> {
              Main.filterTags.remove(s);
              updatePropertyDisplay();
              updateFilteredPresentationList();
            });
            displayables.add(text);
          }
          break;
        case "Topic":
          for (String s : Main.filterTopics) {
            PUIText text = new PUIText(this, s);
            text.addActionListener(puiElement -> {
              Main.filterTopics.remove(s);
              updatePropertyDisplay();
              updateFilteredPresentationList();
            });
            displayables.add(text);
          }
          break;
        default:
          throw new IllegalArgumentException("property not defined");
      }
      propertyDisplay.clearElements();
      propertyDisplay.addAllElements(displayables);
    }
  }

  public void addProperty() {
    if (Main.presentationManager == null || selectedPresentation == null) {
      return;
    }

    if (menu == 0) { // edit-mode

      switch (((PUIText) properties.getElements().get(selectedProperty)).getText()) {
        case "Year" -> {
          // not editable in editor (move file into folder)
        }
        case "Language" -> {
          String newProperty = editProperty(Main.presentationManager.allLanguages, false);
          if (newProperty == null) {
            return;
          }
          Language l = Language.UNDEFINED;

          try {
            l = Language.valueOf(newProperty);
          } catch (IllegalArgumentException e) {
            System.out.println("[UI] :: " + newProperty + " is not a Language!");
          }

          selectedPresentation.setLanguage(l);
          Main.presentationManager.allLanguages.add(l);
          updatePropertyDisplay();
          updateFilteredPresentationList();
        }
        case "Tag" -> {
          editStringProperty(Main.presentationManager.allTags, selectedPresentation.tags());
        }
        case "Topic" -> {
          editStringProperty(Main.presentationManager.allTopics, selectedPresentation.topics());
        }
        default -> throw new IllegalArgumentException("property not defined");
      }

    } else if (menu == 1) { // filter-mode
      switch (((PUIText) properties.getElements().get(selectedProperty)).getText()) {
        case "Year" -> {
          ArrayList<String> values = new ArrayList<>(Main.presentationManager.allYears);

          if (!Main.presentationManager.allYears.isEmpty()) {
            int selected = getUserSelection("Years", values);
            if (selected >= 0) {
              Main.filterYears.add(values.get(selected));
            }
          }
        }
        case "Language" -> {
          ArrayList<String> values = new ArrayList<>();
          Main.presentationManager.allLanguages.forEach(l -> values.add(l.name()));

          if (!values.isEmpty()) {
            int selected = getUserSelection("Languages", values);

            if (selected >= 0) {
              Main.filterLanguages.add(Language.valueOf(values.get(selected)));
            }
          }
        }
        case "Tag" -> {
          ArrayList<String> values = new ArrayList<>(Main.presentationManager.allTags);

          if (!Main.presentationManager.allTags.isEmpty()) {
            int selected = getUserSelection("Tags", values);
            if (selected >= 0) {
              Main.filterTags.add(values.get(selected));
            }
          }
        }
        case "Topic" -> {
          ArrayList<String> values = new ArrayList<>(Main.presentationManager.allTopics);

          if (!Main.presentationManager.allTopics.isEmpty()) {
            int selected = getUserSelection("Topics", values);
            if (selected >= 0) {
              Main.filterTopics.add(values.get(selected));
            }
          }
        }
        default -> throw new IllegalArgumentException("property not defined");
      }
      updatePropertyDisplay();
      updateFilteredPresentationList();
    } else {
      System.err.println("Properties can only be added in edit- and filter-mode!");
    }
  }

  private void editStringProperty(Set<String> allPropertyValues,
      Set<String> presentationPropertyList) {
    String newProperty = editProperty(allPropertyValues, true);
    if (newProperty != null) {
      presentationPropertyList.add(newProperty);
      allPropertyValues.add(newProperty);
      updatePropertyDisplay();
      updateFilteredPresentationList();
    }
  }

  private <T> String editProperty(Set<T> allPropertyValues, boolean createNew) {
    ArrayList<String> userSelection = new ArrayList<>();
    userSelection.add(""); // for new Properties
    allPropertyValues.forEach(e -> {
      userSelection.add(e.toString());
    });
    int propertyIndex = getUserSelection("Select a Property", userSelection);

    if (propertyIndex < 0) { // invalid
      System.err.println("invalid propertyIndex when selecting Tag!");
    } else if (propertyIndex == 0) { // new
      if (createNew) {
        String newProperty = getUserInput("Create a new Tag", "tag");
        if (newProperty == null || newProperty.isBlank() || allPropertyValues.contains(
            newProperty)) {
          System.err.println("[UI] :: Invalid Property : " + newProperty);
        } else {
          return newProperty;
        }
      } else {
        return null;
      }
    } else { // existing
      return userSelection.get(propertyIndex);
    }
    return null;
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

    for (Presentation p : Presentation.getSortedPresentations(
        Main.presentationManager.presentations.values())) {
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
        sendUserInfo("You need to select a Folder!");
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