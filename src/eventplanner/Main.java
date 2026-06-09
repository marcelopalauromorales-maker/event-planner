package eventplanner;

import eventplanner.view.MainFrame;

// Entry point of the app
public class Main {
    public static void main(String[] args) {
        // Swing requires the GUI to be created on the Event Dispatch Thread
        javax.swing.SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}