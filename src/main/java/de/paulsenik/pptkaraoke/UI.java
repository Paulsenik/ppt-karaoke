package de.paulsenik.pptkaraoke;

import de.paulsenik.jpl.ui.PUIElement;
import de.paulsenik.jpl.ui.core.PUIFrame;
import java.awt.Color;

public class UI extends PUIFrame {

  /**
   * 0 = Settings-Menu 1 = Play-Menu
   */
  public int menu = 0;

  // Buttons
  public PUIElement menuSettingsButton;
  public PUIElement menuPlayButton;

  public UI() {
    initElements();
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

    for (PUIElement e : PUIElement.registeredElements) {
      e.doPaintOverOnHover(false);
      e.doPaintOverOnPress(false);
    }
  }

  @Override
  public void updateElements() {
    super.updateElements();
    menuSettingsButton.setBounds(0, 0, 50, 50);
    menuPlayButton.setBounds(50, 0, 50, 50);
  }

  public void changeMenu(int newMenu) {
    if (newMenu == menu) {
      return;
    }
    menu = newMenu;
    updateElements();
  }


}
