package de.paulsenik.pptkaraoke;

import de.paulsenik.jpl.ui.PUICheckBox;

public class Main {
    public static void main(String[] args) {
        UI ui = new UI();
        PUICheckBox c = new PUICheckBox(ui);
        c.setBounds(100, 100, 400, 400);
    }
}
