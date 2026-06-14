package eventplanner;

import eventplanner.view.MainFrame;

// Entry point of the Event Planner application
public class Main {
    public static void main(String[] args) {
        // Swing requires GUI creation on the Event Dispatch Thread
        javax.swing.SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}