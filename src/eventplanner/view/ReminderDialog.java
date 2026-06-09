package eventplanner.view;

import eventplanner.model.Event;

import javax.swing.*;
import java.awt.*;
import java.util.List;

// Simple popup to show reminders at startup
public class ReminderDialog {
    public static void showReminders(JFrame parent, List<Event> reminders) {
        StringBuilder msg = new StringBuilder();
        msg.append("Upcoming events within the reminder period:\n\n");
        for (Event e : reminders) {
            msg.append("• ").append(e.getTitle())
               .append(" on ").append(e.getDate())
               .append(" at ").append(e.getTime())
               .append(" (reminder ").append(e.getReminderHours()).append("h before)\n");
        }
        JOptionPane.showMessageDialog(parent, msg.toString(), "Reminders", JOptionPane.INFORMATION_MESSAGE);
    }
}